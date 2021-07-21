package com.appdome.overlay.smile;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import static android.view.Gravity.CENTER;

public class WidgetService extends Service {
    int LAYOUT_FLAG;
    View FULL_PARTIAL_VIEW;
    WindowManager windowManager;
    ImageView imageClose;
    TextView tvWidget;
    float height,width;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       boolean SCREEN_STATE = intent.getBooleanExtra("OVERLAY_MODE",false);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            LAYOUT_FLAG= WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else
        {LAYOUT_FLAG= WindowManager.LayoutParams.TYPE_PHONE;
        }
        //set flags for view which define the overlay mode in layout
        FULL_PARTIAL_VIEW = LayoutInflater.from(this).inflate(R.layout.android_overly,null);
        WindowManager.LayoutParams layoutParams =
                new WindowManager.LayoutParams(SCREEN_STATE?WindowManager.LayoutParams.WRAP_CONTENT:WindowManager.LayoutParams.MATCH_PARENT,
                        SCREEN_STATE?WindowManager.LayoutParams.WRAP_CONTENT:WindowManager.LayoutParams.MATCH_PARENT,LAYOUT_FLAG,
                        SCREEN_STATE?WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE:WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                        PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.TOP| CENTER;
        layoutParams.x=0;
        layoutParams.y=100;
        WindowManager.LayoutParams imageParam = new WindowManager.LayoutParams(140,140,LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        imageParam.gravity = Gravity.BOTTOM| CENTER;
        imageParam.y=100;

        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        imageClose = new ImageView(this);
        imageClose.setImageResource(R.drawable.ic_baseline_close_24);
        imageClose.setVisibility(View.INVISIBLE);
        windowManager.addView(imageClose,imageParam);
        windowManager.addView(FULL_PARTIAL_VIEW,layoutParams);
        FULL_PARTIAL_VIEW.setVisibility(View.VISIBLE);
        if(SCREEN_STATE)
        {
            partialDragAndMoveWidget(layoutParams);
        }
        else
        {
            fullOverlayWidget();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(FULL_PARTIAL_VIEW !=null)
        {
            windowManager.removeView(FULL_PARTIAL_VIEW);
        }
        if(imageClose!=null)
        {
            windowManager.removeView(imageClose);
        }
    }
    //drag and move only for partial mode
    private void partialDragAndMoveWidget(WindowManager.LayoutParams layoutParams)
    {
        height = windowManager.getDefaultDisplay().getHeight();
        width = windowManager.getDefaultDisplay().getWidth();
        tvWidget = FULL_PARTIAL_VIEW.findViewById(R.id.smile_overlay);
        tvWidget.setOnTouchListener(new View.OnTouchListener() {
            int initx,inity;
            float initTouchX, initTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        imageClose.setVisibility(View.VISIBLE);
                        initx = layoutParams.x;
                        inity = layoutParams.y;

                        initTouchX = event.getRawX();
                        initTouchY= event.getRawY();

                        return true;
                    case MotionEvent.ACTION_UP:
                        imageClose.setVisibility(View.GONE);
                        layoutParams.x = initx-(int)(initTouchX-event.getRawX());
                        layoutParams.y = inity-(int)(initTouchY-event.getRawY());
                        if(layoutParams.y > (height*0.8) && layoutParams.x >= (CENTER))
                        {
                            stopSelf();
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        layoutParams.x = initx-(int)(initTouchX-event.getRawX());
                        layoutParams.y = inity-(int)(initTouchY-event.getRawY());
                        windowManager.updateViewLayout(FULL_PARTIAL_VIEW,layoutParams);
                        return true;
                }
                return false;
            }
        });
    }
    private void fullOverlayWidget()
    {
        tvWidget = FULL_PARTIAL_VIEW.findViewById(R.id.smile_overlay);
        tvWidget.setTextSize(25);
        tvWidget.setBackground((ContextCompat.getDrawable(this,R.mipmap.android_icon_trans)));
        imageClose.setVisibility(View.VISIBLE);
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });
    }
}
