package e.administrator.xy.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.Talk;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.TalkAdapter;
import e.administrator.xy.util.constant;
import e.administrator.xy.view.RecycleViewDivider;

public class MyTalk extends AppCompatActivity implements View.OnClickListener {
    private TextView returnYj,myTalkNull;
    private RecyclerView mRecyclerView;
    private List<Talk> talkList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_talk);
        init();
        initdata();
    }

    private void init() {
        mRecyclerView = findViewById(R.id.mytalk);
        myTalkNull = findViewById(R.id.myTalkNull);
        returnYj = findViewById(R.id.tv_talk_back);
        returnYj.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_talk_back:
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void initdata() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        SharedPreferences sp = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        params.put("use_id",sp.getInt("use_id",0));
        client.post(constant.BASE_URL+constant.Talk_get,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    if (json!=null){
                        talkList = JsonUtil.getListFromJson(json, new TypeToken<List<Talk>>(){}.getType());
                        if (talkList.size()>1){
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(MyTalk.this));
                            mRecyclerView.addItemDecoration(new RecycleViewDivider(MyTalk.this, LinearLayoutManager.HORIZONTAL,10,getResources().getColor((R.color.blue))));
                            TalkAdapter adapter = new TalkAdapter(MyTalk.this,JsonUtil.JsonSortTalk(talkList));
                            adapter.setOnRefreshListener(new TalkAdapter.Inter() {
                                @Override
                                public void refresh() {
                                    initdata();
                                }
                            });
                            mRecyclerView.setAdapter(adapter);
                        }else {
                            myTalkNull.setVisibility(View.VISIBLE);
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MyTalk.this, "请刷新重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
