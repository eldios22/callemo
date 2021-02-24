package com.accessibilityexample.ui.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.accessibilityexample.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Map;
import java.util.Set;

public class PassedActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    EditText data, key;
    ImageView icon;
    LinearLayout currentButton;
    int number = 5;
    Boolean firtTime = true;
    LinearLayout[] buttons = new LinearLayout[22];
    LinearLayout new_button;
    SharedPreferences storage;
    SharedPreferences sub_storage;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor sub_editor;
    Map<String, String> map;
    Map<String, Integer> sub_map;
    boolean allow_adding = true;
    boolean is_hide=true;
    LinearLayout buttonLayout;
    RelativeLayout iconlayout;
    TextView key_tv,value_tv;





    LocalBroadcastManager localBroadcastManager;
    BroadcastReceiver clearButtonBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Set<Map.Entry<String, String>> setHashMap = map.entrySet();
            for (Map.Entry<String, String> i : setHashMap) {
                Intent broadcastIntent = new Intent("create-button");

                broadcastIntent.putExtra("value", (String) i.getValue());
                broadcastIntent.putExtra("icon",sub_map.get(i.getValue()));


                localBroadcastManager.sendBroadcast(broadcastIntent);

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        storage = getSharedPreferences("Callemo_Value", Context.MODE_PRIVATE);
        sub_storage = getSharedPreferences("Callemo_icon", Context.MODE_PRIVATE);

        editor = storage.edit();
        sub_editor = sub_storage.edit();
        map = (Map<String, String>) storage.getAll();
        sub_map = (Map<String, Integer>) sub_storage.getAll();

        localBroadcastManager
                = LocalBroadcastManager.getInstance(this);

        setContentView(R.layout.activity_passed);




        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);



        IntentFilter createButtonFilter = new IntentFilter("did-clear-button");
        localBroadcastManager.registerReceiver(
                clearButtonBroadcast, createButtonFilter);


        data = findViewById(R.id.Data);
        key = findViewById(R.id.Key);
        data.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        key.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        data.setEnabled(false);
        key.setEnabled(false);
        key_tv = findViewById(R.id.key_tv);
        value_tv = findViewById(R.id.value_tv);

        TextWatcher keyTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                key_tv.setText(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };
        TextWatcher valueTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                value_tv.setText(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        key.addTextChangedListener(keyTextWatcher);
        data.addTextChangedListener(valueTextWatcher);

        FloatingActionButton updateButton = findViewById(R.id.update);
        FloatingActionButton loadButton = findViewById(R.id.load);
        ImageView addButton = findViewById(R.id.add);
        FloatingActionButton removeButton = findViewById(R.id.remove);
        FloatingActionButton stopButton = findViewById(R.id.stop);
        buttonLayout = findViewById(R.id.buttonslist);
        iconlayout = findViewById(R.id.icons);
        for (int i = 0; i < iconlayout.getChildCount(); i++) {

            View child = iconlayout.getChildAt(i);
            child.setOnClickListener(v ->{
                    String tag = (String)v.getTag();
                    if(!sub_map.containsValue(tag)){
                    int id = getId(tag);
                    icon.setImageResource(id);
                    icon.setTag(id);}
                    else Toast.makeText(this,"Exist",Toast.LENGTH_LONG);
        });



        }
        icon = findViewById(R.id.icon);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });




        updateButton.setOnClickListener(v -> {
            if (currentButton != null ) {
                if ((((!map.containsKey(key.getText().toString())) && (!map.containsValue(data.getText().toString())) ) ||(!sub_map.containsValue((Integer)icon.getTag()))) && (Integer)icon.getTag() != R.drawable.icons_new && !data.getText().equals("New") && !key.getText().equals("Change Value")) {
                    TextView text = (TextView) currentButton.getChildAt(1);
                    ImageView img = (ImageView) currentButton.getChildAt(0);
                    sub_map.remove(map.get(text.getText()));
                    sub_map.put(data.getText().toString(),(Integer)icon.getTag());
                    sub_editor.remove(map.get(text.getText()));
                    sub_editor.putInt(data.getText().toString(),(Integer)icon.getTag());
                    sub_editor.apply();


                    map.remove(text.getText());
                    map.put(key.getText().toString(), data.getText().toString());
                    editor.remove((String) text.getText());
                    editor.putString(key.getText().toString(), data.getText().toString());
                    editor.apply();

                    text.setText(key.getText().toString());
                    img.setImageResource((Integer)icon.getTag());
                    img.setTag((Integer)icon.getTag());

                    if (currentButton == new_button) {
                        allow_adding = true;
                    }
                } else {

                    Toast.makeText(this, "Exist", Toast.LENGTH_LONG).show();
                }

            }else Toast.makeText(this, "Not chosen ", Toast.LENGTH_LONG).show();
        });

        loadButton.setOnClickListener(v -> {
            if (firtTime) {
                Set<Map.Entry<String, String>> setHashMap = map.entrySet();

                for (Map.Entry<String, String> i : setHashMap) {
                    Intent broadcastIntent = new Intent("create-button");
                    broadcastIntent.putExtra("value", (String) i.getValue());
                    broadcastIntent.putExtra("icon",sub_map.get(i.getValue()));
                    localBroadcastManager.sendBroadcast(broadcastIntent);
                    is_hide = false;


                }
                firtTime = false;
            } else {
                Intent broadcastIntent = new Intent("clear-button");
                localBroadcastManager.sendBroadcast(broadcastIntent);
                is_hide = false;

            }


        });
        addButton.setOnClickListener(v -> {
            if (allow_adding && number <= 19) {
                LayoutParams lp = new LayoutParams( LayoutParams.WRAP_CONTENT,150);
                lp.setMargins(30, 0, 0, 0);
                number++;
                if (!map.containsKey("New")) {
                    map.put("New", "Change Value");
                }
                editor.putString("New", "Change Value");
                editor.apply();
                sub_map.put("Change Value", R.drawable.icons_new);
                sub_editor.putInt("Change Value", R.drawable.icons_new);
                sub_editor.apply();
                buttons[number] = createButton("New", R.drawable.icons_new);
                buttonLayout.addView(buttons[number], lp);
                new_button = buttons[number];
                allow_adding = false;
            }
            else if (number <= 19){Toast.makeText(this,"Update new button before",Toast.LENGTH_LONG).show();}
            else {Toast.makeText(this,"Max is 20",Toast.LENGTH_LONG).show();}


        });

        removeButton.setOnClickListener(v -> {
            if (currentButton != null && number != 1) {
                buttonLayout.removeView(currentButton);
                number--;
                TextView text = (TextView) currentButton.getChildAt(1);
                sub_map.remove(map.get(text.getText().toString()));
                sub_editor.remove(map.get(text.getText().toString()));
                sub_editor.apply();
                map.remove(text.getText().toString());
                editor.remove(text.getText().toString());
                editor.apply();
                icon.setImageResource(R.mipmap.yellow);
                key.setText("KeyBOX");
                data.setText("ValueBOX");
                key_tv.setText("Prev Key");
                value_tv.setText("Prev Value");

                key.setEnabled(false);
                data.setEnabled(false);
                if (currentButton == new_button) {
                    allow_adding = true;
                }
                currentButton = null;
            } else if (number != 1) {
                Toast.makeText(this, "No chosen button", Toast.LENGTH_LONG).show();
            } else Toast.makeText(this, "Cannot remove anymore", Toast.LENGTH_LONG).show();


        });
        int number_contents = map.size();
        if (number_contents == 0) {
            String[] keyArray = {"LoveU","Sad","Angry","Shoot","Hello"};
            String[] valueArray = {"(灬♥ω♥灬)","(⌯˃̶᷄ ﹏ ˂̶᷄⌯)ﾟ","ᕙ( ︡’︡益’︠)ง","(⌐▀͡ ̯ʖ▀)=/̵͇̿̿/'̿'̿̿̿ ̿ ̿̿","(≧▽≦)"};
            for (int i = 1; i <= number; i++) {
                LayoutParams lp = new LayoutParams( LayoutParams.WRAP_CONTENT,150);
                lp.setMargins(30, 0, 0, 0);
                int idd=this.getResources().getIdentifier("icons_" + i, "drawable", this.getPackageName());
                buttons[i] = createButton(keyArray[i-1],idd);
                map.put(keyArray[i-1], valueArray[i-1]);
                sub_map.put(valueArray[i-1], idd);
                editor.putString(keyArray[i-1], valueArray[i-1]);
                editor.apply();
                sub_editor.putInt(valueArray[i-1], idd);
                sub_editor.apply();
                buttonLayout.addView(buttons[i], lp);
            }
        } else {
            Set<Map.Entry<String, String>> setHashMap = map.entrySet();
            number =0;
            for (Map.Entry<String, String> i : setHashMap) {
                if (!i.getKey().equals("New")) {
                    number++;
                    LayoutParams lp = new LayoutParams( LayoutParams.WRAP_CONTENT,150);
                    lp.setMargins(30, 0, 0, 0);
                    buttons[number] = createButton(i.getKey(), sub_map.get(i.getValue()));
                    buttonLayout.addView(buttons[number], lp);

                } else {
                    map.remove(i.getKey());
                    sub_map.remove(i.getValue());
                    editor.remove(i.getKey());
                    editor.apply();
                    sub_editor.remove(i.getValue());
                    sub_editor.apply();
                }


            }
        }


    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }



    protected LinearLayout createButton(String name, int id) {
        LinearLayout ln = new LinearLayout(this);
        ln.setOrientation(LinearLayout.HORIZONTAL);


        ln.setGravity(Gravity.CENTER);

        ImageView iv = new ImageView(this);

        LayoutParams iv_param = new LayoutParams(50, 50);
        iv_param.setMargins(20, 0, 70, 0);
        iv.setLayoutParams(iv_param);



        iv.setImageResource(id);

        int color = Color.parseColor("#000000"); //The color u want
        iv.setColorFilter(color);
        iv.setTag(id);

        TextView tv = new TextView(this);
        LayoutParams tv_param = new LayoutParams(LayoutParams.WRAP_CONTENT,50);
        tv_param.setMargins(0, 0, 0, 0);
        tv.setLayoutParams(tv_param);

        tv.setText(name);
        tv.setTextColor(Color.BLACK);


        ln.addView(iv);
        ln.addView(tv);


        ln.setOnClickListener(v -> {

            data.setEnabled(true);
            key.setEnabled(true);

            LinearLayout clickButton = (LinearLayout) v;
            currentButton = clickButton;

            TextView text = (TextView) clickButton.getChildAt(1);
            ImageView img = (ImageView) clickButton.getChildAt(0);
            int tag = (Integer)img.getTag();


            String value = (String) map.get(text.getText());
            data.setText(value);
            key.setText(text.getText());
            icon.setImageResource((Integer)img.getTag());
            icon.setTag((Integer)img.getTag());
            drawerLayout.closeDrawer(GravityCompat.START);


        });
        return ln;


    }
    public int getId(String name){
        return this.getResources().getIdentifier(name, "drawable", this.getPackageName());

    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}