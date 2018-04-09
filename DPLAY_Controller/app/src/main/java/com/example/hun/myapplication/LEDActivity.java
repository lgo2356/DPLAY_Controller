package com.example.hun.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class LEDActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);

        final ToggleButton led02 = (ToggleButton) findViewById(R.id.ledBtn2);

        if(led02.isChecked()) {
            Toast.makeText(getApplicationContext(), "Check!!", Toast.LENGTH_SHORT).show();
        }

        led02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(led02.isChecked()) {
                    Toast.makeText(getApplicationContext(), "02 Checked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "02 Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
