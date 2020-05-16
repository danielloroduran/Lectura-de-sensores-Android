package com.example.danilororysm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LecturaSensores extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectura_sensores);
    }

    public void oyente_btnParar(View view) {

        Intent finalizado = new Intent();
        finalizado.putExtra("resultado", 1);
        setResult(RESULT_OK, finalizado);

        finish();

    }

}
