package com.tzb.bcmrs.activities;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.lcw.library.imagepicker.ImagePicker;
import com.tzb.bcmrs.R;
import com.tzb.bcmrs.tools.GlideLoader;
import com.tzb.bcmrs.tools.MyCrypto;
import com.tzb.bcmrs.tools.Vec2;
import com.tzb.bcmrs.util.AESUril;
import com.tzb.bcmrs.util.Base64_2_IntegerUtil;
import com.tzb.bcmrs.util.Formula;
import com.tzb.bcmrs.view.WaitDialog;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.security.Key;
import java.util.ArrayList;
import java.util.Vector;

public class DecodeActivity extends BaseActivity {
    private TextView mTextView;
    MyHandler handler;
    private static final int REQUEST_SELECT_IMAGES_CODE = 0x01;
    private ArrayList<String> mImagePaths;
    private WaitDialog mDialog;
    private Vector<String>KeyPartsPath;
    private Vector<String>KeyPartsStr;//密钥分片Base64编码
    private Vec2[] vec2_s;
    private ArrayList<Vec2> vec2s;
    private String result_str;
    final int pixel=25;
    private Button btn_recover;
    private String keyInteger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decodesteg);
        MD(R.id.mainTitleBar);
        mTextView=(TextView)findViewById(R.id.tv_select_images);
        handler=new MyHandler();
        mDialog=new WaitDialog();
        KeyPartsPath=new Vector<String>();
        KeyPartsStr=new Vector<String>();
        vec2s=new ArrayList<Vec2>();
        btn_recover=(Button)findViewById(R.id.btn_recover);
        findViewById(R.id.bt_select_images).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_recover.setEnabled(true);
                KeyPartsPath.clear();
                KeyPartsStr.clear();
                vec2s.clear();
                ImagePicker.getInstance()
                        .setTitle("标题")//设置标题
                        .showCamera(true)//设置是否显示拍照按钮
                        .showImage(true)//设置是否展示图片
                        .showVideo(true)//设置是否展示视频
                        .setMaxCount(9)//设置最大选择图片数目(默认为1，单选)
                        .setSingleType(true)//设置图片视频不能同时选择
                        .setImagePaths(mImagePaths)//设置历史选择记录
                        .setImageLoader(new GlideLoader())//设置自定义图片加载器
                        .start(DecodeActivity.this, REQUEST_SELECT_IMAGES_CODE);//REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode
            }
        });
        findViewById(R.id.btn_recover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialogThread myDialogThread=new MyDialogThread();
                myDialogThread.start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_IMAGES_CODE && resultCode == RESULT_OK) {
            mImagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("当前选中图片路径：\n\n");
            for (int i = 0; i < mImagePaths.size(); i++) {
                stringBuffer.append(mImagePaths.get(i) + "\n\n");
                KeyPartsPath.add(mImagePaths.get(i));
            }
            TextView tv_path=(TextView)findViewById(R.id.tv_path);
           tv_path.setText(stringBuffer.toString());
        }
    }

    private class MyDialogThread extends Thread{
        @Override
        public void run() {
            try {
                Message msg3=new Message();
                msg3.what=1;
                handler.sendMessage(msg3);
                //接下来恢复密钥
                //首先从图片中提取密钥分片
                Decodeprocessing();
                //然后由密钥分片从(123,45678)解析到Vec2[]
                //vec2_s=new Vec2[KeyPartsStr.size()];

                for (int i=0;i<KeyPartsStr.size();i++){
                    int left=KeyPartsStr.get(i).indexOf("(");
                    int comma=KeyPartsStr.get(i).indexOf(",");
                    int right=KeyPartsStr.get(i).indexOf(")");
                    BigInteger x=new BigInteger(KeyPartsStr.get(i).substring(left+1,comma));
                    BigInteger y=new BigInteger (KeyPartsStr.get(i).substring(comma+1,right));
                    Vec2 t=new Vec2(x,y);
                    vec2s.add(t);
                }
                //然后恢复密钥,BitInteger型
                BigInteger result=MyCrypto.recoverKey(vec2s,vec2s.size());
                result_str=result.toString();

                sleep(10);
                Message msg4=new Message();
                msg4.what=2;
                handler.sendMessage(msg4 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                mDialog.show(getFragmentManager(),"恢复密钥");
                //mDialog.closeDialog();
            }
            if (msg.what==2){
                mDialog.dismiss();
                Base64_2_IntegerUtil base64_2_integerUtil=new Base64_2_IntegerUtil();
                String result=base64_2_integerUtil.Integer2Base64(result_str);
                if (result==null){
                    Toast.makeText(getApplicationContext(),"密钥恢复失败，可能是图片数量不足以恢复密钥",Toast.LENGTH_LONG).show();
                    btn_recover.setEnabled(false);

                }else {
                    Toast.makeText(getApplicationContext(),"恢复的密钥为"+result,Toast.LENGTH_LONG).show();
                }


            }
        }
    }
    private String extractMessage(Bitmap bi) {
        int a,b;

        if (bi.getHeight()<pixel){a = bi.getHeight();}
        else {a = pixel;}

        if (bi.getWidth()<pixel){b = bi.getWidth();}
        else {b = pixel;}

        String extractedText = "";
        Formula sm = new Formula();
        for (int i = 0; i < a; i++) {
            // pass through each row
            for (int j = 0; j < b; j++) {
                // holds the pixel that is currently being processed
                int pixel = bi.getPixel(j, i);
                int R1 = (pixel >> 16) & 0xff;
                int G1 = (pixel >> 8) & 0xff;
                int B1 = (pixel) & 0xff;

                String r1 = Integer.toBinaryString(R1);
                String g1 = Integer.toBinaryString(G1);
                String b1 = Integer.toBinaryString(B1);

                String rr = sm.bintoeightbin(r1);
                String R = rr.substring(7, 8);

                String gg = sm.bintoeightbin(g1);
                String G = gg.substring(7, 8);

                String bb = sm.bintoeightbin(b1);
                String B = bb.substring(7, 8);

                extractedText += R+G+B;

            }
        }

        return extractedText;
    }
    private void Decodeprocessing() throws IOException {
        TextView textView=(TextView)findViewById(R.id.tv_path);
        if (textView.getText().equals("")){
            Toast.makeText(getApplicationContext(),"请选择图片",Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i=0;i< KeyPartsPath.size();i++){//对每一张图都要提取密钥
            Uri uri=getImageStreamFromExternal(getApplicationContext(),KeyPartsPath.get(i));
            Bitmap bi3=MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            Bitmap bi2=bi3.copy(Bitmap.Config.ARGB_8888,true);
            Formula sm=new Formula();

            String hasilExtract=extractMessage(bi2);
            String sb=sm.binarytostring(hasilExtract);
            KeyPartsStr.add(sb);

        }

    }

    public static Bitmap GetBitmap(String path, int w, int h) {//从路径转化为Bitmap
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int) scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(
                BitmapFactory.decodeFile(path, opts));
        return Bitmap.createScaledBitmap(weak.get(), w, h, true);
    }
    public static Uri getImageStreamFromExternal(Context context,String path) {
        Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(mediaUri,
                null,
                MediaStore.Images.Media.DISPLAY_NAME + "= ?",
                new String[]{path.substring(path.lastIndexOf("/") + 1)},
                null);

        Uri uri = null;
        if (cursor.moveToFirst()) {
            uri = ContentUris.withAppendedId(mediaUri,
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
        }
        cursor.close();
        return uri;
    }


}
