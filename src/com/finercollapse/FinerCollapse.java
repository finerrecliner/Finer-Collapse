package com.finercollapse;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class FinerCollapse extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("Hello World!");
        setContentView(tv);
        //setContentView(R.layout.main);
    }
}