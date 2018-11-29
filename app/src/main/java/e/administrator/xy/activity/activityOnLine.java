package e.administrator.xy.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.activity;
import e.administrator.xy.pojo.club;
import e.administrator.xy.util.ActivityOnLineAdapter;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.constant;

public class activityOnLine extends AppCompatActivity {

    private MaterialRefreshLayout refreshLayout;    // 下拉刷新控件
    private RecyclerView mRecyclerView;//recyvleview控件
    private List<activity> activityList = new ArrayList<activity>();
    private RelativeLayout returnTab3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_line);
        initView();
        initData();
        initRefreshLayout();
    }

    private void initView() {
        returnTab3 = findViewById(R.id.activityOnLine_goBack);
        returnTab3.setOnClickListener(new View.OnClickListener() {
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
        refreshLayout = findViewById(R.id.activityOnLineRefresh);
        mRecyclerView = findViewById(R.id.activityOnLineRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(constant.BASE_URL + constant.activity_getOnLine, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            String json = new String(responseBody,"utf-8");
                            if (json!=null) {
                                activityList = JsonUtil.getListFromJson(json, new TypeToken<List<activity>>() {}.getType());
                                JsonUtil.JsonSortActivity(activityList);
                                ActivityOnLineAdapter adapter = new ActivityOnLineAdapter(activityOnLine.this, activityList);
                                mRecyclerView.setAdapter(adapter);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(activityOnLine.this, "请重试", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(activityOnLine.this, "成功刷新", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
