package e.administrator.xy.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.activity;
import e.administrator.xy.util.GlideImageLoader;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.activityMemberAdapter;
import e.administrator.xy.util.constant;

public class activityDetails extends AppCompatActivity implements View.OnClickListener {
    private TextView name,time,place,intro,join,praiseNum,peopleNum;
    private LinearLayout activityDetailKeyWord,praiseClick,activityDetailPlaceLinear,memberDetail;
    private RelativeLayout returnActivity;
    private MaterialRefreshLayout refreshLayout;    // 下拉刷新控件
    private Banner banner;  // 广告栏控件
    private List<String> bannerImgList = new ArrayList<String>();//轮播图片
    private RecyclerView mRecyclerView;//recyvleview控件

    private int activity_id;
    private String account;
    private activity a = new activity();//接受后台传递的活动实体
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initView();
        initData();
        initRefreshLayout();
    }

    private void initRefreshLayout() {
        refreshLayout.setIsOverLay(false);  // 是否是侵入式下拉刷新
        refreshLayout.setWaveShow(true);    // 是否显示波浪纹
        refreshLayout.setLoadMore(false);   // 是否需要上拉加载
        refreshLayout.setWaveColor(getResources().getColor(R.color.blue));   // 设置波浪纹颜色
        // 注册下拉刷新监听器
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            // 下拉刷新执行的方法
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                initData(); // 重新加载数据
                refreshLayout.finishRefresh();
                Toast.makeText(activityDetails.this, "成功刷新", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        memberDetail = findViewById(R.id.memberDetail);
        mRecyclerView = findViewById(R.id.activityDetailMember);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //水平展示
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        banner = findViewById(R.id.activityDetailBanner);
        refreshLayout = findViewById(R.id.activityDetilsRefresh);
        name = findViewById(R.id.activityDetailName);
        activityDetailKeyWord = findViewById(R.id.activityDetailKeyWord);
        time = findViewById(R.id.activityDetailTime);
        activityDetailPlaceLinear = findViewById(R.id.activityDetailPlaceLinear);
        place = findViewById(R.id.activityDetailPlace);
        intro = findViewById(R.id.activityDetailIntro);
        returnActivity = findViewById(R.id.activityDetail_goBack);
        join = findViewById(R.id.activityDetailjoin);
        praiseClick = findViewById(R.id.activityDetailLoveNum);
        praiseNum = findViewById(R.id.activityDetailPraise);
        peopleNum = findViewById(R.id.activityDetailPeopleNum);
        memberDetail.setOnClickListener(this);
        join.setOnClickListener(this);
        praiseClick.setOnClickListener(this);
        returnActivity.setOnClickListener(this);
    }

    private void initData() {
        activity_id = getIntent().getIntExtra("activity_id",-1);
        account = getIntent().getStringExtra("account");
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("activity_id",activity_id);
        client.post(constant.BASE_URL + constant.activity_getByActivityId, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    a = JsonUtil.getObjectFromJson(json,activity.class);

                    SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
                    initBanner();
                    name.setText(a.getName());;
                    time.setText(a.getStarttime()+"--"+a.getEndtime());
                    praiseNum.setText(a.getaAccount().size()+"");
                    //如果为线上，则隐藏地点，否则显示
                    if (getIntent().getIntExtra("onLine",-1)==1){
                        activityDetailPlaceLinear.setVisibility(View.GONE);
                    }else {
                        place.setText(a.getPlace());
                    }
                    intro.setText(a.getIntro());
                    peopleNum.setText(a.getjAccount().size()+"/"+a.getPeopleNum());
                    //判断当前用户是否报名
                    for (String a: a.getjAccount()){
                        if (a.equals(sp.getString("account",null))){
                            join.setTag("unadd");
                            join.setText("退出活动");
                            break;
                        }else {
                            join.setTag("add");
                            join.setText("立即报名");
                        }
                    }
                    //判断当前用户是否点赞
                    for (String a: a.getaAccount()){
                        if (a.equals(sp.getString("account",null))){
                            praiseNum.setTag("unpraise");
                            break;
                        }else {
                            praiseNum.setTag("praise");
                        }
                    }

                    //获取关键字
                    String[] keyword = a.getKeyword().split(",");
                    for (int i=0;i<keyword.length;i++){
                        TextView badge = new TextView(activityDetails.this);
                        badge.setText(keyword[i]);
                        badge.setTextSize(12);
                        badge.setPadding(20,10,20,10);
                        GradientDrawable drawable=new GradientDrawable();
                        drawable.setCornerRadius(30);
                        switch (keyword[i]){
                            case "运动":drawable.setColor(Color.rgb(50,205,50));break;
                            case "娱乐":drawable.setColor(Color.rgb(64,224,208));break;
                            case "日常运动":drawable.setColor(Color.rgb(255,100,97));break;
                        }
                        badge.setBackground(drawable);
                        activityDetailKeyWord.addView(badge);
                    }

                    //获取报名名单
                    activityMemberAdapter adapter = new activityMemberAdapter(activityDetails.this,a.getjAccount());
                    mRecyclerView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Toast.makeText(activityDetails.this, "请重试", Toast.LENGTH_SHORT).show();
        }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activityDetail_goBack:
//                Intent intent = new Intent(activityDetails.this,MainActivity.class);
//                intent.putExtra("id",3);
//                startActivity(intent);
                //调用手机自带返回键
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
                //活动成员
            case R.id.memberDetail:
                Intent intent0 = new Intent(this,clubMember.class);
                //传递对象intent0.putExtra("memberList",(Serializable) c.getMemberList());
                intent0.putExtra("resource","activity");
                intent0.putStringArrayListExtra("member", a.getjAccount());
                intent0.putExtra("applyPeople",a.getAccount());
                startActivity(intent0);
                break;
            case R.id.activityDetailjoin:
                //申请报名活动
                if (join.getTag().equals("add")){
//                    SharedPreferences sp1 = getSharedPreferences("data",MODE_PRIVATE);
//                    AsyncHttpClient client1 = new AsyncHttpClient();
//                    RequestParams params1 = new RequestParams();
//                    params1.put("sendAccount",sp1.getString("account",null));
//                    params1.put("receiveAccount",account);
//                    params1.put("activity_id",activity_id);
//                    params1.put("root","活动申请");
//                    params1.put("reason",a.getName());
//                    client1.post(constant.BASE_URL + constant.message_add,params1, new AsyncHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                            try {
//                                String json = new String(responseBody,"utf-8");
//                                if (json!=null){
//                                    Toast.makeText(activityDetails.this, "申请成功，请等待发起人同意", Toast.LENGTH_SHORT).show();
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                            Toast.makeText(getApplicationContext(), "请刷新重试", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                    if (a.getjAccount().size()<a.getPeopleNum()){
                        // 构建一个popupwindow的布局
                        View popupView = activityDetails.this.getLayoutInflater().inflate(R.layout.popupwindow_activityapply, null);
                        // 创建PopupWindow对象，指定宽度和高度
                        final PopupWindow window = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        // 设置动画
                        window.setAnimationStyle(R.style.popup_window_anim);
                        // 设置背景颜色
                        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
                        //只有同时都设置了以下两个属性，你点击popupwindow以外的区域时该popupwindow才会消失
                        // 1.设置可以触摸弹出框以外的区域
                        window.setOutsideTouchable(true);
                        // 2.设置可以获取焦点
                        window.setFocusable(true);
                        // 设置可以触摸弹出框以外的区域
                        window.setOutsideTouchable(true);
                        // TODO：更新popupwindow的状态
                        window.update();
                        // 以下拉的方式显示，并且可以设置显示的位置
                        window.showAtLocation(join,Gravity.CENTER,0,0);
                        //1.为添加好友按钮添加点击事件
                        final EditText name = popupView.findViewById(R.id.popupwidow_name);
                        final EditText contaact = popupView.findViewById(R.id.popupwidow_contact);
                        popupView.findViewById(R.id.activityApply).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPreferences sp = getSharedPreferences("data",MODE_PRIVATE);
                                String account = sp.getString("account",null);
                                AsyncHttpClient client1 = new AsyncHttpClient();
                                RequestParams params1 = new RequestParams();
                                params1.put("account",account);
                                params1.put("activity_id",a.getActivity_id());
                                params1.put("name",name.getText().toString().trim());
                                params1.put("contact",contaact.getText().toString().trim());
                                client1.post(constant.BASE_URL + constant.activity_joinsAdd, params1, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        String json = new String(responseBody);
                                        if (json!=null){
                                            window.dismiss();
                                            Toast.makeText(activityDetails.this, "已成功加入", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        Toast.makeText(activityDetails.this, "请重试", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }else {
                        Toast.makeText(this, "人数已满", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //退出活动
                    SharedPreferences sp = activityDetails.this.getSharedPreferences("data", Context.MODE_PRIVATE);
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.put("activity_id",a.getActivity_id());
                    params.put("account",sp.getString("account",null));
                    client.post(constant.BASE_URL + constant.activity_joinsDelete, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                String json = new String(responseBody,"utf-8");
                                if (!json.isEmpty()){
                                    join.setTag("add");
                                    join.setText("加入");
                                    Toast.makeText(activityDetails.this, "退出成功", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(activityDetails.this, "退出失败", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(activityDetails.this, "请刷新重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            case R.id.activityDetailLoveNum:
                //活动点赞
                if (praiseNum.getTag().equals("praise")){
                    SharedPreferences sp = activityDetails.this.getSharedPreferences("data", Context.MODE_PRIVATE);
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.put("activity_id",activity_id);
                    params.put("account",sp.getString("account",null));
                    client.post(constant.BASE_URL+constant.activity_praiseAdd,params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                SharedPreferences sp = activityDetails.this.getSharedPreferences("data", Context.MODE_PRIVATE);
                                String json = new String(responseBody,"utf-8");
                                if (!json.isEmpty()){
                                    praiseNum.setTag("unpraise");
                                    praiseNum.setText(a.getaAccount().size()+1+"");
                                    Toast.makeText(activityDetails.this, "点赞成功", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(activityDetails.this, "点赞失败", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(activityDetails.this, "请刷新重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    SharedPreferences sp = activityDetails.this.getSharedPreferences("data", Context.MODE_PRIVATE);
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.put("activity_id",activity_id);
                    params.put("account",sp.getString("account",null));
                    client.post(constant.BASE_URL + constant.activity_praiseDelete, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                String json = new String(responseBody,"utf-8");
                                if (!json.isEmpty()){
                                    praiseNum.setTag("praise");
                                    praiseNum.setText(a.getaAccount().size()-1+"");
                                    Toast.makeText(activityDetails.this, "取消点赞", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(activityDetails.this, "取消失败", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(activityDetails.this, "请刷新重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
    }
    private void initBanner() {
        bannerImgList.add(a.getPic1());
        bannerImgList.add(a.getPic2());
        bannerImgList.add(a.getPic3());
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setImages(bannerImgList);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                ArrayList<String> viewList = new ArrayList<String>();
                viewList.add(a.getPic1());
                viewList.add(a.getPic2());
                viewList.add(a.getPic3());
                Intent intent = new Intent(activityDetails.this, ImageBrowseActivity.class);
                intent.putStringArrayListExtra("imageList", viewList);
                intent.putExtra("index", position);
                startActivity(intent);
            }
        });
        banner.start();
    }
}
