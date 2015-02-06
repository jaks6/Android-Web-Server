package ee.ut.cs.mc.mass.restserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    /** Web Server port */
    public static final int PORT = 8765;

    WebServerReceiver webServerReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume(){
        super.onResume();

        //Register BroadcastReceiver
        //to receive event from our service
        webServerReceiver = new WebServerReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NanoHttpdService.ACTION_UI_MSG);
        registerReceiver(webServerReceiver, intentFilter);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonStartServiceClicked(View v){
        Intent intent = new Intent(this, NanoHttpdService.class);
        intent.putExtra("port", PORT);
        startService(intent);
    }

    public void buttonStopServiceClicked(View v){
        Intent intent = new Intent(this, NanoHttpdService.class);
        stopService(intent);
    }

    private class WebServerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String datapassed = arg1.getStringExtra("MESSAGE");

            Toast.makeText(MainActivity.this,
                    "Web Server started!",
                    Toast.LENGTH_LONG).show();
            TextView textIpaddr = (TextView) findViewById(R.id.textView);
            textIpaddr.setText(datapassed);
        }
    }
}
