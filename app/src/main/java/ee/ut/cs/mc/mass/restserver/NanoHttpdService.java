package ee.ut.cs.mc.mass.restserver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class NanoHttpdService extends Service {
    private static final String TAG = NanoHttpdService.class.getName();

    final static String ACTION_UI_MSG = "UI_MSG";

    private NotificationManager mNM;
    private static final int mNotificationId = 001;


    private RestServer server;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        // TODO: Write a global flag which marks that this service is running
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();

        Log.d(TAG, "onCreate");
    }


    /** Thread on which the NanoHttpd Server runs */
    private class NanoHttpdThread extends Thread{
        Context context;
        int port;
        NanoHttpdThread(Context ctx,int port){
            this.context = ctx;
            this.port = port;
        }
        @Override
        public void run() {
            server = new RestServer(context, port);
            try {
                server.start();

                Intent intent = new Intent();
                intent.setAction(ACTION_UI_MSG);
                String ip = Util.getIpAddress();
                intent.putExtra("MESSAGE", "Server IP: http://" + ip + ":" + MainActivity.PORT);

                sendBroadcast(intent);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        final int port = intent.getIntExtra("port", 8080);
        new NanoHttpdThread(this, port).start();

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }



    @Override
    public void onDestroy() {
        // TODO: Change the global flag which indicates whether this service is running
        server.stop();

        //Quit showing device IP in UI
        Intent intent = new Intent();
        intent.setAction(ACTION_UI_MSG);
        intent.putExtra("MESSAGE", "Server stopped");
        sendBroadcast(intent);

        // Cancel the persistent notification.
        mNM.cancel(mNotificationId);
        Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        //Notification click behaviour
        Intent resultIntent = new Intent(this, MainActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_service)
                        .setContentTitle(getString(R.string.notification_title))
                        .setContentText(getString(R.string.notification_running_desc))
                        .setContentIntent(resultPendingIntent);


        Notification n = mBuilder.build();
        n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        // Send the notification.
        mNM.notify(mNotificationId, n);
    }

}
