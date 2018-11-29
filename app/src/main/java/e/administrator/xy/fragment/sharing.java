package e.administrator.xy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.activity.MainActivity;
import e.administrator.xy.activity.activityDetails;
import e.administrator.xy.pojo.Talk;
import e.administrator.xy.pojo.activity;
import e.administrator.xy.pojo.topic;
import e.administrator.xy.util.ActivityAdapter;
import e.administrator.xy.util.GlideImageLoader;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.TalkAdapter;
import e.administrator.xy.util.TopicAdapter;
import e.administrator.xy.util.constant;
import e.administrator.xy.util.hotTopicAdapter;
import e.administrator.xy.view.RecycleViewDivider;

public class sharing extends Fragment {
    private Banner banner;  // 广告栏控件
    private List<String> bannerImgList = new ArrayList<String>();//轮播图片
    private List<Talk> talkList;
    private List<activity> activityList;
    private List<topic> topicList = new ArrayList<topic>();
    private hotTopicAdapter adapter;
    private RecyclerView mRecyclerView;
    private LinearLayout sharing_allTopic;
    private MaterialRefreshLayout refreshLayout;    // 下拉刷新控件

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout =inflater.inflate(R.layout.fragment_sharing,null);
        init(layout);
        initRefreshLayout();
        initHotActivity();
        initHotTopic();
        initTalk();
        return layout;
    }

    private void init(View layout) {
        sharing_allTopic = layout.findViewById(R.id.sharing_allTopic);
        sharing_allTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("id",22);
                startActivity(intent);
            }
        });
        refreshLayout = layout.findViewById(R.id.refresh_sharing);
        banner = layout.findViewById(R.id.sharingActivityBanner);
    }

    private void initTalk() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.post(constant.BASE_URL+constant.Talk_get, params,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    talkList = JsonUtil.getListFromJson(json, new TypeToken<List<Talk>>(){}.getType());
                    if (!talkList.isEmpty()){
                            mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_xl);
                            mRecyclerView.setNestedScrollingEnabled(false);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            mRecyclerView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.HORIZONTAL,10,getResources().getColor((R.color.blue))));
                            TalkAdapter adapter = new TalkAdapter(getContext(), JsonUtil.JsonSortTalk(talkList));
                            adapter.setOnRefreshListener(new TalkAdapter.Inter() {
                                @Override
                                public void refresh() {
                                    initTalk();
                                }
                            });
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

    private void initHotTopic() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(constant.BASE_URL + constant.topicGet, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    topicList = JsonUtil.getListFromJson(json, new TypeToken<List<topic>>(){}.getType());
                    //按成员数量排序
                    JsonUtil.JsonSortTopic(topicList);
                    if (!topicList.isEmpty()){
                        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.sharingHotTopic);
                        mRecyclerView.setNestedScrollingEnabled(false);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        mRecyclerView.setLayoutManager(linearLayoutManager);
                        adapter = new hotTopicAdapter(getContext(), topicList);
                        mRecyclerView.setAdapter(adapter);
                    }else {
                        Toast.makeText(getContext(), "请重试1", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "请重试2", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initHotActivity() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(constant.BASE_URL + constant.activity_getByAccount, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                if (json!=null) {
                    try {
                        activityList = JsonUtil.getListFromJson(json, new TypeToken<List<activity>>() {}.getType());
                        JsonUtil.JsonSortHotActivity(activityList);
                        bannerImgList.add(activityList.get(0).getAva());
                        bannerImgList.add(activityList.get(1).getAva());
                        bannerImgList.add(activityList.get(2).getAva());
                        bannerImgList.add(activityList.get(3).getAva());
                        bannerImgList.add(activityList.get(4).getAva());
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
                        banner.setClickable(true);
                        banner.setOnBannerListener(new OnBannerListener() {
                            @Override
                            public void OnBannerClick(int position) {
                                Intent intent1 = new Intent(getContext(),activityDetails.class);
                                intent1.putExtra("activity_id",activityList.get(position).getActivity_id());
                                intent1.putExtra("account",activityList.get(position).getAccount());
                                intent1.putExtra("onLine",activityList.get(position).getOnline());
                                startActivity(intent1);
                            }
                        });
                        banner.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "请刷新重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initRefreshLayout() {
        // 注册下拉刷新监听器
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            // 下拉刷新执行的方法
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                initTalk(); // 重新加载数据
                refreshLayout.finishRefresh();
                Toast.makeText(getContext(), "成功刷新", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
