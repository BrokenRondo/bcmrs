package com.tzb.bcmrs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tzb.bcmrs.R;

public class ShowkeyActivity extends BaseActivity {
    private String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_key);
        MD(R.id.mainTitleBar);
        Intent intent=getIntent();
        key=intent.getStringExtra("KEY");
        TextView textView=(TextView)findViewById(R.id.showkeyTextView);
        textView.setText("生成的密钥为："+key);
        Button button_prev=(Button)findViewById(R.id.button_prev);
        Button button_next=(Button)findViewById(R.id.button_next);
        button_next.setText("下一步");
        button_prev.setText("上一步");
        button_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ShowkeyActivity.this,secretsharingActivity.class);
                intent.putExtra("KEY",key);
                startActivity(intent);
            }
        });


    }
}
