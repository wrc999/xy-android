package e.administrator.xy.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.io.IOException;

import e.administrator.xy.R;
import e.administrator.xy.fragment.activityListOnLine;
import e.administrator.xy.fragment.activityListUnderLine;
import e.administrator.xy.fragment.onLine;
import e.administrator.xy.fragment.underLine;

public class someActivityList extends AppCompatActivity {
    private RelativeLayout goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.some_activity_list);
        initView();
    }

    private void initView() {
        goBack = findViewById(R.id.someActivityList_goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
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
                getSupportFragmentManager(), FragmentPagerItems.with(someActivityList.this)
                .add("线上", activityListOnLine.class)
                .add("线下", activityListUnderLine.class)
                .create());
        ViewPager viewPager = (ViewPager) findViewById(R.id.someActivityList_viewpager);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.someActivityList_viewpagertab);
        viewPagerTab.setViewPager(viewPager);
    }
}
