package e.administrator.xy.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.view;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.constant;
import e.administrator.xy.util.viewAdapter;
import e.administrator.xy.view.RecycleViewDivider;

public class MyView extends AppCompatActivity {

    private RecyclerView rRecyclerView;
    private List<view> viewList;
    private RelativeLayout returnGc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_view);
        init();
        initView();
    }

    private void init() {
        returnGc = findViewById(R.id.tv_myview_back);
        returnGc.setOnClickListener(new View.OnClickListener() {
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
    }

    private void initView() {
        rRecyclerView = (RecyclerView) findViewById(R.id.viewList);
        rRecyclerView.setLayoutManager(new GridLayoutManager(MyView.this,3));
        rRecyclerView.addItemDecoration(new RecycleViewDivider(MyView.this, LinearLayoutManager.HORIZONTAL,1,getResources().getColor((R.color.txtGray))));
        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("use_id",sp.getInt("use_id",0));
        client.post(constant.BASE_URL + constant.view_get, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    if (json!=null){
                        viewList = JsonUtil.getListFromJson(json, new TypeToken<List<view>>(){}.getType());
                        List<String> imageList = new ArrayList<String>();
                        for(view v : viewList){
                            imageList.add(v.getPhoto());
                        }
                        if (viewList!=null){
                            rRecyclerView.setAdapter(new viewAdapter(MyView.this,imageList));
                        }else {
                            Toast.makeText(MyView.this, "获取失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
