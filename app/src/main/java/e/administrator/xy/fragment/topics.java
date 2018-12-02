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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.activity.Search;
import e.administrator.xy.pojo.club;
import e.administrator.xy.pojo.topic;
import e.administrator.xy.pojo.view;
import e.administrator.xy.util.ClubAdapter;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.TopicAdapter;
import e.administrator.xy.util.constant;

public class topics extends Fragment {

    private RecyclerView mRecyclerView;//recyvleview控件
    private LinearLayout searchButton; //搜索控件
    private List<topic> temp = new ArrayList<topic>(),topicList = new ArrayList<topic>();
    private TopicAdapter adapter;
    private String resource;//标识上一个页面

    int lastVisibleItem=0;
    int pageSize=3;//每页条数
    boolean isLoading = false;//用来控制进入getdata()的次数

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        resource = getActivity().getIntent().getStringExtra("talkAdd");
        mRecyclerView = view.findViewById(R.id.allTopic);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //禁止mEecyclerView滑动，但会在滑动的时候添加惯性
        //mRecyclerView.setNestedScrollingEnabled(false);
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
                                        topicList.add(temp.get(i));
                                    }
                                }else {
                                    for(int i=0;i<temp.size();i++){
                                        topicList.add(temp.get(i));
                                    }
                                }
                                Iterator<topic> it1 = temp.iterator();
                                int i=0;
                                while (it1.hasNext()) {
                                    topic str = it1.next();
                                    if (i<pageSize) {
                                        it1.remove();
                                        i++;
                                    }else {
                                        break;
                                    }
                                }
                                isLoading = false;
                                adapter = new TopicAdapter(getContext(), topicList,resource);
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
        searchButton = view.findViewById(R.id.topic_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Search.class);
                intent.putExtra("root","topic");
                startActivity(intent);
            }
        });
    }

    private void initData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(constant.BASE_URL + constant.topicGet, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    temp = JsonUtil.getListFromJson(json, new TypeToken<List<topic>>(){}.getType());
                    //按成员数量排序
                    JsonUtil.JsonSortTopic(temp);
                    if (!temp.isEmpty()){
                        topicList = new ArrayList<topic>();
                        for(int k=0;k<pageSize;k++){
                            topicList.add(temp.get(k));
                        }
                        for (topic topic:topicList) {
                            temp.remove(topic);
                        }
                        adapter = new TopicAdapter(getContext(), topicList,resource);
                        mRecyclerView.setAdapter(adapter);
                    }else {
                        Toast.makeText(getContext(), "内容为空", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
