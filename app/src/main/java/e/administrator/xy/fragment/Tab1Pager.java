package e.administrator.xy.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.CreateGroupCallback;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.activity.Search;
import e.administrator.xy.activity.clubDetails;
import e.administrator.xy.pojo.club;
import e.administrator.xy.util.ClubAdapter;
import e.administrator.xy.util.GlideImageLoader;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.constant;


public class Tab1Pager extends Fragment implements View.OnClickListener {
    private Banner banner;  // 广告栏控件
    private List<Integer> bannerImgList = new ArrayList<Integer>();//轮播图片
    private LinearLayout searchButton; //搜索控件
    private MaterialRefreshLayout refreshLayout;    // 下拉刷新控件
    private RecyclerView mRecyclerView;//recyvleview控件

    private LinearLayout first,second,third,fourth,fifth;
    private ImageView firstPic,secondPic,thirdPic,fourthPic,fifthPic;
    private TextView firstName,secondName,thirdName,fourthName,fifthName;
    private List<club> clubList = new ArrayList<club>(),temp = new ArrayList<club>(),hotClub = new ArrayList<club>();

    private ClubAdapter adapter;
    private int lastVisibleItem=0;//保存下拉时最后一条的位置
    private int pageSize=3;//每页条数
    private boolean isLoading = false;//用来控制进入getdata()的次数

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.tab1, null);
        init(layout);
        initBanner();
        initData();
        initRefreshLayout();
        return layout;
    }

    private void init(View layout) {
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.allClub);
        refreshLayout = layout.findViewById(R.id.refresh_xy);
        banner = layout.findViewById(R.id.banner);
        searchButton = layout.findViewById(R.id.Tab1_search);
        //跳转搜索界面
        searchButton.setOnClickListener(this);
        //热门社团
        first = layout.findViewById(R.id.first);
        first.setOnClickListener(this);
        second = layout.findViewById(R.id.second);
        second.setOnClickListener(this);
        third = layout.findViewById(R.id.third);
        third.setOnClickListener(this);
        fourth = layout.findViewById(R.id.fourth);
        fourth.setOnClickListener(this);
        fifth = layout.findViewById(R.id.fifth);
        fifth.setOnClickListener(this);
        firstName = layout.findViewById(R.id.firstName);
        firstPic = layout.findViewById(R.id.firstPic);
        secondName = layout.findViewById(R.id.secondName);
        secondPic = layout.findViewById(R.id.secondPic);
        thirdName = layout.findViewById(R.id.thirdName);
        thirdPic = layout.findViewById(R.id.thirdPic);
        fourthName = layout.findViewById(R.id.fourthName);
        fourthPic = layout.findViewById(R.id.fourthPic);
        fifthName = layout.findViewById(R.id.fifthName);
        fifthPic = layout.findViewById(R.id.fifthPic);

        //上拉加载
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount() && !isLoading) {
                    //到达底部之后如果footView的状态不是正在加载的状态,就将 他切换成正在加载的状态
                    if (temp.size()>0) {
                        isLoading = true;
                        adapter.changeState(1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //判断剩下的数据是否够多
                                if (pageSize<temp.size()){
                                    for(int i=0;i<pageSize;i++){
                                        clubList.add(temp.get(i));
                                    }
                                }else {
                                    for(int i=0;i<temp.size();i++){
                                        clubList.add(temp.get(i));
                                    }
                                }
                                Iterator<club> it1 = temp.iterator();
                                int i=0;
                                while (it1.hasNext()) {
                                    club str = it1.next();
                                    if (i<pageSize) {
                                        it1.remove();
                                        i++;
                                    }else {
                                        break;
                                    }
                                }
                                isLoading = false;
                                adapter = new ClubAdapter(getContext(), clubList);
                                mRecyclerView.setAdapter(adapter);
                            }
                        }, 1000);
                    } else {
                        adapter.changeState(2);

                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //拿到最后一个出现的item的位置
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void initData() {
        final AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.post(constant.BASE_URL+constant.club_get, params,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    temp = JsonUtil.getListFromJson(json, new TypeToken<List<club>>(){}.getType());
                    //按成员数量排序
                    JsonUtil.JsonSortClub(temp);
                    if (!temp.isEmpty()){
                        for (int j =0;j<5;j++) {
                            hotClub.add(temp.get(j));
                        }
                        Iterator<club> it = temp.iterator();
                        int i=0;
                        while (it.hasNext()) {
                            club str = it.next();
                            if (i<5) {
                                it.remove();
                                i++;
                            }else {
                                break;
                            }
                        }
                        //热门社团
                        Glide.with(getContext()).load(hotClub.get(0).getClubAva()).into(firstPic);
                        Glide.with(getContext()).load(hotClub.get(1).getClubAva()).into(secondPic);
                        Glide.with(getContext()).load(hotClub.get(2).getClubAva()).into(thirdPic);
                        Glide.with(getContext()).load(hotClub.get(3).getClubAva()).into(fourthPic);
                        Glide.with(getContext()).load(hotClub.get(4).getClubAva()).into(fifthPic);
                        firstName.setText(hotClub.get(0).getClubName());
                        secondName.setText(hotClub.get(1).getClubName());
                        thirdName.setText(hotClub.get(2).getClubName());
                        fourthName.setText(hotClub.get(3).getClubName());
                        fifthName.setText(hotClub.get(4).getClubName());
                        clubList = new ArrayList<club>();
                        for(int k=0;k<pageSize;k++){
                            clubList.add(temp.get(k));
                        }
                        for (club club:clubList) {
                            temp.remove(club);
                        }
                        adapter = new ClubAdapter(getContext(), clubList);
                        mRecyclerView.setAdapter(adapter);
                    }else {
                        Toast.makeText(getContext(), "请重试", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "请刷新重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Tab1_search:
                Intent intent = new Intent(getContext(), Search.class);
                intent.putExtra("root","club");
                startActivity(intent);
                break;
            case R.id.first:
                Intent intent0 = new Intent(getContext(), clubDetails.class);
                intent0.putExtra("club_id",hotClub.get(0).getClub_id());
                intent0.putExtra("account",hotClub.get(0).getAccount());
                startActivity(intent0);
                break;
            case R.id.second:
                Intent intent1 = new Intent(getContext(), clubDetails.class);
                intent1.putExtra("club_id",hotClub.get(1).getClub_id());
                intent1.putExtra("account",hotClub.get(1).getAccount());
                startActivity(intent1);
                break;
            case R.id.third:
                Intent intent2 = new Intent(getContext(), clubDetails.class);
                intent2.putExtra("club_id",hotClub.get(2).getClub_id());
                intent2.putExtra("account",hotClub.get(2).getAccount());
                startActivity(intent2);
                break;
            case R.id.fourth:
                Intent intent3 = new Intent(getContext(), clubDetails.class);
                intent3.putExtra("club_id",hotClub.get(3).getClub_id());
                intent3.putExtra("account",hotClub.get(3).getAccount());
                startActivity(intent3);
                break;
            case R.id.fifth:
                Intent intent4 = new Intent(getContext(), clubDetails.class);
                intent4.putExtra("club_id",hotClub.get(4).getClub_id());
                intent4.putExtra("account",hotClub.get(4).getAccount());
                startActivity(intent4);
                break;
        }
    }

    private void initBanner() {
        bannerImgList.add(R.mipmap.middle1);
        bannerImgList.add(R.mipmap.middle2);
        bannerImgList.add(R.mipmap.middle3);
        bannerImgList.add(R.mipmap.middle4);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setImages(bannerImgList);
        banner.start();
    }

    private void initRefreshLayout() {
        // 注册下拉刷新监听器
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            // 下拉刷新执行的方法
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                bannerImgList = new ArrayList<Integer>();
                clubList = new ArrayList<club>();
                temp =  new ArrayList<club>();
                hotClub = new ArrayList<club>();
                initData(); // 重新加载数据
                refreshLayout.finishRefresh();
                Toast.makeText(getContext(), "成功刷新", Toast.LENGTH_SHORT).show();
            }
        });
    }
}