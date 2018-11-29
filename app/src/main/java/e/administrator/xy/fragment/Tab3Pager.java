package e.administrator.xy.fragment;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.activity.Search;
import e.administrator.xy.activity.activityDetails;
import e.administrator.xy.activity.activityOnLine;
import e.administrator.xy.activity.someActivityList;
import e.administrator.xy.pojo.activity;
import e.administrator.xy.util.ActivityAdapter;
import e.administrator.xy.util.DateUtil;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.constant;


public class Tab3Pager extends Fragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private List<activity> activityList = new ArrayList<activity>(),
            temp = new ArrayList<activity>(),underLineList = new ArrayList<activity>();
    private LinearLayout search;
    private RelativeLayout firstOnline,secondOnline,thirdOnline,fourthOnline;
    private ImageView firstOnlineAva,secondOnlineAva,thirdOnlineAva,fourthOnlineAva;
    private TextView onlineMore,firstOnlineName,secondOnlineName,thirdOnlineName,fourthOnlineName,
            firstOnlineTime,secondOnlineTime,thirdOnlineTime,fourthOnlineTime;
    private LinearLayout newActivity,hotActivity,endActivity,examieActivity,falseExamieActivity;

    private ActivityAdapter adapter;
    private int lastVisibleItem=0;//保存下拉时最后一条的位置
    private int pageSize=1;//每页条数
    private boolean isLoading = false;//用来控制进入getdata()的次数

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.tab3,null);
        initViews(layout);
        initData();
        return layout;
    }

    private void initViews(View layout) {
        mRecyclerView = layout.findViewById(R.id.tab3_underLineActivityList);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount() && !isLoading) {
                    //到达底部之后如果footView的状态不是正在加载的状态,就将 他切换成正在加载的状态
                    if (underLineList.size()>0) {
                        isLoading = true;
                        adapter.changeState(1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //判断剩下的数据是否够多
                                if (pageSize<underLineList.size()){
                                    for(int i=0;i<pageSize;i++){
                                        temp.add(underLineList.get(i));
                                    }
                                }else {
                                    for(int i=0;i<underLineList.size();i++){
                                        temp.add(underLineList.get(i));
                                    }
                                }
                                Iterator<activity> it1 = underLineList.iterator();
                                int i=0;
                                while (it1.hasNext()) {
                                    activity str = it1.next();
                                    if (i<pageSize) {
                                        it1.remove();
                                        i++;
                                    }else {
                                        break;
                                    }
                                }
                                isLoading = false;
                                adapter = new ActivityAdapter(getContext(), temp);
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
        newActivity = layout.findViewById(R.id.tab3_newActivity);
        newActivity.setOnClickListener(this);
        hotActivity = layout.findViewById(R.id.tab3_hotActivity);
        hotActivity.setOnClickListener(this);
        endActivity = layout.findViewById(R.id.tab3_endActivity);
        endActivity.setOnClickListener(this);
        examieActivity = layout.findViewById(R.id.tab3_examineActivity);
        examieActivity.setOnClickListener(this);
        falseExamieActivity = layout.findViewById(R.id.tab3_falseExamineActivity);
        falseExamieActivity.setOnClickListener(this);
        search = layout.findViewById(R.id.Tab3_search);
        search.setOnClickListener(this);
        onlineMore = layout.findViewById(R.id.onlineMore);
        onlineMore.setOnClickListener(this);
        firstOnline = layout.findViewById(R.id.firstOnline);
        firstOnlineName = layout.findViewById(R.id.firstOnlineName);
        firstOnlineAva = layout.findViewById(R.id.firstOnlineAva);
        firstOnlineTime = layout.findViewById(R.id.firstOnlineTime);
        firstOnline.setOnClickListener(this);
        secondOnline = layout.findViewById(R.id.secondOnline);
        secondOnlineName = layout.findViewById(R.id.secondOnlineName);
        secondOnlineAva = layout.findViewById(R.id.secondOnlineAva);
        secondOnlineTime = layout.findViewById(R.id.secondOnlineTime);
        secondOnline.setOnClickListener(this);
        thirdOnline = layout.findViewById(R.id.thirdOnline);
        thirdOnlineName = layout.findViewById(R.id.thirdOnlineName);
        thirdOnlineAva = layout.findViewById(R.id.thirdOnlineAva);
        thirdOnlineTime = layout.findViewById(R.id.thirdOnlineTime);
        thirdOnline.setOnClickListener(this);
        fourthOnline = layout.findViewById(R.id.fourthOnline);
        fourthOnlineName = layout.findViewById(R.id.fourthOnlineName);
        fourthOnlineAva = layout.findViewById(R.id.fourthOnlineAva);
        fourthOnlineTime = layout.findViewById(R.id.fourthOnlineTime);
        fourthOnline.setOnClickListener(this);
    }

    private void initData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(constant.BASE_URL+constant.activity_getByAccount, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    if (json!=null){
                        activityList = JsonUtil.getListFromJson(json, new TypeToken<List<activity>>(){}.getType());
                        JsonUtil.JsonSortActivity(activityList);
                        //获取所有线下活动
                        for (activity a:activityList) {
                            if (a.getOnline()==0){
                                underLineList.add(a);
                            }
                        }
                        //获取线上活动
                        Iterator<activity> it = activityList.iterator();
                        while (it.hasNext()) {
                            activity a = it.next();
                            if (a.getOnline()==0) {
                                it.remove();
                            }else {
                                break;
                            }
                        }

                        firstOnlineName.setText(activityList.get(0).getName());
                        Glide.with(getContext()).load(activityList.get(0).getAva()).into(firstOnlineAva);
                        if (DateUtil.judgeTime(activityList.get(0).getStarttime())){
                            firstOnlineTime.setText("正在进行中");
                        }else {
                            firstOnlineTime.setText("已结束");
                        }
                        secondOnlineName.setText(activityList.get(1).getName());
                        Glide.with(getContext()).load(activityList.get(1).getAva()).into(secondOnlineAva);
                        if (DateUtil.judgeTime(activityList.get(1).getStarttime())){
                            secondOnlineTime.setText("正在进行中");
                        }else {
                            secondOnlineTime.setText("已结束");
                        }
                        thirdOnlineName.setText(activityList.get(2).getName());
                        Glide.with(getContext()).load(activityList.get(2).getAva()).into(thirdOnlineAva);
                        if (DateUtil.judgeTime(activityList.get(2).getStarttime())){
                            thirdOnlineTime.setText("正在进行中");
                        }else {
                            thirdOnlineTime.setText("已结束");
                        }
                        fourthOnlineName.setText(activityList.get(3).getName());
                        Glide.with(getContext()).load(activityList.get(3).getAva()).into(fourthOnlineAva);
                        if (DateUtil.judgeTime(activityList.get(3).getStarttime())){
                            fourthOnlineTime.setText("正在进行中");
                        }else {
                            fourthOnlineTime.setText("已结束");
                        }

                        if (underLineList!=null){
                            for(int k=0;k<pageSize;k++){
                                temp.add(underLineList.get(k));
                            }
                            for (activity activity:temp) {
                                underLineList.remove(activity);
                            }
                            adapter = new ActivityAdapter(getContext(),temp);
                            mRecyclerView.setAdapter(adapter);
                        }else {
                            Toast.makeText(getContext(), "获取失败", Toast.LENGTH_SHORT).show();
                        }
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
            case R.id.Tab3_search:
                Intent intent = new Intent(getContext(), Search.class);
                intent.putExtra("root","activity");
                startActivity(intent);
                break;
            case R.id.tab3_newActivity:
                Intent newActivity = new Intent(getContext(), someActivityList.class);
                newActivity.putExtra("classify","newActivity");
                startActivity(newActivity);
                break;
            case R.id.tab3_hotActivity:
                Intent hotActivity = new Intent(getContext(), someActivityList.class);
                hotActivity.putExtra("classify","hotActivity");
                startActivity(hotActivity);
                break;
            case R.id.tab3_endActivity:
                Intent endActivity = new Intent(getContext(), someActivityList.class);
                endActivity.putExtra("classify","endActivity");
                startActivity(endActivity);
                break;
            case R.id.tab3_examineActivity:
                Intent examineActivity = new Intent(getContext(), someActivityList.class);
                examineActivity.putExtra("classify","examineActivity");
                startActivity(examineActivity);
                break;
            case R.id.tab3_falseExamineActivity:
                Intent falseExamineActivity = new Intent(getContext(), someActivityList.class);
                falseExamineActivity.putExtra("classify","falseExamineActivity");
                startActivity(falseExamineActivity);
                break;
            case R.id.onlineMore:
                Intent intent4 = new Intent(getContext(), activityOnLine.class);
                startActivity(intent4);
                break;
            case R.id.firstOnline:
                Intent intent0 = new Intent(getContext(),activityDetails.class);
                intent0.putExtra("activity_id",activityList.get(0).getActivity_id());
                intent0.putExtra("account",activityList.get(0).getAccount());
                intent0.putExtra("onLine",1);
                startActivity(intent0);
                break;
            case R.id.secondOnline:
                Intent intent1 = new Intent(getContext(),activityDetails.class);
                intent1.putExtra("activity_id",activityList.get(1).getActivity_id());
                intent1.putExtra("account",activityList.get(1).getAccount());
                intent1.putExtra("onLine",1);
                startActivity(intent1);
                break;
            case R.id.thirdOnline:
                Intent intent2 = new Intent(getContext(),activityDetails.class);
                intent2.putExtra("activity_id",activityList.get(2).getActivity_id());
                intent2.putExtra("account",activityList.get(2).getAccount());
                intent2.putExtra("onLine",1);
                startActivity(intent2);
                break;
            case R.id.fourthOnline:
                Intent intent3 = new Intent(getContext(),activityDetails.class);
                intent3.putExtra("activity_id",activityList.get(3).getActivity_id());
                intent3.putExtra("account",activityList.get(3).getAccount());
                intent3.putExtra("onLine",1);
                startActivity(intent3);
                break;
        }
    }
}
