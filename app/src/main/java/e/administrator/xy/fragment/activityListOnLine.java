package e.administrator.xy.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.activity;
import e.administrator.xy.util.ActivityAdapter;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.constant;


public class activityListOnLine extends Fragment {
    private List<activity> activityList;
    private RecyclerView mRecyclerView;
    private MaterialRefreshLayout refreshLayout;    // 下拉刷新控件

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout =inflater.inflate(R.layout.fragment_activity_list_on_line,null);
        init(layout);
        initRefreshLayout();
        initData();
        return layout;
    }

    private void initData() {
        String url = null;
        final String classify = getActivity().getIntent().getStringExtra("classify");
        AsyncHttpClient client = new AsyncHttpClient();
        if (classify.equals("examineActivity")||classify.equals("falseExamineActivity")){
            RequestParams params = new RequestParams();
            params.put("online",1);
            if (classify.equals("examineActivity")){url = constant.activity_getExamine;}
            if (classify.equals("falseExamineActivity")){url = constant.activity_getFalseExamine;}
            client.post(constant.BASE_URL + url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String json = new String(responseBody);
                    if (json!=null){
                        try {
                            List<activity> sortActivity = null;
                            activityList = JsonUtil.getListFromJson(json, new TypeToken<List<activity>>(){}.getType());
                            sortActivity = JsonUtil.JsonSortActivity(activityList);
                            if (!sortActivity.isEmpty()){
                                mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.ActivityListUnderlineRecyclerView);
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                ActivityAdapter adapter = new ActivityAdapter(getContext(),sortActivity);
                                mRecyclerView.setAdapter(adapter);
                            }else {
                                Toast.makeText(getContext(), "线上活动为空哟", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getContext(), "请刷新重试", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            client.post(constant.BASE_URL + constant.activity_getOnLine, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String json = new String(responseBody);
                    if (json!=null){
                        try {
                            List<activity> sortActivity = null;
                            activityList = JsonUtil.getListFromJson(json, new TypeToken<List<activity>>(){}.getType());
                            if (classify.equals("newActivity")){
                                sortActivity = JsonUtil.JsonSortActivity(activityList);
                            }
                            if (classify.equals("hotActivity")){
                                sortActivity = JsonUtil.JsonSortHotActivity(activityList);
                            }
                            if (classify.equals("endActivity")){
                                sortActivity = JsonUtil.JsonSortEndActivity(activityList);
                            }
                            if (!sortActivity.isEmpty()){
                                mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.ActivityListOnlineRecyclerView);
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                ActivityAdapter adapter = new ActivityAdapter(getContext(),sortActivity);
                                mRecyclerView.setAdapter(adapter);
                            }else {
                                Toast.makeText(getContext(), "线上活动为空哟", Toast.LENGTH_SHORT).show();
                            }
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

    }

    private void initRefreshLayout() {
        // 注册下拉刷新监听器
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            // 下拉刷新执行的方法
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                initData(); // 重新加载数据
                refreshLayout.finishRefresh();
                Toast.makeText(getContext(), "成功刷新", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(View layout) {
        refreshLayout = layout.findViewById(R.id.refresh_ActivityListOnline);
    }
}
