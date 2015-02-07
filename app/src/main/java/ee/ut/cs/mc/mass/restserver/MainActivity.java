package ee.ut.cs.mc.mass.restserver;

import android.bluetooth.BluetoothAdapter;
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

import ee.ut.cs.mc.mass.restserver.ble.BleAdvertisementService;


public class MainActivity extends ActionBarActivity {

    /** Web Server port */
    public static final int PORT = 8765;
    private static final int REQUEST_ENABLE_BT = 1;

    WebServerReceiver webServerReceiver;
    BleReceiver bleReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume(){
        super.onResume();

        //Register BroadcastReceivers
        //to receive events from our services
        setUpBroadcastReceivers();
    }

    private void setUpBroadcastReceivers() {
        webServerReceiver = new WebServerReceiver();
        bleReceiver = new BleReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NanoHttpdService.ACTION_UI_MSG);
        registerReceiver(webServerReceiver, intentFilter);

        IntentFilter intentFilterBle = new IntentFilter();
        intentFilterBle.addAction(BleAdvertisementService.ACTION_BT_DISABLED);
        intentFilterBle.addAction(BleAdvertisementService.ACTION_BT_ADVERTISING_START);
        registerReceiver(bleReceiver,intentFilterBle);
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

    public void buttonStartBleAdvertisingClicked(View v){
        Intent intent = new Intent(this, BleAdvertisementService.class);
        startService(intent);
    }

    public void buttonStopBleAdvertisingClicked(View v){
        Intent intent = new Intent(this, BleAdvertisementService.class);
        stopService(intent);
    }

    private class WebServerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String datapassed = arg1.getStringExtra("MESSAGE");

            TextView textIpaddr = (TextView) findViewById(R.id.textView);
            textIpaddr.setText(datapassed);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bleReceiver);
        unregisterReceiver(webServerReceiver);
    }

    private class BleReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String broadcastAction = arg1.getAction();

            if (broadcastAction.equals(BleAdvertisementService.ACTION_BT_ADVERTISING_START)){
                Toast.makeText(MainActivity.this, "BLE advertising started", Toast.LENGTH_LONG).show();

            } else if (broadcastAction.equals(BleAdvertisementService.ACTION_BT_DISABLED)){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            }
        }
    }
}
