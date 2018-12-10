package e.administrator.xy.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.animate.AnimationType;

import java.io.IOException;

import e.administrator.xy.R;
import e.administrator.xy.pojo.userInfo;

public class jumpAnimation extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private RelativeLayout returnSetting;
    private RadioGroup mGroup;

    private JPTabBar mTabBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jump_animation);
        returnSetting = findViewById(R.id.returnSetting);
        mGroup = findViewById(R.id.radioGroup);
        mTabBar = MainActivity.getTabbar();
        returnSetting.setOnClickListener(this);
        mGroup.setOnCheckedChangeListener(this);
        SharedPreferences sp = getApplicationContext().getSharedPreferences("settig", Context.MODE_PRIVATE);
        String aimation = sp.getString("aimation", String.valueOf(AnimationType.SCALE2));
        AnimationType animationType =  AnimationType.valueOf(aimation);
        if (animationType.equals(AnimationType.SCALE)){mGroup.check(R.id.radioButton1);}
        if (animationType.equals(AnimationType.SCALE2)){mGroup.check(R.id.radioButton2);}
        if (animationType.equals(AnimationType.JUMP)){mGroup.check(R.id.radioButton3);}
        if (animationType.equals(AnimationType.FLIP)){mGroup.check(R.id.radioButton4);}
        if (animationType.equals(AnimationType.ROTATE)){mGroup.check(R.id.radioButton5);}
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.radioButton1:
                mTabBar.setAnimation(AnimationType.SCALE);
                setJumpAnimation(AnimationType.SCALE);
                break;
            case R.id.radioButton2:
                mTabBar.setAnimation(AnimationType.SCALE2);
                setJumpAnimation(AnimationType.SCALE2);
                break;
            case R.id.radioButton3:
                mTabBar.setAnimation(AnimationType.JUMP);
                setJumpAnimation(AnimationType.JUMP);
                break;
            case R.id.radioButton4:
                mTabBar.setAnimation(AnimationType.FLIP);
                setJumpAnimation(AnimationType.FLIP);
                break;
            case R.id.radioButton5:
                mTabBar.setAnimation(AnimationType.ROTATE);
                setJumpAnimation(AnimationType.ROTATE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.returnSetting:
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setJumpAnimation(AnimationType animation){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("settig", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("aimation", String.valueOf(animation));
        edit.commit();
    }
}
