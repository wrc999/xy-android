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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;
import e.administrator.xy.R;
import e.administrator.xy.pojo.User;
import e.administrator.xy.pojo.userInfo;
import e.administrator.xy.util.FriendListAdapter;
import e.administrator.xy.util.SideBar;
import e.administrator.xy.view.RecycleViewDivider;

public class MyFriend extends AppCompatActivity {
    private RecyclerView rvShowMyFriend;
    private SideBar sideBar;
    private RelativeLayout returnYj;
    private ArrayList<User> UserList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_friend);
        initView();
        initData();
    }

    private void initView() {
        returnYj = findViewById(R.id.returnYj);
        returnYj.setOnClickListener(new View.OnClickListener() {
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
        rvShowMyFriend = findViewById(R.id.rv_my_friend);
        sideBar = (SideBar) findViewById(R.id.side_bar);
        //跳转到选择的字母位置
        sideBar.setOnStrSelectCallBack(new SideBar.ISideBarSelectCallBack() {
            @Override
            public void onSelectStr(int index, String selectStr) {
                for (int i = 0; i < UserList.size(); i++) {
                    if (selectStr.equalsIgnoreCase(UserList.get(i).getFirstLetter())) {
                        rvShowMyFriend.scrollToPosition(i); // 选择到首字母出现的位置
                        return;
                    }
                }
            }
        });
    }

    private void initData() {
        //获取好友列表，异步返回结果
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int i, String s, final List<UserInfo> list) {
                if (i == 0){
                    for (UserInfo u:list){
                        UserList.add(new User(u.getNickname(),u.getUserName(),u.getAvatarFile()));
                    }
                    Collections.sort(UserList);
                    rvShowMyFriend.setLayoutManager(new LinearLayoutManager(MyFriend.this));
                    rvShowMyFriend.addItemDecoration(new RecycleViewDivider(MyFriend.this, LinearLayoutManager.HORIZONTAL,1,getResources().getColor((R.color.txtGray))));
                    FriendListAdapter friendListAdapter = new FriendListAdapter(MyFriend.this,UserList);
                    rvShowMyFriend.setAdapter(friendListAdapter);
                    //列表点击监听
                    friendListAdapter.setOnItemClickListener(new FriendListAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(String userName) {
                            Intent intent = new Intent(MyFriend.this,friendInfo.class);
                            intent.putExtra("userName",userName);
                            startActivity(intent);
                        }
                        @Override
                        public void onLongClick(String userName) {
                            Toast.makeText(MyFriend.this,"功能待定",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(MyFriend.this, "获取失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
