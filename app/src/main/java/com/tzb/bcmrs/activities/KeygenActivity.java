package com.tzb.bcmrs.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import com.tzb.bcmrs.R;
import com.tzb.bcmrs.util.AESUril;
import com.tzb.bcmrs.view.MySurfaceView;
import com.tzb.bcmrs.view.WaitDialog;

import static java.lang.Thread.sleep;

public class KeygenActivity extends BaseActivity {

    private Context context;
    private Button reset_btn;
    private Button confirm_btn;
    private MySurfaceView mview;
    private WaitDialog mDialog;
    private String key="";
    private AESUril aesUril;
    MyHandler handler=new MyHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        setContentView(R.layout.activity_keygen);
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        MD(R.id.mainTitleBar);
        context=this;
        mDialog=new WaitDialog();
        mview = (MySurfaceView) findViewById(R.id.MySurfaceView);
        reset_btn = (Button) findViewById(R.id.reset_btn);
        reset_btn.setOnClickListener(new View.OnClickListener() {

            //@Override
            public void onClick(View v) {
                // 清除
                mview.reset();
            }
        });
        confirm_btn=(Button)findViewById(R.id.confirm_btn);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_btn.setEnabled(false);//防止快速双击闪退
                mview.confirm();
                //计算出AES密钥并跳转到下一个页面
                //加载的时候弹出转圈圈
                //mDialog= WaitDialog.createLoadingDialog(KeygenActivity.this,"正在生成密钥");
                MyDialogThread myDialogThread=new MyDialogThread();
                myDialogThread.start();

                //confirm_btn.setEnabled(true);
//                try {
//                    sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }


            }
        });

       // startActivity(new Intent(context, SecondActivity.class));


    }
    private class MyDialogThread extends Thread{
        @Override
        public void run() {

            try {

                Message msg=new Message();
                msg.what=1;
                handler.sendMessage(msg);
                sleep(500);
                Message msg2=new Message();
                msg2.what=2;
                handler.sendMessage(msg2 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //接下来计算AES
            AESUril aesUril=new AESUril();

            try {
                key=aesUril.geneKey(mview.key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (! key.isEmpty()){
                // Toast.makeText(getApplicationContext(),"密钥为:"+key,Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"生成密钥出错！请重启程序",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                mDialog.show(getFragmentManager(),"加载");
                mDialog.setCancelable(false);
                //mDialog.closeDialog();
            }
            if (msg.what==2){
                mDialog.dismiss();
                confirm_btn.setEnabled(true);
                Intent intent=new Intent(KeygenActivity.this,ShowkeyActivity.class);
                intent.putExtra("KEY",key);
                startActivity(intent);
            }
        }
    }
}
