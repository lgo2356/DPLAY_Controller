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
    private static final int REQUEST_CODE_BUZZER = 103;
    private static final int REQUEST_CODE_MOTOR = 104;
    private static final int REQUEST_CODE_BUTTON = 105;
    private static final int REQUEST_CODE_SETTINGS = 106;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLED = (Button) findViewById(R.id.btnLED);
        Button btnSensor = (Button) findViewById(R.id.btnSensor);
        Button btnBuzzer = (Button) findViewById(R.id.btnBuzzer);
        Button btnMotor = (Button) findViewById(R.id.btnMotor);
        Button btnButton = (Button) findViewById(R.id.btnButton);
        Button btnSettings = (Button) findViewById(R.id.btnSettings);

        btnLED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LEDActivity.class);
                startActivityForResult(intent, REQUEST_CODE_LED);
            }
        });

        btnSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SensorActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SENSOR);
            }
        });

        btnBuzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BuzzerActivity.class);
                startActivityForResult(intent, REQUEST_CODE_BUZZER);
            }
        });

        btnMotor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MotorActivity.class);
                startActivityForResult(intent, REQUEST_CODE_MOTOR);
            }
        });

        btnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ButtonActivity.class);
                startActivityForResult(intent, REQUEST_CODE_BUTTON);
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SETTINGS);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
