package e.administrator.xy.fragment;


import android.content.Context;
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

import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.Talk;
import e.administrator.xy.pojo.activity;
import e.administrator.xy.pojo.club;
import e.administrator.xy.util.ActivityAdapter;
import e.administrator.xy.util.ClubAdapter;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.TalkAdapter;
import e.administrator.xy.util.constant;
import e.administrator.xy.view.RecycleViewDivider;

/**
 * A simple {@link Fragment} subclass.
 */
public class clubJoin extends Fragment {

    private List<club> clubList;
    private RecyclerView mRecyclerView;
    private MaterialRefreshLayout refreshLayout;    // 下拉刷新控件

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout =inflater.inflate(R.layout.fragment_club_join,null);
        init(layout);
        initRefreshLayout();
        initData();
        return layout;
    }

    private void initData() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String account = getContext().getSharedPreferences("data", Context.MODE_PRIVATE).getString("account",null);
        params.put("account",account);
        client.post(constant.BASE_URL+constant.club_findByAccount, params,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    clubList = JsonUtil.getListFromJson(json, new TypeToken<List<club>>(){}.getType());
                    if (!clubList.isEmpty()){
                        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.myCLubJoinRecyclerView);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        ClubAdapter adapter = new ClubAdapter(getContext(),clubList);
                        mRecyclerView.setAdapter(adapter);
                    }else {
                        Toast.makeText(getContext(), "你还没参与任何社团哟", Toast.LENGTH_SHORT).show();
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
        refreshLayout = layout.findViewById(R.id.refresh_myclubJoin);
    }

}
