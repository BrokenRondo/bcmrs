package com.tzb.bcmrs.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.tzb.bcmrs.R;
import com.tzb.bcmrs.view.WaitDialog;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;

class Register{
    private String regusername;
    private String regpswd;
    private String req;
    Register(String username,String pswd){
        regusername=username;
        regpswd=pswd;
        req="Register";
    }
}
public class RegisterActivity extends BaseActivity {
    private String reg_usr;
    private String reg_pswd;
    MyHandler myHandler=new MyHandler();
    private String host="192.168.43.82";
    private int port=10068;
    private WaitDialog mdialog=new WaitDialog();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        MD(R.id.mainTitleBar);
        final EditText regusername=findViewById(R.id.editText_regusr);
        final EditText regpswd=findViewById(R.id.editText_regpswd);
        Button btn = (Button)findViewById(R.id.button_prev);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.datePicker).setVisibility(View.VISIBLE);
            }
        });
        Button regnow=findViewById(R.id.button_regnow);
        regnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reg_usr=regusername.getText().toString();
                reg_pswd=regpswd.getText().toString();
               RegisterThread registerThread=new RegisterThread();
               registerThread.start();
            }
        });
    }
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                mdialog.dismiss();
                Intent intent=new Intent(RegisterActivity.this,KeygenActivity.class);
                startActivity(intent);
                finish();
            }
            if (msg.what==0){
               mdialog.show(getFragmentManager(),"请稍候");
            }
            if (msg.what==2){
                mdialog.dismiss();
                AlertDialog.Builder alertdialogbuider=new AlertDialog.Builder(RegisterActivity.this);
                alertdialogbuider.setMessage("无法注册，请稍候尝试");
                alertdialogbuider.setCancelable(true);
                AlertDialog alertDialog=alertdialogbuider.create();
                alertDialog.show();

            }
        }
    }
    public class RegisterThread extends Thread{
        @Override
        public void run() {
            Message msg1=new Message();
            msg1.what=0;
            myHandler.sendMessage(msg1);
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Socket socket=new Socket(host,port);
                boolean flag=socket.isConnected();
                socket.setSoTimeout(2000);
                Register register=new Register(reg_usr,reg_pswd);
                Gson gson=new Gson();
                String reg_json=gson.toJson(register);
                OutputStream ous=socket.getOutputStream();
                ous.write(reg_json.getBytes());
                InputStream inputStream = socket.getInputStream();
                //BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String info = null;
                byte[] res=new byte[5];
                InputStream ins = socket.getInputStream();
                DataInputStream dis = new DataInputStream(ins);
                dis.readFully(res);
                String result_str=new String(res);
                if (result_str.equals("REGOK")){
                    Message msg=new Message();
                    msg.what=1;
                    myHandler.sendMessage(msg);
                }else  {
                    Message msg3=new Message();
                    msg3.what=2;
                    myHandler.sendMessage(msg3);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Message msg3=new Message();
                msg3.what=2;
                myHandler.sendMessage(msg3);
            }
        }
    }

}
