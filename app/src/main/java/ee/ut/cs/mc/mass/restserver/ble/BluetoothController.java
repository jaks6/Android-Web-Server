package ee.ut.cs.mc.mass.restserver.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * Created by Jakob on 7.02.2015.
 */
public class BluetoothController {

    private static final ParcelUuid DATA_UUID = ParcelUuid.fromString("fb579746-84c5-4fff-a1e9-434e609ff998");
    private static final ParcelUuid UUID_HTTP = ParcelUuid.fromString("0000000C-0000-1000-8000-00805F9B34FB");
    private static final ParcelUuid UUID_IP = ParcelUuid.fromString("00000009-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid UUID_IP_MANUAL =
            ParcelUuid.fromString("00000009-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid UUID_HTTP_MANUAL =
            ParcelUuid.fromString("0000000C-0000-1000-8000-00805F9B34FB");
    //    private static final ParcelUuid UUID_IP = Util.createParcelUuidFromShortUuid("0x0009");
    private static final String TAG = BluetoothController.class.getName();

    private Context context;
    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothLeAdvertiser mAdvertiser;
    private BluetoothLeScanner mScanner;
    private AdvertiseCallback mCallback;

    public BluetoothController(Context context) {

        this.context = context;
        initBtAdapter();
    }

    private void initBtAdapter(){
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    /** Returns a boolean indicating whether bluetooth radio is enabled on the phone */
    public boolean checkBtEnabled(){
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }


    /** A test method for reading BLE advertisements and logging them */
    public void readAdvertisements(){
        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        ScanCallback mLeScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                ScanRecord record = result.getScanRecord();
                if (record != null){

                    Map<ParcelUuid, byte[]> serviceDataMap =  record.getServiceData();
                    Iterator<Entry<ParcelUuid, byte[]>> entryIterator =  serviceDataMap.entrySet().iterator();

                    while (entryIterator.hasNext()){
                        Entry<ParcelUuid, byte[]> e = entryIterator.next();

                        Log.d(TAG, "scanRecord[" + record.getDeviceName() + "] = " + e.getKey() + ";||;" + new String(e.getValue()));
                    }

                };
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };


        Log.d(TAG, "--------START SCAN ----------------------------");
        mScanner.startScan(mLeScanCallback);
        try {
            Thread.sleep(1500);
            Log.d(TAG, "--------STOP SCAN ------------------------------");
            mScanner.stopScan(mLeScanCallback);
            Thread.sleep(9500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopAdvertising(){
        if (mAdvertiser != null) mAdvertiser.stopAdvertising(mCallback);
    }

    public void advertiseIp(String ip){
        mAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        mCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }
            @Override
            public void onStartFailure(int result) {
                if (result == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                    Log.d(TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
                }
                else if(result == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS){
                    Log.d(TAG, "Failed to start advertising because no advertising instance is available.");
                }
                else if(result == ADVERTISE_FAILED_ALREADY_STARTED){
                    Log.d(TAG, "Failed to start advertising as the advertising is already started.");
                }
                else if(result == ADVERTISE_FAILED_INTERNAL_ERROR){
                    Log.d(TAG, "Operation failed due to an internal error.");
                }
                else if(result == ADVERTISE_FAILED_FEATURE_UNSUPPORTED){
                    Log.d(TAG, "This feature is not supported on this platform.");
                }
                else {
                    Log.d(TAG, "There was unknown error.");
                }
            }
        };
        AdvertiseSettings settings = getAdvertiseSettings();
        AdvertiseData data = getAdvertisementData();
        Log.d(TAG, "UUID _HTTP: + "+ UUID_HTTP.toString());
        Log.d(TAG, "UUID IP: + "+ UUID_IP.toString());
        AdvertiseData scanResponse = getScanResponseData(UUID_HTTP,ip);
        mAdvertiser.startAdvertising(settings,data, scanResponse, mCallback);
    }




    public void listenForConnections() throws IOException {
        BluetoothServerSocket serverSocket = mBluetoothAdapter
                .listenUsingInsecureRfcommWithServiceRecord("Mobile Host", UUID.randomUUID());
        BluetoothSocket btSocket = serverSocket.accept();
        Log.d("TAG", "BT SOCKET ESTABLISHED");
    }

    /** This is what the device is continously advertising */
    private AdvertiseData getAdvertisementData() {
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.setIncludeTxPowerLevel(false); // reserve advertising space for URI
//        builder.addServiceData(DATA_UUID, "192.168.134.244:8765".getBytes());
        return builder.build();
    }

    /** This is what the device will send in response to a scan response request
     * after a scanning device has seen the advertise data */
    private AdvertiseData getScanResponseData(ParcelUuid uuid, String data) {
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.setIncludeTxPowerLevel(false); // reserve advertising space for URI

        builder.addServiceData(uuid, data.getBytes(StandardCharsets.UTF_8));
        Log.d(TAG, "DATA="+data+" , length in UTF_8=" + data.getBytes(StandardCharsets.UTF_8).length);
        return builder.build();
    }

    private AdvertiseSettings getAdvertiseSettings() {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        builder.setConnectable(false);
        return builder.build();
    }
}
