package ee.ut.cs.mc.mass.restserver.ble;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class BluetoothServerService extends Service {
    private static final String TAG = BluetoothServerService.class.getName();
    public final static String ACTION_BT_DISABLED = "BT_DISABLED";
    public final static String ACTION_BT_LISTENING_START = "BT_ADVERTISING_START";

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
        new BtServerThread(this).start();

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    /** Thread on which the NanoHttpd Server runs */
    private class BtServerThread extends Thread{
        Context context;
        BtServerThread(Context ctx){
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

                try {
                    mBluetoothController.listenForConnections();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.setAction(ACTION_BT_LISTENING_START);
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
