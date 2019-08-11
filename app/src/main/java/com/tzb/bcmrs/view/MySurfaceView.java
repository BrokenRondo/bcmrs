package com.tzb.bcmrs.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import com.tzb.bcmrs.util.BitmapHashUtil;
import com.tzb.bcmrs.util.DensityUtil;
import com.tzb.bcmrs.util.DisplayUtil;
import com.tzb.bcmrs.util.SHA1Util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.jar.Attributes;
import android.view.MotionEvent;
import android.widget.Toast;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    //SurfaceHolder实例
    private SurfaceHolder mSurfaceHolder;
    //Canvas对象
    private Canvas mCanvas;
    //控制子线程是否运行
    private boolean startDraw;
    //path实例
    public Path mPath=new Path(   );
    //Paint实例
    public Paint mPaint=new Paint();
    public String key;



    public MySurfaceView(Context context, AttributeSet attrs){
        super(context,attrs);
        initView();
    }
    private void initView(){
        mSurfaceHolder=getHolder();
        mSurfaceHolder.addCallback(this);
        //设置可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常亮
        this.setKeepScreenOn(true);

    }
    @Override
    public void run() {
        while(startDraw){
            draw();
        }
    }

    /*
    * 创建
    */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startDraw=true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        startDraw=false;
    }


    private void draw(){
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvas.drawColor(Color.WHITE);
            mPaint.setStyle(Paint.Style.STROKE);

            mPaint.setStrokeWidth(DensityUtil.px2dip( getContext(),30));
            mPaint.setColor(Color.BLACK);
            mCanvas.drawPath(mPath, mPaint);
        }catch (Exception e){

        }finally {
            //提交画布
            if (mCanvas!=null){
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();    //获取手指移动的x坐标
        int y = (int) event.getY();    //获取手指移动的y坐标
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mPath.moveTo(x, y);
                break;

            case MotionEvent.ACTION_MOVE:

                mPath.lineTo(x, y);
                break;

            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }
    public void reset() {
        mPath.reset();
    }
    public void confirm(){
        Bitmap bitmap = Bitmap.createBitmap(1080,1920, Bitmap.Config.RGB_565);
        //创建新的画布
        Canvas canvas = new Canvas(bitmap);
//设置画布背景色
        canvas.drawColor(Color.WHITE);
//设置绘制路径、画笔
        canvas.drawPath(mPath, mPaint);
        // 保存bimap到sdcard
        FileOutputStream fos = null;
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] bitmapBytes=baos.toByteArray();
        SHA1Util sha1Util=new SHA1Util();
        String str=new String(bitmapBytes);
        String mapHash=sha1Util.SHA1(str);
        key=mapHash;
        String emptyHash="64c203c6cb8be0fb0a3d66be47e0c515b11abb87";
        if (emptyHash.equals(mapHash)){
            Toast.makeText(super.getContext(),"建议输入随机手势提高安全性",Toast.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(super.getContext(),"图片hash值:"+mapHash,Toast.LENGTH_SHORT).show();
            try {
                fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/a.png");
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

    }

}
