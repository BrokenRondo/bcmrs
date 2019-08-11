package com.tzb.bcmrs.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tzb.bcmrs.R;

public class AuthorizeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize);
        MD(R.id.mainTitleBar);

        String stringExtra = getIntent().getStringExtra("doctorID");

        ImageButton imgBtn = findViewById(R.id.imageButton);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tv = findViewById(R.id.textView11);
        tv.setText("医生ID:" + stringExtra);
    }
}
