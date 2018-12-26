package e.administrator.xy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.activity;
import e.administrator.xy.pojo.club;
import e.administrator.xy.pojo.topic;
import e.administrator.xy.util.ActivityAdapter;
import e.administrator.xy.util.ClubAdapter;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.TopicAdapter;
import e.administrator.xy.util.constant;


public class Search extends AppCompatActivity {

    private SearchView mSearchView;
    private RecyclerView mRecyclerView;//recyvleview控件
    private RelativeLayout returnxy;
    private List<club> clubList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        returnxy = findViewById(R.id.search_goBack);
        returnxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mRecyclerView = findViewById(R.id.searchClubResult);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(Search.this));
        final String root = getIntent().getStringExtra("root");
        mSearchView = (SearchView) findViewById(R.id.clubSearchView);
        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (root.equals("topic")){
                    //搜索并显示数据
                    final AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.put("topicName",query);
                    client.post(constant.BASE_URL+constant.topicGet, params,new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                String json = new String(responseBody,"utf-8");
                                List<topic> topicList = JsonUtil.getListFromJson(json, new TypeToken<List<topic>>(){}.getType());
                                if (!topicList.isEmpty()){
                                    TopicAdapter adapter = new TopicAdapter(Search.this, topicList);
                                    mRecyclerView.setAdapter(adapter);
                                }else {
                                    Toast.makeText(Search.this, "搜索内容为空", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(Search.this, "请刷新重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if (root.equals("club")){
                    //搜索并显示数据
                    final AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.put("clubName",query);
                    client.post(constant.BASE_URL+constant.club_search, params,new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                String json = new String(responseBody,"utf-8");
                                clubList = new  ArrayList<club>();
                                clubList = JsonUtil.getListFromJson(json, new TypeToken<List<club>>(){}.getType());
                                //按成员数量排序
                                JsonUtil.JsonSortClub(clubList);
                                if (!clubList.isEmpty()){
                                    ClubAdapter adapter = new ClubAdapter(Search.this, clubList);
                                    mRecyclerView.setAdapter(adapter);
                                }else {
                                    Toast.makeText(Search.this, "搜索内容为空", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(Search.this, "请刷新重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if (root.equals("activity")){
                    //搜索并显示数据
                    final AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.put("name",query);
                    client.post(constant.BASE_URL+constant.activity_activitySearch, params,new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                String json = new String(responseBody,"utf-8");
                                List<activity> activityList = JsonUtil.getListFromJson(json, new TypeToken<List<activity>>(){}.getType());
                                if (!activityList.isEmpty()){
                                    ActivityAdapter adapter = new ActivityAdapter(Search.this, activityList);
                                    mRecyclerView.setAdapter(adapter);
                                }else {
                                    Toast.makeText(Search.this, "搜索内容为空", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(Search.this, "请刷新重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
