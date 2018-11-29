package e.administrator.xy.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.util.constant;
import e.administrator.xy.util.qiniuUtil.Constants;

public class changePassWord extends AppCompatActivity implements View.OnClickListener {

    private EditText oldPassWord,newPassWord;
    private RelativeLayout returnSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        oldPassWord = findViewById(R.id.oldPassWord);
        newPassWord = findViewById(R.id.newPassWord);
        returnSetting = findViewById(R.id.changePassWordReturnSetting);
        returnSetting.setOnClickListener(this);

    }

    public void submit(View view) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        SharedPreferences sp = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        params.put("use_id",sp.getInt("use_id",0));
        params.put("passWord",oldPassWord.getText());
        params.put("newPassWord",newPassWord.getText());
        client.post(constant.BASE_URL+constant.user_changePassWord, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody, "utf-8");
                    if (json.equals("1")){
                        JMessageClient.updateUserPassword(oldPassWord.getText().toString(),newPassWord.getText().toString(), new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                Toast.makeText(changePassWord.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(changePassWord.this,setting.class);
                                startActivity(intent);
                            }
                        });
                    }else{
                        Toast.makeText(changePassWord.this, "原密码输入错误", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.changePassWordReturnSetting:
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
