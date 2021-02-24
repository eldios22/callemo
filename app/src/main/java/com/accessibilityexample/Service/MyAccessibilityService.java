package com.accessibilityexample.Service;

import android.accessibilityservice.AccessibilityService;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import android.util.Log;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MyAccessibilityService extends AccessibilityService {
    AccessibilityNodeInfo currentView;
    AccessibilityEvent currentEvent;
    String currentText;
    LocalBroadcastManager localBroadcastManager
            = LocalBroadcastManager.getInstance(MyAccessibilityService.this);


    BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            sendInput(extras.getString("Value"));

        }
    };








    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        IntentFilter myFilter = new IntentFilter("send-input");

        localBroadcastManager.registerReceiver(
                myBroadcastReceiver, myFilter);

    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();
        int type = event.getEventType();

            if (source!= null && event.getClassName().equals("android.widget.EditText") && (type == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED || type == AccessibilityEvent.TYPE_VIEW_FOCUSED) ) {
                currentEvent = event;
                currentView = source;
                String text = currentEvent.getText().toString();
                currentText = text.substring(1,text.length()-1);


        }
    }
    @Override
    public void onInterrupt() {
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(
                myBroadcastReceiver);

    }
    public void sendInput(String value){
        if(currentEvent!=null){
        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo
                .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,currentText + value);
        currentText += value;

        currentView.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);}
    }





}
