package e.administrator.xy.activity;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import e.administrator.xy.pojo.activity;
import e.administrator.xy.util.ActivityAdapter;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.constant;

public class clubActivityList extends AppCompatActivity {
    
    private RecyclerView mRecyclerView;
    private RelativeLayout returnYj;
    private TextView clubActivityListNull;
    private List<activity> activityList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_activity_list);
        initview();
        initData();
    }

    private void initview() {
        returnYj = findViewById(R.id.clubDetailActivity_goBack);
        returnYj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用手机自带返回键
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        clubActivityListNull = findViewById(R.id.clubActivityListNull);
        mRecyclerView = findViewById(R.id.recyclerView_clubActivityList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        String account = getIntent().getStringExtra("account");
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("account", account);
        client.post(constant.BASE_URL+constant.activity_getByAccount,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    if (json!=null){
                        activityList = JsonUtil.getListFromJson(json, new TypeToken<List<activity>>(){}.getType());
                        if (activityList.size()>1){
                            ActivityAdapter activityAdapter = new ActivityAdapter(clubActivityList.this,JsonUtil.JsonSortActivity(activityList));
                            mRecyclerView.setAdapter(activityAdapter);
                        }else {
                            clubActivityListNull.setVisibility(View.VISIBLE);
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(clubActivityList.this, "请刷新重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
