package com.tzb.bcmrs.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import com.tzb.bcmrs.tools.MyCrypto;
import com.tzb.bcmrs.tools.Vec2;
import com.tzb.bcmrs.util.AESUril;
import com.tzb.bcmrs.util.Base64_2_IntegerUtil;
import com.tzb.bcmrs.R;
import com.tzb.bcmrs.view.WaitDialog;
import org.w3c.dom.Text;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

public class DoSecretSharingActivity extends BaseActivity {
    final int REQ_PICK_IMAGE= 1001;
    private String key;
    private WaitDialog mDialog;
    private String biginteger;
    MyHandler handler=new MyHandler();
    private int sharedNum;
    private int attendNum;
    private Uri IMGURI;
    private ArrayList<Vec2> vec2s;
    private Vec2[] vec2_s;
    private Base64_2_IntegerUtil base64_2_integerUtil=new Base64_2_IntegerUtil();
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dosecretsharing);
        MD(R.id.mainTitleBar);
        vec2s = new ArrayList<Vec2>();
        mDialog=new WaitDialog();
        imageView=(ImageView)findViewById(R.id.imageView3) ;
        final Intent intent=getIntent();
        key=intent.getStringExtra("KEY");
        //textView.setText("密钥分割："+key);
        sharedNum=intent.getIntExtra("SHAREDNUM",3);
        attendNum=intent.getIntExtra("ATTENDNUM",3);
        Base64_2_IntegerUtil base64_2_integerUtil=new Base64_2_IntegerUtil();
        //开启等待对话框和处理线程
        Button selectbutton=(Button )findViewById(R.id.select_btn);
        selectbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri=android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT,uri);
                intent.setType("image/*");
                startActivityForResult(intent, REQ_PICK_IMAGE);
            }
        });
        Button next_btn=(Button)findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getDrawable()==null){
                    Toast.makeText(getApplicationContext(),"请选择图片",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent1=new Intent(DoSecretSharingActivity.this,DoStegActivity.class);
                intent1.putExtra("SHAREDNUM",sharedNum);
                intent1.putExtra("ATTENDNUM",attendNum);
                intent1.putExtra("IMGURI",IMGURI.toString());
                intent1.putExtra("KEY",key);
                startActivity(intent1);
            }
        });
        MyDialogThread myDialogThread=new MyDialogThread();
        myDialogThread.start();

    }

    private class MyDialogThread extends Thread{
        @Override
        public void run() {
            try {

                Message msg=new Message();
                msg.what=1;
                handler.sendMessage(msg);
                //TextView textView=(TextView)findViewById(R.id.textView_cutkey);
                //接下来进行密钥分割
                AESUril aesUril=new AESUril();
                MyCrypto myCrypto=new MyCrypto();
                biginteger=base64_2_integerUtil.StringtoInteger(key);
                int len=biginteger.length();
                BigInteger integerKey=new BigInteger(biginteger);
               // textView.setText("密钥映射为大整数："+biginteger);
                vec2_s  =myCrypto.splitKey(attendNum,sharedNum,integerKey);


                sleep(500);
                Message msg2=new Message();
                msg2.what=2;
                handler.sendMessage(msg2 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
               // mDialog.show(getFragmentManager(),"加载");
               // mDialog.setCancelable(false);
                //mDialog.closeDialog();
            }
            if (msg.what==2){
               // mDialog.dismiss();
//                String dialogString="分割的密钥为：\n";
//                for (int i=0;i<vec2_s.length;i++)
//                {
//                    dialogString+="(";
//                    dialogString+=vec2_s[i].x.toString();
//                    dialogString+=",";
//                    dialogString+=vec2_s[i].y.toString();
//                    dialogString+=")\n";
//                }
//                TextView textView=(TextView)findViewById(R.id.textView_cutkey);
//                textView.setText(dialogString);
                //接下来进行信息隐藏，先要求选择图片,必须为bmp

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQ_PICK_IMAGE){

                switch (resultCode){
                    case RESULT_OK:
                        //data.getData returns the content URI for the selected Image
//                        Uri selectedImage = data.getData();
//                        imageView.setImageURI(selectedImage);
//                        break;
                        try {
                            Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
                            ImageView imageView=(ImageView)findViewById(R.id.imageView3);
                            imageView.setImageBitmap(bitmap);
                            Toast.makeText(this,"已选择图片",Toast.LENGTH_SHORT).show();
                            IMGURI=data.getData();
                        } catch (IOException e) {
                           Toast.makeText(this,"错误的图片",Toast.LENGTH_LONG).show();
                        }
                }

        }
    }
    public void goSelect(View view){
        Uri uri=android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Intent intent=new Intent(Intent.ACTION_PICK_ACTIVITY,uri);
        startActivityForResult(intent, REQ_PICK_IMAGE);
    }
}
