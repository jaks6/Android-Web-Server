package ee.ut.cs.mc.mass.restserver.ble;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Jakob on 19.03.2015.
 */
public class Sensor {


    private static SensorManager mSensorManager;
    private SensorEventListener mSensorListener;

    float height = 0.0f;


    private Context ctx;



    public Sensor(Context ctx) {
        this.ctx = ctx;
        this.mSensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);

        mSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // when pressure value is changed, this method will be called.
                float pressure_value = 0.0f;

                // if you use this listener as listener of only one sensor (ex,
                // Pressure), then you don't need to check sensor type.
                if (android.hardware.Sensor.TYPE_PRESSURE == event.sensor.getType()) {
                    pressure_value = event.values[0];
                    height = SensorManager.getAltitude(
                            SensorManager.PRESSURE_STANDARD_ATMOSPHERE,
                            pressure_value);
                }

                //MainActivity.this.alti = "Altitude = " + height + " m";
                mSensorManager.flush(mSensorListener);
                mSensorManager.unregisterListener(mSensorListener);

            }

            @Override
            public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

            }
        };
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public float getHeight() {
        return height;
    }

}
