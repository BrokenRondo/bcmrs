package com.tzb.bcmrs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.activity.CaptureActivity;
import com.tzb.bcmrs.R;
import com.tzb.bcmrs.tools.Data;
import com.tzb.bcmrs.tools.MySocket;
import com.tzb.bcmrs.util.Constant;
import com.tzb.bcmrs.view.Listview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

class Item_data{
    public String name;
    public String time;
    public String text;
}
public class MainActivity extends BaseActivity implements AbsListView.OnScrollListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView lv;
    private List<Map<String,Object>> list;
    private List<Item_data>list_item_data;
    private View footerView;
    private SimpleAdapter simpleAdapter;
    private ImageButton img_btn1;
    private ImageButton img_btn2;
    //private Listview listview;
    private int id;
    private int visibleLastIndex;//用来可显示的最后一条数据的索引
    int startId = 0,endId = 0;
    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                //finish();
                //Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                //传递退出所有Activity的Tag对应的布尔值为true
                //intent.putExtra(LoginActivity.EXIST, true);
                //启动BaseActivity
               // startActivity(intent);
                finish();
                //System.exit(0);

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MD(R.id.mainTitleBar);
        final Intent intent=getIntent();
        String id_s=intent.getStringExtra("ID");
        this.id=Integer.valueOf(id_s);
        list_item_data=new ArrayList<>();
        //扫码页面跳转
        img_btn1 = findViewById(R.id.imageButton);
        img_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get Camera parameters
                startQrCode();
            }
        });

        img_btn2 = findViewById(R.id.imageButton2);
        img_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,   QRCodeActivity.class);
                startActivity(intent);
            }
        });

        //获取控件
        swipeRefreshLayout = findViewById(R.id.main_srl);
        lv = findViewById(R.id.main_lv);

        //添加尾部控件
        footerView = getLayoutInflater().inflate(R.layout.layout_footer,null);
        lv.addFooterView(footerView);

        //滑动监听
        lv.setOnScrollListener(this);

        //ArrayList适配器
        list = new ArrayList();
        simpleAdapter = new SimpleAdapter(this,list,R.layout.list_item,
                new String[]{"name","time","summary"},new int[]{R.id.textView3,R.id.textView4,R.id.textView5});
        lv.setAdapter(simpleAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String hospitalName=list_item_data.get(list_item_data.size()-1- position).name;
                String time=list_item_data.get(list_item_data.size()-1- position).time;
                String text=list_item_data.get(list_item_data.size()-1- position).text;

                Intent intent1=new Intent(MainActivity.this,Listview.class);
                intent1.putExtra("NAME",hospitalName);
                intent1.putExtra("TIME",time);
                intent1.putExtra("TEXT",text);
                startActivity(intent1);
            }
        });




        //滑动控件外观设置
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        //下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new LoadDataThread(true).start();
            }
        });

        new LoadDataThread(true).start();
    }

    // 开始扫码
    private void startQrCode() {
        /*if (Context.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(MainActivity.this, "请至权限中心打开本应用的相机访问权限", Toast.LENGTH_LONG).show();
            }
            // 申请权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.REQ_PERM_CAMERA);
            return;
        }*/
        // 二维码扫码
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, Constant.REQ_QR_CODE);
    }
    //扫码结束回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (requestCode == Constant.REQ_QR_CODE && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
            //将扫描出的信息显示出来

            switch (scanResult.charAt(0))
            {
                case 'd':
                    Intent intent = new Intent();
                    intent.putExtra("doctorID",scanResult.substring(2));
                    intent.setClass(MainActivity.this,AuthorizeActivity.class);
                    startActivity(intent);
                    break;

                default:
                    Toast(scanResult);
                    break;
            }

        }
    }

    //滑动监听
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(simpleAdapter.getCount() == visibleLastIndex && scrollState == SCROLL_STATE_IDLE){
//            new LoadDataThread(false).start();
            new LoadDataThread(true).start();
        }
    }
    //计算ListView显示的最后一条信息的索引
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;//减去最后一个加载中那条
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x101:
                    if (swipeRefreshLayout.isRefreshing()){
                        swipeRefreshLayout.setRefreshing(false);//设置不刷新
                    }
                    simpleAdapter.notifyDataSetChanged();
                    break;
                case 0x102:
                    Toast("没有新的数据了");
                    break;
                case 0x103:
                    TextView tv = findViewById(R.id.footer_textview);
                    tv.setText("已加载全部数据");
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    break;
            }
        }
    };
    /**
     * 加载数据的线程
     */
    class LoadDataThread extends  Thread{

        boolean front = true;

        public LoadDataThread(boolean front)
        {
            this.front = front;
        }

        @Override
        public void run() {

            String request = (front?"1":"0") + id;

            String info = MySocket.request(MySocket.HOST,10068, request);
            Gson gson = new Gson();
            Data[] datas =  gson.fromJson(info,Data[].class);

            if(datas != null) {
                list.clear();
                for (int i = datas.length - 1; i >= 0; i--) {
                    add(datas[i], front);
                }
                handler.sendEmptyMessage(0x103);
            } else {
                if(front) {
                    handler.sendEmptyMessage(0x102);//没有新的数据了
                }
                else {
//                    handler.sendEmptyMessage(0x103);
                }
            }

            handler.sendEmptyMessage(0x101);//通过handler发送一个更新数据的标记
        }

        private void add(Data data,boolean front)
        {
//            if(data.id>startId || startId == 0)
//                startId = data.id;
//            if(data.id<endId || endId == 0)
//                endId = data.id;
            Map<String,Object> map = new HashMap<String,Object>();
            String temple=data.text;
            if (data.text.length()>25){
                temple=data.text.substring(0,26);
            }
            String hospitalname="医院："+data.name;
            map.put("name", data.name);
            map.put("time", data.time);
            map.put("summary", temple);
            Item_data item_data=new Item_data();
            item_data.name=data.name;
            item_data.time=data.time;
            item_data.text=data.text;
            list_item_data.add(item_data);
            if(front) {
                //list.clear();
                list.add(0, map);
            }
            else
                list.add(map);
        }
    }

}
