package com.tzb.bcmrs.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tzb.bcmrs.R;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

public class secretsharingActivity extends BaseActivity {
    private Spinner spDown1;
    private Spinner spDown2;
    private List<String>list1;
    private List<String>list2;
    private ArrayAdapter<String>adapter;
    private String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secretsharing);
        MD(R.id.mainTitleBar);
        spDown1=(Spinner)findViewById(R.id.spinner1);
        spDown2=(Spinner)findViewById(R.id.spinner2);
        Intent intent_recv=getIntent();
        key=intent_recv.getStringExtra("KEY");
        Button button_confirm=(Button)findViewById(R.id.button_sharing_confirm);
        button_confirm.setText("确定");
        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num=spDown1.getSelectedItem().toString().substring(2,3);
                int shared_num=Integer.parseInt(spDown1.getSelectedItem().toString().substring(2,3));
                int attend_num=Integer.parseInt((spDown2.getSelectedItem().toString()).substring(2,3));
                if(attend_num>shared_num){
                    Toast.makeText(getApplicationContext(),"在场人数应小于等于共享人数", Toast.LENGTH_SHORT).show();
                }else  {
                    Intent intent=new Intent(secretsharingActivity.this,DoSecretSharingActivity.class);
                    intent.putExtra("KEY",key);
                    intent.putExtra("SHAREDNUM",shared_num);
                    intent.putExtra("ATTENDNUM",attend_num);
                    startActivity(intent);
                }
            }
        });




    }
}
