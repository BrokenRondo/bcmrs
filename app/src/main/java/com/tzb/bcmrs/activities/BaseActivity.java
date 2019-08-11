package com.tzb.bcmrs.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.reflect.Field;

public class BaseActivity extends Activity {

    //动态的设置状态栏 ，实现沉浸式状态栏
    protected void MD(int id)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            LinearLayout linear_bar = (LinearLayout) findViewById(id);
            linear_bar.setVisibility(View.VISIBLE);
            //获取到状态栏的高度
            int statusHeight = getStatusBarHeight();
            //动态的设置隐藏布局的高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linear_bar.getLayoutParams();
            params.height = statusHeight;
            linear_bar.setLayoutParams(params);
        }
    }

    //获取状态栏高度
    private int getStatusBarHeight()
    {
        try
        {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    //屏幕宽
    protected int getSrcWidth()
    {
        return getResources().getDisplayMetrics().widthPixels;
    }
    //屏幕高
    protected int getSrcHeight()
    {
        return getResources().getDisplayMetrics().heightPixels;
    }

    //Toast提示
    protected void Toast(String text)
    {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    //打印log
    protected static void log(String msg) {
        Log.i("掌上医疗", "===============================================================================");
        Log.i("掌上医疗", msg);
    }

    //打印loge
    protected static void loge(String msg) {
        Log.e("掌上医疗", "===============================================================================");
        Log.e("掌上医疗", msg);
    }


}
