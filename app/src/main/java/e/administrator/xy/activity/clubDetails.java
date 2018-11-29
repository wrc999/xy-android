package e.administrator.xy.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.club;
import e.administrator.xy.util.ClubAdapter;
import e.administrator.xy.util.ClubMemberAdapter;
import e.administrator.xy.util.ClubPicAdapter;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.constant;

public class clubDetails extends AppCompatActivity implements View.OnClickListener {
    private TextView slogan,intro,name,school,member,clubDetailFocus,clubDetailJoin,noneClubPic,clunDetailPicNum,clunDetailActivity;
    private ImageView ava;
    private LinearLayout memberDetail,clubPicDetail,clubActivityDetail,clubDetailChat;
    private RelativeLayout clubDetail_goBack;
    private RecyclerView mRecyclerView,pRecyclerView;//recyvleview控件
    club c = new club();
    private int club_id;
    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club_details);
        initView();
        initData();
    }

    private void initData() {
        final SharedPreferences sp = getSharedPreferences("data",MODE_PRIVATE);
        club_id = getIntent().getIntExtra("club_id", 0);
        account = getIntent().getStringExtra("account");
        final AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("club_id",club_id);
        client.post(constant.BASE_URL + constant.club_findById, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody, "utf-8");
                    c = JsonUtil.getObjectFromJson(json, club.class);
                    //口号
                    slogan.setText(c.getClubSlogan());
                    //社团名
                    name.setText(c.getClubName());
                    //社团头像
                    Glide.with(clubDetails.this).load(c.getClubAva()).into(ava);
                    //成员
                    ClubMemberAdapter adapter = new ClubMemberAdapter(clubDetails.this,c.getMemberList());
                    mRecyclerView.setAdapter(adapter);
                    if (c.getViewList().size()>0){
                        ClubPicAdapter adapter1 = new ClubPicAdapter(clubDetails.this,c.getViewList());
                        pRecyclerView.setAdapter(adapter1);
                        clunDetailPicNum.setText(c.getViewList().size()+"张");
                    }else {
                        noneClubPic.setVisibility(View.VISIBLE);
                    }
                    member.setText(c.getMemberList().size()+"人");
                    //学校
                    school.setText(c.getClubSchool());
                    //介绍
                    intro.setText(c.getClubIntro());
                    if(c.getActivityList()!=null){
                        clunDetailActivity.setText(c.getActivityList().size()+"个");
                    }
                    ///关注
                    if (c.getFocusList()!=null){
                        //todo:动态改变关注控件
                        for (int i=0;i<c.getFocusList().size();i++){
                            //如果分享中的点赞名单与登录用户的昵称相同，则更改点赞颜色
                            if (c.getFocusList().get(i).getAccount().equals(sp.getString("account",null))){
                                clubDetailFocus.setText("已关注");
                                clubDetailFocus.setTag("unfocus");
                                clubDetailFocus.setBackgroundResource(R.drawable.unfocus_button);
                            }
                        }
                    }
                    ///申请加入
                    if (c.getMemberList()!=null){
                        //todo:动态改变关注控件
                        for (int i=0;i<c.getMemberList().size();i++){
                            //如果分享中的点赞名单与登录用户的昵称相同，则更改点赞颜色
                            if (c.getMemberList().get(i).getAccount().equals(sp.getString("account",null))){
                                clubDetailJoin.setText("已加入");
                                clubDetailJoin.setTag("unjoin");
                                clubDetailJoin.setBackgroundResource(R.drawable.gray_button);
                            }
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(clubDetails.this, "请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initView() {
        mRecyclerView = findViewById(R.id.clubMember);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        pRecyclerView = findViewById(R.id.clubPicRecyclerView);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        pRecyclerView.setLayoutManager(linearLayoutManager1);
        clubDetail_goBack = findViewById(R.id.clubDetail_goBack);
        clubDetail_goBack.setOnClickListener(this);
        clunDetailPicNum = findViewById(R.id.clunDetailPicNum);
        noneClubPic = findViewById(R.id.noneClubPic);
        slogan = findViewById(R.id.clubDetailSlogan);
        intro = findViewById(R.id.clubDetailIntro);
        name = findViewById(R.id.clubDetailName);
        ava = findViewById(R.id.clubDetailAva);
        ava.setOnClickListener(this);
        school = findViewById(R.id.clubDetailSchool);
        member = findViewById(R.id.clubDetailMember);
        clubDetailChat = findViewById(R.id.clubDetailChat);
        clubDetailChat.setOnClickListener(this);
        memberDetail = findViewById(R.id.memberDetail);
        memberDetail.setOnClickListener(this);
        clubPicDetail = findViewById(R.id.clubPicDetail);
        clubPicDetail.setOnClickListener(this);
        clubActivityDetail = findViewById(R.id.clubActivityDetail);
        clubActivityDetail.setOnClickListener(this);
        clubDetailFocus = findViewById(R.id.clubDetailFocus);
        clubDetailFocus.setOnClickListener(this);
        clubDetailJoin = findViewById(R.id.clubDetailJoin);
        clubDetailJoin.setOnClickListener(this);
        clunDetailActivity = findViewById(R.id.clunDetailActivity);
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.clubDetailAva:
                    ArrayList<String> imageList = new ArrayList<String>();
                    imageList.add(c.getClubAva());
                    Intent intent = new Intent(clubDetails.this, ImageBrowseActivity.class);
                    intent.putStringArrayListExtra("imageList", imageList);
                    startActivity(intent);
                    break;
                //返回
                case R.id.clubDetail_goBack:
                    Runtime runtime = Runtime.getRuntime();
                    try {
                        runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                //成员列表
                case R.id.memberDetail:
                    Intent intent0 = new Intent(this,clubMember.class);
                    //传递对象intent0.putExtra("memberList",(Serializable) c.getMemberList());
                    intent0.putExtra("resource","club");
                    intent0.putExtra("groupID",Long.valueOf(c.getGroupId()));
                    startActivity(intent0);
                    break;
                //社团照片
                case R.id.clubPicDetail:
                    Intent intent1 = new Intent(this, ViewListActivity.class);
                    intent1.putExtra("clubName",c.getClubName());
                    intent1.putExtra("club_id",c.getClub_id());
                    startActivity(intent1);
                    break;
                //社团活动
                case R.id.clubActivityDetail:
                    Intent intent2 =  new Intent(clubDetails.this,clubActivityList.class);
                    intent2.putExtra("account",c.getAccount());
                    startActivity(intent2);
                    break;
                //社团聊天
                case R.id.clubDetailChat:
                    //如果不是该社团成员禁止进入聊天
                    for (int i=0;i<c.getMemberList().size();i++){
                        if (c.getMemberList().get(i).getAccount().equals(JMessageClient.getMyInfo().getUserName())){
                            Intent intent3 =  new Intent(clubDetails.this,ChatRoomActivity.class);
                            intent3.putExtra("groupId",Long.valueOf(c.getGroupId()));
                            intent3.putExtra("groupName",c.getClubName());
                            intent3.putExtra("type","group");
                            startActivity(intent3);
                            return;
                        }
                    }
                    Toast.makeText(this, "您不是该社团成员，不能进入群聊", Toast.LENGTH_SHORT).show();
                    break;
                //关注
                case R.id.clubDetailFocus:
                    SharedPreferences sp = getSharedPreferences("data",MODE_PRIVATE);
                    //判断是否关注
                    if (clubDetailFocus.getTag().equals("focus")){
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        params.put("account",sp.getString("account",null));
                        params.put("club_id",club_id);
                        client.post(constant.BASE_URL + constant.club_focus, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try {
                                    String json = new String(responseBody,"utf-8");
                                    if (!json.isEmpty()){
                                        clubDetailFocus.setText("已关注");
                                        clubDetailFocus.setTag("unfocus");
                                        clubDetailFocus.setBackgroundResource(R.drawable.focus_button);
                                    }else {
                                        Toast.makeText(getApplicationContext(), "取消失败", Toast.LENGTH_SHORT).show();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(getApplicationContext(), "请刷新重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        params.put("account",sp.getString("account",null));
                        params.put("club_id",club_id);
                        client.post(constant.BASE_URL + constant.club_unfocus, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try {
                                    String json = new String(responseBody,"utf-8");
                                    if (!json.isEmpty()){
                                        clubDetailFocus.setText("+关注");
                                        clubDetailFocus.setTag("focus");
                                        clubDetailFocus.setBackgroundResource(R.drawable.radius);
                                    }else {
                                        Toast.makeText(getApplicationContext(), "关注失败", Toast.LENGTH_SHORT).show();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(getApplicationContext(), "请刷新重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    break;
                //申请加入
                case R.id.clubDetailJoin:
                    //判断是否加入
                    SharedPreferences sp1 = getSharedPreferences("data",MODE_PRIVATE);
                    if (clubDetailJoin.getTag().equals("join")){
                        AsyncHttpClient client1 = new AsyncHttpClient();
                        RequestParams params1 = new RequestParams();
                        params1.put("sendAccount",sp1.getString("account",null));
                        params1.put("receiveAccount",account);
                        params1.put("club_id",club_id);
                        params1.put("root","社团申请");
                        params1.put("reason",c.getClubName());
                        client1.post(constant.BASE_URL + constant.message_add,params1, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try {
                                    String json = new String(responseBody,"utf-8");
                                    if (json!=null){
                                        Toast.makeText(clubDetails.this, "申请成功，请等待社长同意", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(getApplicationContext(), "请刷新重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        AsyncHttpClient client1 = new AsyncHttpClient();
                        RequestParams params1 = new RequestParams();
                        params1.put("account",sp1.getString("account",null));
                        params1.put("club_id",club_id);
                        client1.post(constant.BASE_URL + constant.club_quit,params1, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try {
                                    String json = new String(responseBody,"utf-8");
                                    if (json!=null){
                                        clubDetailJoin.setText("申请加入");
                                        clubDetailJoin.setTag("join");
                                        clubDetailJoin.setBackgroundResource(R.drawable.join_button);
                                        Toast.makeText(clubDetails.this, "退出成功", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(getApplicationContext(), "请刷新重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    break;
            }
    }
}
