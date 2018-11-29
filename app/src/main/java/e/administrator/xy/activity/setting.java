package e.administrator.xy.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cn.jpush.im.android.api.JMessageClient;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.util.constant;

public class setting extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout jump,changePassWord,aboutXY,suggest,returnLogin,exit;
    private LinearLayout returnTab4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        initView();
    }

    private void initView() {
        jump = findViewById(R.id.jump);
        changePassWord = findViewById(R.id.changePassWord);
        aboutXY = findViewById(R.id.aboutXY);
        suggest = findViewById(R.id.suggest);
        returnLogin = findViewById(R.id.returnLogin);
        exit = findViewById(R.id.exit);
        returnTab4 = findViewById(R.id.returnTab4);
        jump.setOnClickListener(this);
        changePassWord.setOnClickListener(this);
        aboutXY.setOnClickListener(this);
        suggest.setOnClickListener(this);
        returnLogin.setOnClickListener(this);
        exit.setOnClickListener(this);
        returnTab4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.returnTab4:
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.jump:
                Intent intent1 = new Intent(this,jumpAnimation.class);
                startActivity(intent1);
                break;
            case R.id.changePassWord:
                Intent intent2 = new Intent(this,changePassWord.class);
                startActivity(intent2);
                break;
            case R.id.aboutXY:
                AlertDialog dialog = new AlertDialog.Builder(this).create();
                dialog.setMessage("享悦是为各大高校的同学提供一个交流互动的平台，让他们双方都能够拥有一个互相交流的机会，能够通过我们的这个平台可以与其他高校的同学增加友谊，同时也能使考研同学更好的了解自己的考研学校，提前了解该学校的学习氛围以及宿舍情况。");
                dialog.setTitle("享悦简介");
                dialog.setIcon(R.mipmap.logo);
                dialog.show();
                break;
            case R.id.suggest:
                final EditText inputServer = new EditText(this);
                inputServer.setFocusable(true);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("请输入您的宝贵意见").setIcon(
                        R.mipmap.logo).setView(inputServer).setNegativeButton("取消", null);
                builder.setPositiveButton("发布",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sp = getApplicationContext().getSharedPreferences("data",Context.MODE_PRIVATE);
                                final String suggest = inputServer.getText().toString();
                                AsyncHttpClient client = new AsyncHttpClient();
                                RequestParams params = new RequestParams();
                                params.put("use_id",sp.getInt("use_id",0));
                                params.put("suggestcontent",suggest);
                                client.post(constant.BASE_URL + constant.user_suggestAdd, params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        try {
                                            String json = new String(responseBody,"utf-8");
                                            if (!json.isEmpty()){
                                                Toast.makeText(setting.this, "感谢您的反馈，我们将竭诚为您服务", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                    }
                                });
                            }
                        });
                builder.show();
                break;
            case R.id.returnLogin:
                JMessageClient.logout();
                Toast.makeText(this, "登出成功", Toast.LENGTH_SHORT).show();
                Intent intent5 = new Intent(this,LoginActivity.class);
                SharedPreferences sp = this.getSharedPreferences("data", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.clear();
                edit.commit();
                startActivity(intent5);
                this.finish();
                break;
            case R.id.exit:
                System.exit(0);
                break;
        }
    }
}
