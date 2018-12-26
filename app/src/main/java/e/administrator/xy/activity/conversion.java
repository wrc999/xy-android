package e.administrator.xy.activity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.reflect.TypeToken;
import com.jpeng.jptabbar.JPTabBar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupIDListCallback;
import cn.jpush.im.android.api.callback.GetGroupMembersCallback;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.message;
import e.administrator.xy.pojo.userInfo;
import e.administrator.xy.util.ConversationAdapter;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.constant;
import e.administrator.xy.util.systemMessageAdapter;
import e.administrator.xy.view.RecycleViewDivider;

public class conversion extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RelativeLayout returnBack;
    private LinearLayout systemMessageLinear;
    private TextView systemMessageNum;
    private MaterialRefreshLayout refreshLayout;    // 下拉刷新控件
    String avaPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversion);
        initview();
        initRefreshLayout();
    }

    private void initview() {
        refreshLayout = findViewById(R.id.refresh_conversion);
        systemMessageLinear = findViewById(R.id.systemMessage);
        //单击进入消息页
        systemMessageLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(conversion.this, systemMessage.class);
                startActivity(intent);
            }
        });
        //长按隐藏消息页
        systemMessageLinear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                systemMessageLinear.setVisibility(View.INVISIBLE);
                return true;
            }
        });
        systemMessageNum = findViewById(R.id.systemMessageNum);
        returnBack = findViewById(R.id.conversionList_returnTtab1);
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
        mRecyclerView = findViewById(R.id.recyclerView_conversionList);
        initConversion();
        getSystemMessage();
    }

    private void initRefreshLayout() {
        // 注册下拉刷新监听器
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            // 下拉刷新执行的方法
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                initConversion(); // 重新加载数据
                refreshLayout.finishRefresh();
                Toast.makeText(getApplicationContext(), "成功刷新", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initConversion() {
        final List<Conversation> conversationList = JMessageClient.getConversationList();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ConversationAdapter conversationAdapter = new ConversationAdapter(this, conversationList);
        mRecyclerView.setAdapter(conversationAdapter);

        //列表点击监听
        conversationAdapter.setOnItemClickListener(new ConversationAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Conversation conversation = conversationList.get(position);
                //进入会话后重置会话的未读数
                conversation.resetUnreadCount();
                //如果点击的是单聊
                if (conversation.getType().toString().equals("single")) {
                    Intent intent = new Intent(conversion.this, ChatRoomActivity.class);
                    //获取会话对象的Info。 如果是单聊是聊天对象的UserInfo.
                    UserInfo userInfo = (UserInfo) conversation.getTargetInfo();
                    String userName = userInfo.getUserName();
                    String nickName = userInfo.getNickname();
                    intent.putExtra("userName", userName);
                    intent.putExtra("nickName", nickName);
                    intent.putExtra("type", "single");
                    startActivity(intent);
                }
                //如果点击的是群聊
                if (conversation.getType().toString().equals("group")) {
                    Intent intent = new Intent(conversion.this, ChatRoomActivity.class);
                    //获取会话对象的Info。 如果是单聊是聊天对象的UserI,群聊是GroupInfo.
                    final GroupInfo groupInfo = (GroupInfo) conversation.getTargetInfo();
                    intent.putExtra("groupId",groupInfo.getGroupID());
                    intent.putExtra("groupName",groupInfo.getGroupName());
                    intent.putExtra("type","group");
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(int position) {
                Conversation conversation = conversationList.get(position);
                if (conversation.getType().toString().equals("single")) {
                    UserInfo userInfo = (UserInfo) conversation.getTargetInfo();
                    final String userName = userInfo.getUserName();

                    AlertDialog.Builder builder = new AlertDialog.Builder(conversion.this);
                    builder.setTitle("是否删除会话").setIcon(
                            R.mipmap.logo).setNegativeButton("否", null);
                    builder.setPositiveButton("是",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    JMessageClient.deleteSingleConversation(userName, "");
                                    initConversion();//重新加载本地会话
                                }
                            });
                    builder.show();

                }
                //如果点击的是群聊
                if (conversation.getType().toString().equals("group")) {
                    final GroupInfo groupInfo = (GroupInfo) conversation.getTargetInfo();

                    AlertDialog.Builder builder = new AlertDialog.Builder(conversion.this);
                    builder.setTitle("是否删除会话").setIcon(
                            R.mipmap.logo).setNegativeButton("否", null);
                    builder.setPositiveButton("是",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    JMessageClient.deleteGroupConversation(groupInfo.getGroupID());
                                    initConversion();//重新加载本地会话
                                }
                            });
                    builder.show();
                }
            }
        });
    }
    
    //获取系统消息
    public void getSystemMessage(){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("receiveAccount",getSharedPreferences("data",MODE_PRIVATE).getString("account",null));
        client.post(constant.BASE_URL + constant.message_get,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    int i=0;
                    List<message> messageList = JsonUtil.getListFromJson(json,new TypeToken<List<message>>(){}.getType());
                    for (message m:messageList) {
                        if (m.getResult()==0){
                            i++;
                        }
                    }
                    if (i!=0){
                        systemMessageNum.setText(i+"");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(conversion.this, "请刷新重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //收到新消息后刷新会话列表
    public void onEventMainThread(MessageEvent event) {
        initConversion();
    }
}