package e.administrator.xy.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.common.Constants;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.userInfo;
import e.administrator.xy.util.constant;

public class friendInfo extends AppCompatActivity {
    private ImageView friendAva;
    private Button deleteFriend,quitMember;
    private TextView nickName, account, friendSex, friendBirth, friendEntryYear,
            friendSchool, friendPerSign, friendHobby, friendHomeTown;
    private String userName = null;
    private Long groupID;
    private RelativeLayout friendInfo_returnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        initView();
        initData();
    }

    private void initView() {
        friendAva = findViewById(R.id.friendAva);
        nickName = findViewById(R.id.nickName);
        account = findViewById(R.id.account);
        friendSex = findViewById(R.id.friendSex);
        friendBirth = findViewById(R.id.friendBirth);
        friendEntryYear = findViewById(R.id.friendEntryYear);
        friendSchool = findViewById(R.id.friendSchool);
        friendPerSign = findViewById(R.id.friendPerSign);
        friendHobby = findViewById(R.id.friendHobby);
        friendHomeTown = findViewById(R.id.friendHomeTown);
        deleteFriend = findViewById(R.id.deleteFriend);
        quitMember = findViewById(R.id.quitMember);
        friendInfo_returnback = findViewById(R.id.friendInfo_returnback);
        friendInfo_returnback.setOnClickListener(new View.OnClickListener() {
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

    private void initData() {
        //todo:通过账号名读取极光中的用户头像
        userName = getIntent().getStringExtra("userName");
        groupID = getIntent().getLongExtra("groupID",0);
        JMessageClient.getUserInfo(userName, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                Glide.with(friendInfo.this).load(userInfo.getAvatarFile()).into(friendAva);
                nickName.setText(userInfo.getNickname());
                account.setText(userInfo.getUserName());
                friendSex.setText(userInfo.getExtra("sex"));
                friendBirth.setText(userInfo.getExtra("birth"));
                friendEntryYear.setText(userInfo.getExtra("entryYear"));
                friendSchool.setText(userInfo.getExtra("school") + "-" + userInfo.getExtra("academy") + "-" + userInfo.getExtra("major"));
                friendPerSign.setText(userInfo.getExtra("perSign"));
                friendHobby.setText(userInfo.getExtra("hobby"));
                friendHomeTown.setText(userInfo.getExtra("homeTown"));

            }
        });
        //如果该好友是自己的好友，则显示删除好友选项，否则隐藏
        ContactManager.getFriendList(new GetUserInfoListCallback() {
            @Override
            public void gotResult(int i, String s, List<UserInfo> list) {
                for(UserInfo u : list){
                    if (u.getUserName().equals(userName)){
                        deleteFriend.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        //如果该登录用户为群主，则显示请退成员选项
        if (groupID>0){
            JMessageClient.getGroupInfo(groupID, new GetGroupInfoCallback() {
                @Override
                public void gotResult(int i, String s, GroupInfo groupInfo) {
                    String account = getSharedPreferences("data",MODE_PRIVATE).getString("account",null);
                    if (groupInfo.getGroupOwner().equals(account) && !userName.equals(account)){
                        quitMember.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public void sendMessage(View view) {
        String localAccount = getSharedPreferences("data",MODE_PRIVATE).getString("account",null);
        if (localAccount.equals(userName)){
            Toast.makeText(this, "不能给自己发送信息噢", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(friendInfo.this, ChatRoomActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("nickName", nickName.getText().toString());
            bundle.putString("userName", userName);
            intent.putExtra("type", "single");
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void deleteFriend(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(friendInfo.this);
        builder.setTitle("是否删除该好友？").setIcon(
                R.mipmap.logo).setNegativeButton("否", null);
        builder.setPositiveButton("是",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        JMessageClient.getUserInfo(userName, new GetUserInfoCallback() {
                            @Override
                            public void gotResult(int i, String s, UserInfo userInfo) {
                                userInfo.removeFromFriendList(new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String s) {
                                        if (0 == responseCode) {
                                            Toast.makeText(friendInfo.this, "删除成功", Toast.LENGTH_SHORT).show();
                                            //调用手机自带返回键
                                            Runtime runtime = Runtime.getRuntime();
                                            try {
                                                runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            Toast.makeText(friendInfo.this, "删除失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
        builder.show();
    }

    public void quitMember(View view) {
        final List<String> list = new ArrayList<String>();
        list.add(userName);
        AlertDialog.Builder builder = new AlertDialog.Builder(friendInfo.this);
        builder.setTitle("是否将该成员移除？").setIcon(
                R.mipmap.logo).setNegativeButton("否", null);
        builder.setPositiveButton("是",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        params.put("account",userName);
                        params.put("groupId",groupID);
                        client.post(constant.BASE_URL + constant.club_quit, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                JMessageClient.removeGroupMembers(groupID, list, new BasicCallback() {
                                    @Override
                                    public void gotResult(int i, String s) {
                                        if (i==0){
                                            Toast.makeText(friendInfo.this, "请退成功", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(friendInfo.this, "请重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                        
                    }
                });
        builder.show();
    }
}