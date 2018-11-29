package e.administrator.xy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupMembersCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.club;
import e.administrator.xy.pojo.clubmember;
import e.administrator.xy.util.ClubAdapter;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.activityMemberListAdapter;
import e.administrator.xy.util.clubMemberListAdapter;
import e.administrator.xy.util.constant;

public class clubMember extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout clubMember_goBack;
    private RecyclerView mRecyclerView;//recyvleview控件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_member);
        initView();
        initData();
    }

    private void initView() {
        clubMember_goBack = findViewById(R.id.clubMember_goBack);
        clubMember_goBack.setOnClickListener(this);
        mRecyclerView = findViewById(R.id.clubMember_recycleview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(clubMember.this));
    }

    private void initData() {
        String resource = getIntent().getStringExtra("resource");
        if (resource.equals("club")){
            final Long groupID = getIntent().getLongExtra("groupID",0);
            JMessageClient.getGroupMembers(groupID, new GetGroupMembersCallback() {
                @Override
                public void gotResult(int i, String s, List<UserInfo> list) {
                    clubMemberListAdapter adapter = new clubMemberListAdapter(clubMember.this,list,groupID);
                    mRecyclerView.setAdapter(adapter);
                }
            });
        }
        if (resource.equals("activity")){
            List<String> memberList = (List<String>) getIntent().getStringArrayListExtra("member");
            String applyPeople = getIntent().getStringExtra("applyPeople");
            activityMemberListAdapter adapter = new activityMemberListAdapter(clubMember.this,memberList,applyPeople);
            mRecyclerView.setAdapter(adapter);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clubMember_goBack:
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
