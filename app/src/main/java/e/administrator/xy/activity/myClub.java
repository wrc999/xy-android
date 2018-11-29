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
import e.administrator.xy.fragment.clubFocus;
import e.administrator.xy.fragment.clubJoin;
import e.administrator.xy.fragment.sharing;
import e.administrator.xy.fragment.topics;

public class myClub extends AppCompatActivity {

    private RelativeLayout myClub_goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_club);
        myClub_goBack = findViewById(R.id.myClub_goBack);
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
                getSupportFragmentManager(), FragmentPagerItems.with(myClub.this)
                .add("加入", clubJoin.class)
                .add("关注", clubFocus.class)
                .create());
        ViewPager viewPager = (ViewPager) findViewById(R.id.myClub_viewpager);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.myClub_viewpagertab);
        viewPagerTab.setViewPager(viewPager);
    }
}
