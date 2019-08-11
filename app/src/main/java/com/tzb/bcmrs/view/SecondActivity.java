package com.tzb.bcmrs.view;

import android.app.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.tzb.bcmrs.R;
import com.tzb.bcmrs.util.BitmapHashUtil;

public class SecondActivity extends Activity {

    private ImageView img;
    private Bitmap mBitmap;
    private Canvas canvas;
    private Paint paint;
    // 重置按钮
    private Button reset_btn;
    private Button confirm_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keygen2);

        img = (ImageView) findViewById(R.id.img);
        showImage();
        reset_btn = (Button) findViewById(R.id.reset_btn);
        confirm_btn=(Button) findViewById(R.id.confirm_btn);
        reset_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                img.setImageBitmap(null);
                showImage();
            }
        });
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = new File(Environment.getExternalStorageDirectory()+"ZSYLtest",
                        System.currentTimeMillis() + ".jpg");
                OutputStream stream;
                try {
                    stream = new FileOutputStream(file);
                    mBitmap.compress(CompressFormat.JPEG, 100, stream);
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String mapHash=new BitmapHashUtil().getHash(mBitmap);
                Toast.makeText(getApplicationContext(),"图片hash值:"+mapHash,Toast.LENGTH_SHORT).show();

            }
        });
//        ViewGroup.LayoutParams para;
//        para = img.getLayoutParams();
//
//        Log.d(TAG, "layout height0: " + para.height);
//        Log.d(TAG, "layout width0: " + para.width);
//        para.height = 1440;
//        para.width = 1080;


    }

    private void showImage() {
        // 创建一张空白图片
        Display display = getWindowManager().getDefaultDisplay();

        int width= display.getWidth();
        int height=display.getHeight();
        mBitmap = Bitmap.createBitmap(width, height-450, Bitmap.Config.ARGB_8888);
        // 创建一张画布
        canvas = new Canvas(mBitmap);
        // 画布背景为白色
        canvas.drawColor(Color.WHITE);
        // 创建画笔
        paint = new Paint();
        // 画笔颜色为蓝色
        paint.setColor(Color.BLUE);
        // 宽度5个像素
        paint.setStrokeWidth(5);
        // 先将白色背景画上
        canvas.drawBitmap(mBitmap, new Matrix(), paint);
        img.setImageBitmap(mBitmap);

        img.setOnTouchListener(new OnTouchListener() {
            int startX;
            int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 获取手按下时的坐标
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 获取手移动后的坐标
                        int endX = (int) event.getX();
                        int endY = (int) event.getY();
                        // 在开始和结束坐标间画一条线
                        canvas.drawLine(startX, startY, endX, endY, paint);
                        // 刷新开始坐标
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        img.setImageBitmap(mBitmap);
                        break;
                }
                return true;
            }
        });

    }

}

