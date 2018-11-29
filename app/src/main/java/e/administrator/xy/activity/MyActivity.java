package e.administrator.xy.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.io.IOException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.fragment.clubFocus;
import e.administrator.xy.fragment.clubJoin;
import e.administrator.xy.fragment.onLine;
import e.administrator.xy.fragment.underLine;
import e.administrator.xy.pojo.activity;
import e.administrator.xy.util.ActivityAdapter;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.constant;
import e.administrator.xy.view.RecycleViewDivider;

public class MyActivity extends AppCompatActivity {
    private RelativeLayout myClub_goBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myactivity);
        init();
    }

    private void init() {
        myClub_goBack = findViewById(R.id.myActivity_goBack);
        myClub_goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用手机自带返回键
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //使用getChildFragmentManager代替getFragmentManager，防止二次访问时出现空白页面
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(MyActivity.this)
                .add("线上", onLine.class)
                .add("线下", underLine.class)
                .create());
        ViewPager viewPager = (ViewPager) findViewById(R.id.myActivity_viewpager);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.myActivity_viewpagertab);
        viewPagerTab.setViewPager(viewPager);
    }
}
