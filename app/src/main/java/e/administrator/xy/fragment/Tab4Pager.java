package e.administrator.xy.fragment;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import cn.jpush.im.android.api.JMessageClient;
import e.administrator.xy.R;
import e.administrator.xy.activity.LoginActivity;
import e.administrator.xy.activity.MyActivity;
import e.administrator.xy.activity.MyTalk;
import e.administrator.xy.activity.MyFriend;
import e.administrator.xy.activity.MyView;
import e.administrator.xy.activity.myClub;
import e.administrator.xy.activity.setting;
import e.administrator.xy.activity.userInfoActivity;


public class Tab4Pager extends Fragment implements View.OnClickListener{
    private TextView nickName,account;
    private ImageView userPic;
    private LinearLayout info;
    private RelativeLayout  myTalk,myActivity,myCLubs,exit,showFriend,settings,myView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.tab4,null);
            init(layout);
            return layout;
        }

    private void init(View layout) {
            nickName = layout.findViewById(R.id.nickName);
            account = layout.findViewById(R.id.account);
            userPic = layout.findViewById(R.id.user_pic);
            info = layout.findViewById(R.id.info);
            myTalk = layout.findViewById(R.id.MyTalk);
            myActivity = layout.findViewById(R.id.MyActivity);
            myCLubs = layout.findViewById(R.id.myCLubs);
            showFriend = layout.findViewById(R.id.myFriend);
            myView = layout.findViewById(R.id.MyView);
            settings = layout.findViewById(R.id.setting);
            exit = layout.findViewById(R.id.exit);
            exit.setOnClickListener(this);
            myCLubs.setOnClickListener(this);
            info.setOnClickListener(this);
            myTalk.setOnClickListener(this);
            myActivity.setOnClickListener(this);
            showFriend.setOnClickListener(this);
            myView.setOnClickListener(this);
            settings.setOnClickListener(this);
            SharedPreferences sp = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
            nickName.setText(sp.getString("nickName","您还未登录"));
            account.setText(sp.getString("account","请先登录"));
            try{
                if (JMessageClient.getMyInfo().getAvatarFile()!=null){
                    Glide.with(getContext()).load(JMessageClient.getMyInfo().getAvatarFile()).into(userPic);
                }else {
                    Intent intent = new Intent(getContext(),LoginActivity.class);
                    startActivity(intent);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.info:
                Intent intent = new Intent(getContext(),userInfoActivity.class);
                startActivity(intent);
                break;
            case  R.id.MyTalk:
                Intent intent1 = new Intent(getContext(),MyTalk.class);
                startActivity(intent1);
                break;
            case  R.id.MyActivity:
                Intent intent2 = new Intent(getContext(),MyActivity.class);
                startActivity(intent2);
                break;
            case R.id.myCLubs:
                Intent intent3 = new Intent(getContext(),myClub.class);
                startActivity(intent3);
                break;
            case R.id.myFriend:
                Intent intent5 = new Intent(getContext(),MyFriend.class);
                startActivity(intent5);
                break;
            case R.id.MyView:
                Intent intent6 = new Intent(getContext(),MyView.class);
                startActivity(intent6);
                break;
            case R.id.setting:
                Intent intent7 = new Intent(getContext(),setting.class);
                startActivity(intent7);
                break;
            case R.id.exit:
                JMessageClient.logout();
                Toast.makeText(getContext(), "登出成功", Toast.LENGTH_SHORT).show();
                Intent intent4 = new Intent(getContext(),LoginActivity.class);
                SharedPreferences sp = getContext().getSharedPreferences("data", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.clear();
                edit.commit();
                startActivity(intent4);
                getActivity().finish();
                break;
        }
    }
}