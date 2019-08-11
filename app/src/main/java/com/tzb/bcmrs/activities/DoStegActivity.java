package com.tzb.bcmrs.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tzb.bcmrs.R;
import com.tzb.bcmrs.tools.MyCrypto;
import com.tzb.bcmrs.tools.Vec2;
import com.tzb.bcmrs.util.AESUril;
import com.tzb.bcmrs.util.Base64_2_IntegerUtil;
import com.tzb.bcmrs.util.Formula;
import com.tzb.bcmrs.view.WaitDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Random;

public class DoStegActivity extends BaseActivity {

    MyHandler handler=new MyHandler();
    private WaitDialog mDialog=new WaitDialog();;
    private Uri IMGURI;
    private int sharedNum;
    private int attendNum;
    private String key;
    String fname = "";
    final int pixel=25;
    private ArrayList<Vec2> vec2s;
    private Vec2[] vec2_s;
    private String biginteger;
    private Base64_2_IntegerUtil base64_2_integerUtil=new Base64_2_IntegerUtil();
    Double d=0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dosteg);
        MD(R.id.mainTitleBar);
        mDialog.show(getFragmentManager(),"??");
        mDialog.setCancelable(false);
        final Intent intent=getIntent();
        sharedNum=intent.getIntExtra("SHAREDNUM",3);
        attendNum=intent.getIntExtra("ATTENDNUM",3);
        IMGURI=Uri.parse( intent.getStringExtra("IMGURI"));
        key=intent.getStringExtra("KEY");
        TextView textView=(TextView)findViewById(R.id.textView25);
        Button btn_toDecode=(Button)findViewById(R.id.btn_toDecode);
        btn_toDecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DoStegActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        MyDialogThread myDialogThread=new MyDialogThread();
        myDialogThread.start();


    }

    private class MyDialogThread extends Thread{
        @Override
        public void run() {
            try {
                Message msg3=new Message();
                msg3.what=1;
                handler.sendMessage(msg3);
                //接下来先进行密钥分割
                AESUril aesUril=new AESUril();
                MyCrypto myCrypto=new MyCrypto();
                biginteger=base64_2_integerUtil.StringtoInteger(key);
                BigInteger integerKey=new BigInteger(biginteger);
                // textView.setText("密钥映射为大整数："+biginteger);
                vec2_s  =myCrypto.splitKey(attendNum,sharedNum,integerKey);
                //密钥分割完后进行信息隐藏，隐藏到图像里
                for (int i=0;i<vec2_s.length;i++)
                {
                    String keytobeHide="";
                    keytobeHide+="(";
                    keytobeHide+=vec2_s[i].x.toString();
                    keytobeHide+=",";
                    keytobeHide+=vec2_s[i].y.toString();
                    keytobeHide+=")";
                    try {
                        Encodeprocessing(keytobeHide);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                sleep(10);
                Message msg4=new Message();
                msg4.what=2;
                handler.sendMessage(msg4 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){

                //mDialog.closeDialog();
            }
            if (msg.what==2){
                mDialog.dismiss();


            }
        }
    }
    /*进行一次信息隐写的函数*/
    public void Encodeprocessing(String string) throws IOException {
        Bitmap localBitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),IMGURI);
        Bitmap Cover_Image=localBitmap.copy(Bitmap.Config.ARGB_8888,true);

        Formula fm=new Formula();
        String hasil=fm.stringtobinary(string).concat("0000000000000000");
        int pjg_hasil=hasil.length();//隐藏密文的长度
        int total_lsb=fm.sumlsb(Cover_Image);//能进行隐写的长度
        if (total_lsb < pjg_hasil) {//如果图片不能隐藏这个密钥
            Toast.makeText(getApplicationContext(), "Please select another image", Toast.LENGTH_LONG).show();
            return;
        }
        if (Cover_Image == null) {

            Toast.makeText(getApplicationContext(), "Please attach an image", Toast.LENGTH_LONG).show();
            return;
        }
        else {
            Bitmap Stego_Image=insertMessage(hasil);
            SaveImage(Stego_Image);
            //d=fm.hitungPSNR(Cover_Image,Stego_Image);
           // Toast.makeText(getApplicationContext(), "Image Saved :  " + fname, Toast.LENGTH_SHORT).show();
        }


    }

    private Bitmap insertMessage(String encryptedMessage) throws IOException {
        Bitmap bil2= MediaStore.Images.Media.getBitmap(getContentResolver(),IMGURI);
        Bitmap bi1=bil2.copy(Bitmap.Config.ARGB_8888,true);
        int a,b;
        if (bi1.getHeight()<pixel){
            a=bi1.getHeight();
        }
        else {
            a=pixel;
        }
        if(bi1.getWidth()<pixel)
        {
            b=bi1.getWidth();
        }
        else
        {
            b=pixel;
        }
        Formula sm=new Formula();
        int charIndex=0;
        String r3,g3,b3;
        int pjgpesan=encryptedMessage.length();
        for (int i = 0; i < a; i++) {
            // pass through each row
            for (int j = 0; j < b; j++) {
                // holds the pixel that is currently being processed
                int pixel = bi1.getPixel(j, i);
                // Mengubag semua nilai pixel Lsb menjadi 0
                int A = (pixel >> 24) & 0xff;
                int R = (pixel >> 16) & 0xff;
                int G = (pixel >> 8) & 0xff;
                int B = (pixel) & 0xff;
                String r1 = Integer.toBinaryString(R);
                String g1 = Integer.toBinaryString(G);
                String b1 = Integer.toBinaryString(B);

                String rr = sm.bintoeightbin(r1);//menjadi binari dengan pjg 8
                String r2 = rr.substring(0, 7); // mengambil bilai sebanyak 7 dari 8
                String gg = sm.bintoeightbin(g1);
                String g2 = gg.substring(0, 7);
                String bb = sm.bintoeightbin(b1);
                String b2 = bb.substring(0, 7);

                //red
                if (charIndex < pjgpesan) {
                    String PesanR = encryptedMessage.substring(charIndex, charIndex + 1); // index 0, 1 alias indeks ke - 0;
                    if ( Integer.valueOf(PesanR) == 1) {
                        r3 = r2.concat("1"); //mengganti bit paling belakang menjadi 1
                    }
                    else{
                        r3 = r2.concat("0"); //mengganti bit paling belaka menjadi 0
                    }
                    R = sm.binarytointeger(r3); // nilai pixel R baru
                    charIndex++; // char index di tambah sebanyak 1
                }

                //green
                if (charIndex<pjgpesan) {
                    String PesanG = encryptedMessage.substring(charIndex, charIndex + 1); // lnjut dari index atasnya
                    if ( Integer.valueOf(PesanG) == 1) {
                        g3 = g2.concat("1");
                    }
                    else{
                        g3 = g2.concat("0");
                    }
                    G = sm.binarytointeger(g3);
                    charIndex++; //char index di tambah sebanyak 1
                }

                //blue
                if (charIndex<pjgpesan){
                    String PesanB = encryptedMessage.substring(charIndex, charIndex + 1); // lnjut dari index atasnya
                    if ( Integer.valueOf(PesanB) == 1) {
                        b3 = b2.concat("1");
                    }
                    else{
                        b3 = b2.concat("0");
                    }
                    B = sm.binarytointeger(b3);
                    charIndex++; //char index di tambah sebanyak 1
                }

                if (charIndex>=pjgpesan){
                    return bi1;
                }

                int rgba = (A<<24)|(R<<16)|(G<<8)|(B); //gabungkan 3  komponen warna
                bi1.setPixel(j, i, rgba); //settting pixel baru

            }
        }
        return bi1;

    }

    public void SaveImage(Bitmap paramBitmap) {

        File localFile1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Image-Stego");
        localFile1.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        fname = "Singgih-" + n + ".jpg";
        File localFile2 = new File(localFile1, fname);
        scanMedia(localFile2);

        if (localFile2.exists()) {
            localFile2.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(localFile2);
            paramBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scanMedia(File paramFile) {
        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(paramFile)));
    }
}
