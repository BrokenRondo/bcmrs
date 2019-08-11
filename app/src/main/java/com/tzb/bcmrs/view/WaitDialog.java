package com.tzb.bcmrs.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tzb.bcmrs.R;
import com.tzb.bcmrs.util.DisplayUtil;

public class WaitDialog extends DialogFragment {
    private TextView tip;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , Bundle savedInstanceState)
    {
        // 创建View, 指定布局XML
        View view = inflater.inflate(R.layout.wait_dialog, container);
        // 点击按钮时，关闭对话框


        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // 当对话框显示时，调整对话框的窗口位置
        Window window = getDialog().getWindow();
        if (window != null)
        {
            window.setBackgroundDrawable( new ColorDrawable(Color.WHITE));
            window.setWindowAnimations(R.style.dialog);
            // 设置对话框的窗口显示
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount=0.3f; // 背景灰度
            lp.gravity = Gravity.CENTER; // 靠下显示
            int wid= new DisplayUtil().getScreenWidth(this.getActivity());
            int height=new DisplayUtil().getScreenHeight(this.getActivity());
//            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.width=wid-100;
            window.setAttributes(lp);
        }
    }
    /**
     * 关闭dialog
     *
     * http://blog.csdn.net/qq_21376985
     *
     */
    public void closeDialog() {
           dismiss();
    }
    public void SetText(String text){

    }
}
