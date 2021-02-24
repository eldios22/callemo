package com.accessibilityexample.Service;

import android.app.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;


import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.accessibilityexample.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OverlayService extends Service implements OnTouchListener, OnClickListener {
    LocalBroadcastManager localBroadcastManager
            = LocalBroadcastManager.getInstance(OverlayService.this);
    int count = 0;

    Boolean isExist = false;


    Boolean isShown = false;
    View view;
    int mWidth;
    boolean isAllow=true;
    private GestureDetector gestureDetector;
    int x=0;
    int y=200;
    FloatingActionButton methodButton;
    WindowManager.LayoutParams params;



    Map<String, Integer> map = new HashMap<String, Integer>();
    FloatingActionButton[] buttons = new FloatingActionButton[50];
    Context context;


    private WindowManager wm;


    BroadcastReceiver createButtonBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {



            map.put(intent.getExtras().getString("value"), intent.getExtras().getInt("icon"));



            addMethodButton();


        }
    };



    BroadcastReceiver clearButtonBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            removeAllButtons();
            map.clear();
            view.setVisibility(View.VISIBLE);
            wm.updateViewLayout(view,params);
            Intent broadcastIntent = new Intent("did-clear-button");
            localBroadcastManager.sendBroadcast(broadcastIntent);


        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setTheme(R.style.AppTheme);
        context = this;

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        IntentFilter createButtonFilter = new IntentFilter("create-button");

        IntentFilter clearButtonFilter = new IntentFilter("clear-button");
        localBroadcastManager.registerReceiver(createButtonBroadcast, createButtonFilter);
        localBroadcastManager.registerReceiver(clearButtonBroadcast, clearButtonFilter);



    }


    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        return false;
    }

    @Override
    public void onClick(View v) {

        FloatingActionButton button = (FloatingActionButton) v;
        LocalBroadcastManager localBroadcastManager
                = LocalBroadcastManager.getInstance(this);
        Intent myIntent = new Intent("send-input");
        myIntent.putExtra("Value",(String)button.getTag());
        localBroadcastManager.sendBroadcast(myIntent);

    }

    public void addButton(String key, int i) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();


        buttons[count] = new FloatingActionButton(this);
        buttons[count].setImageResource(map.get(key));
        buttons[count].setTag(key);
        buttons[count].setOnTouchListener(this);
        buttons[count].setCustomSize((int)(metrics.density*40));
        buttons[count].setCompatElevation(0);




        buttons[count].setOnClickListener(this);

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        if(x == 0 ){

        params.x=x+(int)(metrics.density*57)+((i)%4)*(int)(metrics.density*57);

        params.y =y+(int)(metrics.density*5.7)+((i)/4)*(int)(metrics.density*57);}
        else {params.x=x-(int)(metrics.density*57)-((i)%4)*(int)(metrics.density*57);

            params.y =y+(int)(metrics.density*5.7)+((i+1)/5)*(int)(metrics.density*57);}





        wm.addView(buttons[count], params);
        count++;


    }

    public void removeAllButtons() {
        for (int i = 0; i <= count - 1; i++) {
            wm.removeView(buttons[i]);
        }

        isShown = false;
        count = 0;

    }

    public void addMethodButton() {
        if (!isExist) {


            view = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);
            methodButton = view.findViewById(R.id.method_button);

            int LAYOUT_FLAG;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
            }

            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.y = 200;





            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            final RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.overlay);
            ViewTreeObserver vto = layout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int width = layout.getMeasuredWidth();

                    //To get the accurate middle of the screen we subtract the width of the android floating widget.
                    mWidth = size.x - width;

                }
            });

            gestureDetector = new GestureDetector(this, new SingleTapConfirm());




            methodButton.setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (gestureDetector.onTouchEvent(event)) {

                        if (!isShown) {
                            isShown = true;
                            Set<Map.Entry<String, Integer>> setHashMap = map.entrySet();
                            int count=0;

                            for (Map.Entry<String, Integer> i : setHashMap) {
                                addButton(i.getKey(),count);
                                count++;
                            }
                        } else  {
                            isShown = false;
                            for (int i = 0; i <= count - 1; i++) {
                                wm.removeView(buttons[i]);
                            }
                            ;
                            count = 0;
                        }
                        return true;
                    }else if(gestureDetector.onTouchEvent(event)) {if(!isShown){view.setVisibility(View.INVISIBLE);wm.updateViewLayout(view,params);}}
                    else
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = params.x;
                            initialY = params.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;
                        case MotionEvent.ACTION_UP:
                            //Only start the activity if the application is in background. Pass the current badge_count to the activity

                            //Logic to auto-position the widget based on where it is positioned currently w.r.t middle of the screen.
                            int middle = mWidth / 2;
                            float nearestXWall = params.x >= middle ? mWidth : 0;
                            params.x = (int) nearestXWall;
                            x=params.x;



                            wm.updateViewLayout(view, params);



                            return true;
                        case MotionEvent.ACTION_MOVE:
                            isAllow= false;


                            if(!isShown) {


                                int xDiff = Math.round(event.getRawX() - initialTouchX);
                                int yDiff = Math.round(event.getRawY() - initialTouchY);


                                //Calculate the X and Y coordinates of the view.
                                params.x = initialX + xDiff;
                                params.y = initialY + yDiff;
                                y=params.y;



                                //Update the layout with new X & Y coordinates
                                wm.updateViewLayout(view, params);

                            }
                            return true;



                    }
                    return false;
                }
            });


            wm.addView(view,params);
            isExist=true;


        }




    }
    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }
    private class LongPressConfirm extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent event) {

            super.onLongPress(event);
        }
    }

}