package com.tzb.bcmrs.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.activity.CaptureActivity;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.tzb.bcmrs.R;
import com.tzb.bcmrs.tools.MyCrypto;
import com.tzb.bcmrs.tools.MySocket;
import com.tzb.bcmrs.tools.Vec2;
import com.tzb.bcmrs.util.Constant;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class QRCodeActivity extends BaseActivity {

    private EditText editText1;
    private EditText editText2;
    private ArrayList<Vec2> vec2s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        MD(R.id.mainTitleBar);

        editText1 = this.findViewById(R.id.qrc_editText1);
        editText2 = this.findViewById(R.id.qrc_editText2);
        vec2s = new ArrayList<Vec2>();
        Button reckey=findViewById(R.id.button_recoverkey);
        reckey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(QRCodeActivity.this,DecodeActivity.class);
                startActivity(intent);
            }
        });
        Button btn1 = findViewById(R.id.qrc_button1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String count = editText1.getText().toString().trim();
                if (TextUtils.isEmpty(count)) {
                    Toast("请输入内容");
                    return;
                }

                int requsetLeast = 3;
                int numberDivided = 6;
                //md5加密
                BigInteger bigInt = MyCrypto.MD5(count);

                Toast(bigInt.toString());
                //密钥分割
                Vec2[] v =  MyCrypto.splitKey_static(requsetLeast,numberDivided,bigInt);
                Bitmap[] bmp = new Bitmap[numberDivided];
                for(int i = 0;i<numberDivided;i++)
                {
                    String str = "k:" + v[i].x + "," + v[i].y;
                    bmp[i] = generateBitmap(str,600,600);
                }

                new SendBmpThread(MySocket.HOST,8001,bmp[0]).start();
                new SendBmpThread(MySocket.HOST,8002,bmp[1]).start();
                new SendBmpThread(MySocket.HOST,8003,bmp[2]).start();
                new SendBmpThread(MySocket.HOST,8004,bmp[3]).start();
                new SendBmpThread(MySocket.HOST,8005,bmp[4]).start();
                new SendBmpThread(MySocket.HOST,8006,bmp[5]).start();

            }
        });

        Button btn2 = findViewById(R.id.qrc_button2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QRCodeActivity.this, CaptureActivity.class);
                startActivityForResult(intent, Constant.REQ_QR_CODE);
            }
        });
    }

    //扫码结束回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);

            String str = editText2.getText().toString().trim();
            switch (scanResult.charAt(0))
            {
                case 'k':
                    vec2s.add(new Vec2(scanResult.substring(2)));
                    break;
            }
            if(vec2s.size() == 3)
            {
                try{
                    if(MyCrypto.MD5(str).equals(MyCrypto.recoverKey(vec2s,3)))
                        Toast("校验成功");
                    else
                        Toast(MyCrypto.MD5(str) + " " + MyCrypto.recoverKey(vec2s,3) + " 校验失败");
                } catch(ArithmeticException e){
                    Toast("运算出错，校验失败");
                }
                vec2s.clear();
            }


        }
    }

    private Bitmap generateBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, (Hashtable) hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x101:
                    Toast(msg.obj.toString() + "：完成");
                    break;
            }
        }
    };
    /**
     * 加载数据的线程
     */
    class SendBmpThread extends  Thread{

        Bitmap bmp;
        String host;
        int port;

        public SendBmpThread(String host,int port,Bitmap bmp)
        {
            this.bmp = bmp;
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {

            MySocket.sendBmp(host,port,bmp);

            Message msg = new Message();
            msg.obj = port;
            msg.what = 0x101;
            handler.sendMessage(msg);//通过handler发送一个更新数据的标记
        }
    }

}
