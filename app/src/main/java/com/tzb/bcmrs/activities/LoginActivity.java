package com.tzb.bcmrs.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tzb.bcmrs.R;
import com.tzb.bcmrs.view.WaitDialog;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.security.MessageDigest;

class Login{
    private String username;
    private String pswd;
    private String req;
    private String res;
    public String result;

    Login(String username1,String pswd1){
        username=username1;
        pswd=pswd1;
        req="login";
    }
}
public class LoginActivity extends BaseActivity {
    private EditText username;
    private EditText passwd;
    private Button login;
    private Button register;
    private String host="192.168.43.82";
    private int port=10068;
    MyHandler myHandler=new MyHandler();
    private WaitDialog mdialog=new WaitDialog();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MD(R.id.mainTitleBar);
        username=(EditText)findViewById(R.id.editText);
        passwd=(EditText)findViewById(R.id.editText2);
        login=(Button)findViewById(R.id.button2);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginThread loginThread=new LoginThread();
                loginThread.start();

//                if (user.equals("user")&&pswd.equals("123456")){
//                    Intent intent  = new Intent(LoginActivity.this,MainActivity.class);
//                    startActivity(intent);
//                }
//                else if (user.equals("new")&&pswd.equals("1234"))
//                {
//                    Intent intent  = new Intent(LoginActivity.this,MainActivity.class);
//                    startActivity(intent);
//                }
//                else
//                {
//                    Toast.makeText(getApplicationContext(),"用户名或密码错误！",Toast.LENGTH_SHORT).show();
//                }
            }
        });

        register=(Button)findViewById(R.id.button_reg);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
    public static final String EXIST = "exist";
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int i=0;
        if (intent != null) {//判断其他Activity启动本Activity时传递来的intent是否为空
            //获取intent中对应Tag的布尔值
            boolean isExist = intent.getBooleanExtra(EXIST, false);
            //如果为真则退出本Activity
            if (isExist) {
                this.finish();
            }
        }
    }
    public class LoginThread extends Thread{
        @Override
        public void run() {
            String user=username.getText().toString();
            String pswd=passwd.getText().toString();
            try {
                Socket client=new Socket(host,port);
                boolean flag=client.isConnected();
                Login login=new Login(user,pswd);
                Gson gson=new Gson();
                String login_json=gson.toJson(login);
               // Toast.makeText(getApplicationContext(),gson.toJson(login),Toast.LENGTH_LONG).show();
                OutputStream ous=client.getOutputStream();
                ous.write(login_json.getBytes());
                InputStream inputStream = client.getInputStream();
                String info = null;
                byte[] res=new byte[8];
                InputStream ins = client.getInputStream();
                DataInputStream dis = new DataInputStream(ins);
                dis.readFully(res);
                String result_str=new String(res);
                if(result_str!=null){
                    System.out.println("我是客户端，服务器说："+info);
                    Login login1=new Gson().fromJson(info,Login.class);
                    if (result_str.substring(0,2).equals("OK")){
                        String id=result_str.substring(2,8).trim();
                        Message msg=new Message();
                        msg.what=1;
                        msg.arg1=Integer.valueOf(id);
                        myHandler.sendMessage(msg);

                    }
                    else
                    {
                        Message msg=new Message();
                        msg.what=2;
                        myHandler.sendMessage(msg);
                    }
                }

            }
            catch (IOException e) {
                //Toast.makeText(getApplicationContext(),"无法连接服务器",Toast.LENGTH_SHORT).show();
                Message msg=new Message();
                msg.what =3;
                myHandler.sendMessage(msg);
            }
            super.run();
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                Intent intent  = new Intent(LoginActivity.this,MainActivity.class);
                String id=String.valueOf( msg.arg1);
                intent.putExtra("ID",id);
                startActivity(intent);
                finish();
                //mDialog.closeDialog();
            }
            if (msg.what==2){
                Toast.makeText(getApplicationContext(),"用户名或密码错误",Toast.LENGTH_SHORT).show();

            }
            if (msg.what==3){
                Toast.makeText(getApplicationContext(),"无法连接服务器",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
