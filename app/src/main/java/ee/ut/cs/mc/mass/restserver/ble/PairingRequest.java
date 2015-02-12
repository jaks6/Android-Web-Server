package ee.ut.cs.mc.mass.restserver.ble;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PairingRequest extends BroadcastReceiver {
    private static final String TAG = PairingRequest.class.getName();

    @Override
    public void onReceive(Context context, Intent intent){
        if (intent.getAction().equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            new Thread(new Runnable() {
                public void run() {
                    device.setPairingConfirmation(true);
                    try {
                        Log.d(TAG, "Start Pairing...");
                        Method m = device.getClass().getMethod("createBond", (Class[]) null);
                        m.invoke(device, (Object[]) null);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

//            byte[] pinBytes = BluetoothDevice.convertPinToBytes("1234");

//            device.createInsecureRfcommSocketToServiceRecord()
//            device.setPin(pinBytes);
        }
    }
}