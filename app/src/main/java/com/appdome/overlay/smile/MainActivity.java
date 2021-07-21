package com.appdome.overlay.smile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {
    private View parentLayout;
    Button overlayStartButton;
    ToggleButton toggleButtonMode;
    boolean IS_OVERLAY_MODE = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentLayout = findViewById(android.R.id.content);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        overlayStartButton = findViewById(R.id.wigedButton);
        toggleButtonMode = findViewById(R.id.toggleButton);
        getPermission();
        overlayStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Settings.canDrawOverlays(MainActivity.this))
                {
                    getPermission();
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this,WidgetService.class);
                    intent.putExtra("OVERLAY_MODE", IS_OVERLAY_MODE);
                    startService(intent);
                    finish();

                }
            }
        });

    }
    // request for the permission ACTION_MANAGE_OVERLAY_PERMISSION
    public void getPermission()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && !Settings.canDrawOverlays(this))
        {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
            startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            if(!Settings.canDrawOverlays(MainActivity.this)) {
                Snackbar.make(parentLayout, "Permission denied by user",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }
    @SuppressLint("ResourceAsColor")
    // event define the overlay mode partial overlay or full screen.
    public void setOverlayMode(View view)
    {
        if(toggleButtonMode.getText().toString().equals("ON"))
        {
            IS_OVERLAY_MODE =true;
            toggleButtonMode.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.radius_button_on));

        }
        else
        {
            IS_OVERLAY_MODE = false;
            toggleButtonMode.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.radius_button_off));

        }
        Log.i("toggle", toggleButtonMode.getText().toString());
        Log.i("Screen",String.valueOf(IS_OVERLAY_MODE));
    }
}