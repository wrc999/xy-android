package e.administrator.xy.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.jpeng.jptabbar.BadgeDismissListener;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.animate.AnimationType;
import com.jpeng.jptabbar.anno.NorIcons;
import com.jpeng.jptabbar.anno.SeleIcons;
import com.jpeng.jptabbar.anno.Titles;
import com.longsh.optionframelibrary.OptionBottomDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.fragment.Tab1Pager;
import e.administrator.xy.fragment.Tab2Pager;
import e.administrator.xy.fragment.Tab3Pager;
import e.administrator.xy.fragment.Tab4Pager;
import e.administrator.xy.util.Adapter;
import e.administrator.xy.util.StatusBarUtils;
import e.administrator.xy.util.constant;
import e.administrator.xy.util.qiniuUtil.Auth;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Titles
    private static final int[] mTitles = {R.string.tab1, R.string.tab2, R.string.tab3, R.string.tab4};

    @SeleIcons
    private static final int[] mSeleIcons = {R.mipmap.message_select, R.mipmap.share_select, R.mipmap.photo_select, R.mipmap.mine_select};

    @NorIcons
    private static final int[] mNormalIcons = {R.mipmap.message, R.mipmap.share, R.mipmap.photo, R.mipmap.mine};

    private List<Fragment> list = new ArrayList<>();

    private ViewPager mPager;

    private static JPTabBar mTabbar;

    private Tab1Pager mTab1;

    private Tab2Pager mTab2;

    private Tab3Pager mTab3;

    private Tab4Pager mTab4;

    private ImageView add,chat;
    int unReadNum = 0;//消息未读数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavigate();//初始导航界面
        initJump();//初始跳转
        methodRequiresTwoPermission();
    }
    @AfterPermissionGranted(1)//添加注解，是为了首次执行权限申请后，回调该方法
    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            //已经申请过权限，直接调用相机
            //openCamera();
        } else {
            EasyPermissions.requestPermissions(this, "需要获取权限",
                    1, perms);
        }
    }

    private void initJump() {
        add = (ImageView) findViewById(R.id.add);
        add.setOnClickListener(this);
        chat = (ImageView) findViewById(R.id.main_chat);
        chat.setOnClickListener(this);
        unReadNum = JMessageClient.getAllUnReadMsgCount();
        if (unReadNum>0){
            //设置消息角标
            new QBadgeView(this).bindTarget(chat).setBadgeNumber(unReadNum)
                    .setBadgeTextColor(Color.RED)
                    .setGravityOffset(5,true)
                    .setBadgeGravity(Gravity.START | Gravity.TOP)
                    .setBadgeBackgroundColor(Color.TRANSPARENT)
                    .setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                        @Override
                        public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                            if(unReadNum>0) {
                                List<Conversation> conversationList = JMessageClient.getConversationList();
                                for (Conversation conversation:conversationList) {
                                    //进入会话后重置会话的未读数
                                    conversation.resetUnreadCount();
                                }
                            }
                        }
                    })
                    .setShowShadow(false);
        }else {
            new QBadgeView(this).bindTarget(chat).hide(true);
        }

        //设置状态栏颜色
        StatusBarUtils.setStatusBarColor(this,R.color.white);
        //控制页面跳转
        int id = getIntent().getIntExtra("id", -1);
        if ((id/10) == 2) {
            //1是指底部第2个Fragment
            mPager.setCurrentItem(1);
        }
        if (id == 3) {
            //2是指底部第3个Fragment
            mPager.setCurrentItem(2);
        }
        if (id == 4) {
            //3是指底部第4个Fragment
            mPager.setCurrentItem(3);
        }
    }

    //添加滑动和底部导航
    private void initNavigate() {
        mTabbar = (JPTabBar) findViewById(R.id.tabbar);
        mPager = (ViewPager) findViewById(R.id.view_pager);
        //设置Tab标题字体类型
        mTabbar.setTabTypeFace("fonts/Jaden.ttf");
        mTab1 = new Tab1Pager();
        mTab2 = new Tab2Pager();
        mTab3 = new Tab3Pager();
        mTab4 = new Tab4Pager();
        mTabbar.setGradientEnable(true);
        mTabbar.setPageAnimateEnable(true);
        SharedPreferences sp = getApplicationContext().getSharedPreferences("settig", Context.MODE_PRIVATE);
        String aimation = sp.getString("aimation", String.valueOf(AnimationType.SCALE2));
        AnimationType animationType =  AnimationType.valueOf(aimation);
        mTabbar.setAnimation(animationType);
        list.add(mTab1);
        list.add(mTab2);
        list.add(mTab3);
        list.add(mTab4);

        mPager.setAdapter(new Adapter(getSupportFragmentManager(), list));
        //实现渐变、自动让ViewPager改变页面功能
        mTabbar.setContainer(mPager);

    }

    public static JPTabBar getTabbar() {
        return mTabbar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_chat:
                Intent intent = new Intent(MainActivity.this,conversion.class);
                startActivity(intent);
                break;
            //加号弹出窗
            case R.id.add:
                // 构建一个popupwindow的布局
                View popupView = MainActivity.this.getLayoutInflater().inflate(R.layout.popupwindow, null);
                // 创建PopupWindow对象，指定宽度和高度
                PopupWindow window = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                window.showAsDropDown(add, 0, 40);
                //1.为添加好友按钮添加点击事件
                popupView.findViewById(R.id.add1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
                        startActivity(intent);
                    }
                });
                //2.为发布分享按钮添加点击事件
                popupView.findViewById(R.id.add2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, TalkAddActivity.class);
                        startActivity(intent);
                    }
                });
                //3.为发布活动按钮添加点击事件
                popupView.findViewById(R.id.add3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent(MainActivity.this, ActivityAddActivity.class);
                        startActivity(intent1);
                    }
                });
                //4.为创建社团按钮添加点击事件
                popupView.findViewById(R.id.add4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,clubAdd.class);
                        startActivity(intent);
                    }
                });
                //4.为话题分享按钮添加点击事件
                popupView.findViewById(R.id.add5).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,topicAdd.class);
                        startActivity(intent);
                    }
                });
        }
    }
}