package e.administrator.xy.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.message;
import e.administrator.xy.pojo.topic;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.constant;
import e.administrator.xy.util.systemMessageAdapter;

public class systemMessage extends AppCompatActivity {

    private RelativeLayout returnBack;
    private RecyclerView mRecyclerView;
    private List<message> messageList;
    private MaterialRefreshLayout refreshLayout;    // 下拉刷新控件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_message);
        initview();
        initdata();
        initRefreshLayout();
    }

    private void initdata() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("receiveAccount",getSharedPreferences("data",MODE_PRIVATE).getString("account",null));
        client.post(constant.BASE_URL + constant.message_get,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    messageList = JsonUtil.getListFromJson(json,new TypeToken<List<message>>(){}.getType());
                    systemMessageAdapter adapter = new systemMessageAdapter(systemMessage.this,messageList);
                    mRecyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(systemMessage.this, "请刷新重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initview() {
        refreshLayout = findViewById(R.id.refresh_systemMessage);
        returnBack = findViewById(R.id.systemMessage_returnTtab1);
        mRecyclerView = findViewById(R.id.recyclerView_systemMessage);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        returnBack.setOnClickListener(new View.OnClickListener() {
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
    }
    private void initRefreshLayout() {
        // 注册下拉刷新监听器
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            // 下拉刷新执行的方法
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                initdata(); // 重新加载数据
                refreshLayout.finishRefresh();
                Toast.makeText(getApplicationContext(), "成功刷新", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
