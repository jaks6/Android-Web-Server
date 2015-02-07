package ee.ut.cs.mc.mass.restserver.ble;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BleAdvertisementService extends Service {
    private static final String TAG = BleAdvertisementService.class.getName();
    public final static String ACTION_BT_DISABLED = "BT_DISABLED";
    public final static String ACTION_BT_ADVERTISING_START = "BT_ADVERTISING_START";

    BluetoothController mBluetoothController;

    @Override
    public void onCreate() {
        // TODO: Write a global flag which marks that this service is running
        mBluetoothController = new BluetoothController(this);
        Log.d(TAG, "onCreate");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        new BleAdvertisementThread(this).start();

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    /** Thread on which the NanoHttpd Server runs */
    private class BleAdvertisementThread extends Thread{
        Context context;
        BleAdvertisementThread(Context ctx){
            this.context = ctx;
        }
        @Override
        public void run() {
            if (!mBluetoothController.checkBtEnabled()) {
                Intent intent = new Intent();
                intent.setAction(ACTION_BT_DISABLED);
                sendBroadcast(intent);

                stopSelf();
            } else {
                Log.i(TAG, "Starting bt advertisement");

                mBluetoothController.advertiseIp("192.168.1.1");

                Intent intent = new Intent();
                intent.setAction(ACTION_BT_ADVERTISING_START);
                sendBroadcast(intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "BLE service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
