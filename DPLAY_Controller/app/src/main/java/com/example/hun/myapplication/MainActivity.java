package com.example.hun.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int REQUEST_CODE_LED = 101;
    private static final int REQUEST_CODE_SENSOR = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLED = (Button) findViewById(R.id.btnLED);
        Button btnSensor = (Button) findViewById(R.id.btnSensor);

        btnLED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LEDActivity.class);
                startActivityForResult(intent, REQUEST_CODE_LED);
                Toast.makeText(getApplicationContext(), "Button01 Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        btnSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SensorActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SENSOR);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show();
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
