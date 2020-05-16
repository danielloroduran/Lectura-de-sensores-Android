package com.example.danilororysm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText txtIP;

    private static final int BTN_REQUEST = 1;

    private ServicioSensor mService;
    private boolean mBound = false;
    private boolean pulsado = false;
    Intent intent;

//    Intent mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtIP = findViewById(R.id.txtIP);

        intent = new Intent(this, ServicioSensor.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void onClickIniciar(View view){


        String direccion = txtIP.getText().toString();

        if(direccion.equals("")){
            intent.putExtra("direccion", "10.0.2.2");
        }else{
            intent.putExtra("direccion", direccion);
        }

        if(!pulsado){
            if(mBound) {
                mService.startService(intent);
            }
            Intent i = new Intent(this, LecturaSensores.class);
            startActivityForResult(i, BTN_REQUEST);
        }
        else if(pulsado){
            if(mBound) {
                mService.register();
                mService.startService(intent);
            }
            Intent i = new Intent(this, LecturaSensores.class);
            startActivityForResult(i, BTN_REQUEST);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServicioSensor.LocalBinder binder = (ServicioSensor.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };




    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == BTN_REQUEST){
            if(resultCode == RESULT_OK){
                mService.unRegister();
                mService.stopService(intent);

            }
        }
    }




}
