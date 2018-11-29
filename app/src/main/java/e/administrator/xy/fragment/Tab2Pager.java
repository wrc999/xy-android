package e.administrator.xy.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import e.administrator.xy.R;
import e.administrator.xy.activity.MainActivity;


public class Tab2Pager extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout =inflater.inflate(R.layout.tab2,null);
        init(layout);
        return layout;
    }

    public void init(View layout) {
        //使用getChildFragmentManager代替getFragmentManager，防止二次访问时出现空白页面
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), FragmentPagerItems.with(getContext())
                .add("广场", sharing.class)
                .add("话题", topics.class)
                .create());
        ViewPager viewPager = (ViewPager) layout.findViewById(R.id.tab1_viewpager);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = (SmartTabLayout) layout.findViewById(R.id.tab2_viewpagertab);
        viewPagerTab.setViewPager(viewPager);
        int id = getActivity().getIntent().getIntExtra("id",-1);
        if (id == 21){
            viewPager.setCurrentItem(0);
        }
        if (id == 22){
            viewPager.setCurrentItem(1);
        }
    }
}