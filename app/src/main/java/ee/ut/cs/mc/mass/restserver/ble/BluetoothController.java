package ee.ut.cs.mc.mass.restserver.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.ParcelUuid;

import java.util.UUID;

/**
 * Created by Jakob on 7.02.2015.
 */
public class BluetoothController {
    private Context context;
    private BluetoothAdapter mBluetoothAdapter;

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

    public void advertiseIp(String ip){
        BluetoothLeAdvertiser advertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(true)
                .build();


        AdvertiseData data = new AdvertiseData.Builder()
                .addServiceData(
                        new ParcelUuid(UUID.fromString("webserver")),
                        "webserver".getBytes())
                .addServiceUuid(
                        new ParcelUuid(UUID.fromString(ip)))
                .build();
        AdvertiseCallback callback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }
        };
        advertiser.startAdvertising(settings,data,callback);
    }
}
