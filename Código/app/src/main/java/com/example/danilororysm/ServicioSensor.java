package com.example.danilororysm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ServicioSensor extends Service implements SensorEventListener {

    private final IBinder binder = new LocalBinder();
    private SensorManager mSensorManager;
    private Sensor mSensor;

    static int DATA_MIN_TIME = 1000;
    long lastSaved = System.currentTimeMillis();

    private Socket socket;
    private int PORT = 5000;
    private String SERVER_IP = "";
    PrintWriter out;
    MyTask mt;
    String mensaje = "";


    public ServicioSensor() {
    }

    public void onCreate(){
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        SERVER_IP = intent.getStringExtra("direccion");

        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        return Service.START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if((System.currentTimeMillis() - lastSaved) > DATA_MIN_TIME){
            if(mSensor.getType() == Sensor.TYPE_ACCELEROMETER){
                lastSaved = System.currentTimeMillis();
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                mensaje = "Detectado: X:"+x+" Y: "+y+" Z: "+z+"\n";

                mt = new MyTask();
                mt.execute();

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class LocalBinder extends Binder {
        ServicioSensor getService(){
            return ServicioSensor.this;
        }
    }

    public void unRegister(){
        mSensorManager.unregisterListener(this, mSensor);
    }

    public void register(){
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params){
            try{

                socket = new Socket(SERVER_IP, PORT);
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                out.write(mensaje);
                mensaje = "";
                out.flush();
                out.close();
                socket.close();
                this.cancel(true);
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Error al enviar datos. Revise la IP", Toast.LENGTH_SHORT).show();
                e.printStackTrace();

            }
            return null;
        }



    }

}
