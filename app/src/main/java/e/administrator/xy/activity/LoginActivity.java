package e.administrator.xy.activity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Explode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.userInfo;
import e.administrator.xy.pojo.view;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.StatusBarUtils;
import e.administrator.xy.util.constant;

public class LoginActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog = null;

    private Button btnLogin;
    private CardView cv;
    private FloatingActionButton fab;

    public static EditText account,passWord;
    private String action = "user_login";//资源访问接口
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        JMessageClient.init(this,true);
        init();
        setListener();
    }

    private void init() {
        //设置状态栏颜色
        StatusBarUtils.setStatusBarColor(this,R.color.black_de);
        account = (EditText) findViewById(R.id.login_username);
        passWord = (EditText) findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btn_login);
        cv = findViewById(R.id.cv);
        cv.getBackground().setAlpha(100);//添加透明度
        fab = findViewById(R.id.fab);
        /**=================     获取个人信息不是null，说明已经登陆，无需再次登陆，则直接进入type界面    =================*/
        UserInfo myInfo = JMessageClient.getMyInfo();
        if (myInfo != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (account.length()<1){
                    Toast.makeText(LoginActivity.this, "账号不能为空哟", Toast.LENGTH_SHORT).show();
                }else if (passWord.length()<1){
                    Toast.makeText(LoginActivity.this, "密码不能为空哟", Toast.LENGTH_SHORT).show();
                }else {
                    mProgressDialog = ProgressDialog.show(LoginActivity.this, "提示：", "正在加载中。。。");
                    mProgressDialog.setCanceledOnTouchOutside(true);
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    final String userName = account.getText().toString();
                    final String password = passWord.getText().toString();
                    params.put("account",userName);
                    params.put("passWord",password);
                    client.post(constant.BASE_URL+constant.user_login,params, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                String json = new String(responseBody, "utf-8");
                                userInfo userInfo = JsonUtil.getObjectFromJson(json, userInfo.class);
                                if (userInfo != null) {
                                    SharedPreferences sp = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor edit = sp.edit();
                                    edit.putInt("use_id", userInfo.getUse_id());
                                    edit.putString("account", userInfo.getAccount());
                                    edit.putString("avaPath", userInfo.getAvaPath());
                                    edit.putString("nickName", userInfo.getNickName());
                                    edit.putString("birth", userInfo.getBirth());
                                    edit.putString("sex", userInfo.getSex());
                                    edit.putString("school", userInfo.getSchool());
                                    edit.putString("academy", userInfo.getAcademy());
                                    edit.putString("major", userInfo.getMajor());
                                    edit.putString("entryYear", userInfo.getEntryYear());
                                    edit.putString("perSign", userInfo.getPerSign());
                                    edit.putString("hobby", userInfo.getHobby());
                                    edit.putString("homeTown", userInfo.getHomeTown());
                                    edit.commit();

                                    //第三方登录
                                    JMessageClient.login(userName, password, new BasicCallback() {
                                        @Override
                                        public void gotResult(int responseCode, String LoginDesc) {
                                            if (responseCode == 0) {
                                                mProgressDialog.dismiss();
                                                Intent i2 = new Intent(LoginActivity.this,MainActivity.class);
                                                startActivity(i2);
                                                finish();
                                            } else {
                                                mProgressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "第三方登陆失败", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "账号密码错误", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(LoginActivity.this, "请刷新重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, fab, fab.getTransitionName());
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class), options.toBundle());
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fab.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }
}