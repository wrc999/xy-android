package e.administrator.xy.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.api.BasicCallback;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.message;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.constant;
import e.administrator.xy.util.systemMessageAdapter;

public class AddFriendActivity extends AppCompatActivity {
    private EditText mEt_userName;
    private EditText mEt_reason;
    private Button mButton;
    private RelativeLayout returnIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        initView();
        initData();
    }

    //发送添加好友请求
    private void initData() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = mEt_userName.getText().toString();
                final String reason = mEt_reason.getText().toString();
                ContactManager.sendInvitationRequest(name, "", reason, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            SharedPreferences sp = getSharedPreferences("data",MODE_PRIVATE);
                            AsyncHttpClient client = new AsyncHttpClient();
                            RequestParams params = new RequestParams();
                            params.put("sendAccount",sp.getString("account",null));
                            params.put("receiveAccount",name);
                            params.put("root","好友申请");
                            params.put("reason",reason);
                            client.post(constant.BASE_URL + constant.message_add,params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    try {
                                        String json = new String(responseBody,"utf-8");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    Toast.makeText(getApplicationContext(), "请刷新重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                            Toast.makeText(getApplicationContext(), "申请成功", Toast.LENGTH_SHORT).show();
                            //调用手机自带返回键,返回上一页面
                            Runtime runtime = Runtime.getRuntime();
                            try {
                                runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (s.equals("already is friend")){
                            Toast.makeText(getApplicationContext(), "TA已经是你的好友了，请勿重复添加", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(), "该用户不存在，请核实该账号是否正确", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        mEt_userName = (EditText) findViewById(R.id.et_user_name);
        mEt_reason = (EditText) findViewById(R.id.et_reason);
        mButton = (Button) findViewById(R.id.bt_add_friend);
        returnIndex = (RelativeLayout) findViewById(R.id.returnIndex);
        returnIndex.setOnClickListener(new View.OnClickListener() {
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
}
