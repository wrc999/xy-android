package e.administrator.xy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
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
import e.administrator.xy.fragment.topics;
import e.administrator.xy.pojo.Talk;
import e.administrator.xy.pojo.topic;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.TalkAdapter;
import e.administrator.xy.util.constant;
import e.administrator.xy.view.RecycleViewDivider;

public class topicDetail extends AppCompatActivity {

    private RelativeLayout returnTopic;
    private TextView topicDetailName,topicDetailIntro,topicDetailJoins;
    private RecyclerView mRecyclerView;//recyvleview控件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_detail);
        initView();
        initData();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.topicDetail_recycleview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        returnTopic = findViewById(R.id.topicDetail_goBack);
        returnTopic.setOnClickListener(new View.OnClickListener() {
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
        topicDetailName = findViewById(R.id.topicDetailName);
        topicDetailIntro = findViewById(R.id.topicDetailIntro);
        topicDetailJoins = findViewById(R.id.topicDetailJoins);
    }

    private void initData() {
        topic topic = (topic)getIntent().getSerializableExtra("topicDetail");
        topicDetailName.setText(topic.getTopicName());
        topicDetailIntro.setText(topic.getTopicIntro());
        topicDetailJoins.setText("此话题共有"+topic.getTopicJoins()+"条讨论");

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("topicName",topic.getTopicName());
        client.post(constant.BASE_URL+constant.Talk_get, params,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    List<Talk> talkList = JsonUtil.getListFromJson(json, new TypeToken<List<Talk>>(){}.getType());
                    if (!talkList.isEmpty()){
                        mRecyclerView.addItemDecoration(new RecycleViewDivider(topicDetail.this, LinearLayoutManager.HORIZONTAL,10,getResources().getColor((R.color.blue))));
                        TalkAdapter adapter = new TalkAdapter(topicDetail.this, JsonUtil.JsonSortTalk(talkList));
                        adapter.setOnRefreshListener(new TalkAdapter.Inter() {
                            @Override
                            public void refresh() {
                                initData();
                            }
                        });
                        mRecyclerView.setAdapter(adapter);
                    }else {
                        Toast.makeText(topicDetail.this, "话题暂未有人参与讨论", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(topicDetail.this, "请刷新重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
