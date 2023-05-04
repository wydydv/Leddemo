package com.example.leddemo;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class TabActivity extends AppCompatActivity {
    private LinearLayout layout1;
    private LinearLayout layout2;
    private TextView test;
    private TabHost tabHost;
    private LinearLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initview();

    }
    private void initview() {
        //初始化容器
        layout1 = (LinearLayout) findViewById(R.id.tab1);
        layout2 = (LinearLayout) findViewById(R.id.tab2);
        test = (TextView) findViewById(R.id.text_test);
        tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();
        //创建标签，设置标题/图标/页面布局
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("盘存",null).setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("寻线",null).setContent(R.id.tab2));
        //标签切换事件处理，setOnTabChangedListener
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener(){
            @Override
            // tabId是newTabSpec参数设置的tab页名，并不是layout里面的标识符id
            public void onTabChanged(String tabId) {
                if (tabId.equals("tab1")) {   //第一个标签
                    Log.e("TAG", "onTabChanged1: " );
                }
                if (tabId.equals("tab2")) {   //第二个标签
                    Log.e("TAG", "onTabChanged2: " );
                }
            }

        });

    }
}
