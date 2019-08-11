package com.tzb.bcmrs.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.tzb.bcmrs.R;
import com.tzb.bcmrs.activities.BaseActivity;

public class Listview extends BaseActivity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_info);
        MD(R.id.mainTitleBar);
        final Intent intent=getIntent();
        String hospitalname=intent.getStringExtra("NAME");
        String time=intent.getStringExtra("TIME");
        String text=intent.getStringExtra("TEXT");
        TextView HospitalText=findViewById(R.id.textView6);
        TextView timeView=findViewById(R.id.textView7);
        TextView textView=findViewById(R.id.textView9);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        HospitalText.setText("就诊医院："+hospitalname);
        timeView.setText(time);
        textView.setText(text);
    }
}
