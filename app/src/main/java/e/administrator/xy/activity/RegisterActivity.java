package e.administrator.xy.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.util.StatusBarUtils;
import e.administrator.xy.util.constant;

public class RegisterActivity extends AppCompatActivity{
    private static final String TAG = "RegisterActivity";
    private ProgressDialog mProgressDialog = null;

    private Button btnRegister;
    private EditText username,password,password1;
    private FloatingActionButton fab;
    private CardView cvAdd;
    private String userName,passWord,passWord1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        init();
        ShowEnterAnimation();
        setListener();
    }

    private void init() {
        //设置状态栏颜色
        StatusBarUtils.setStatusBarColor(this,R.color.black_de);
        username = (EditText) findViewById(R.id.register_username);
        password = (EditText) findViewById(R.id.register_password);
        password1 = (EditText) findViewById(R.id.register_password1);
        btnRegister = (Button) findViewById(R.id.bt_register);
        fab = findViewById(R.id.fab);
        cvAdd = findViewById(R.id.cv_add);
        validate();
    }
    private void setListener() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = username.getText().toString();
                passWord = password.getText().toString();
                passWord1 = password1.getText().toString();
                if (userName.length()<1){
                    Toast.makeText(RegisterActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passWord.length()<1){
                    Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passWord.length()!=6){
                    Toast.makeText(RegisterActivity.this, "请输入六位密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!passWord.equals(passWord1)) {
                    Toast.makeText(RegisterActivity.this, "两次密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }

                mProgressDialog = ProgressDialog.show(RegisterActivity.this, "提示：", "正在加载中。。。");
                mProgressDialog.setCanceledOnTouchOutside(true);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("account", userName);
                params.put("passWord", passWord);
                client.post(constant.BASE_URL + constant.user_add, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            String json = new String(responseBody, "utf-8");
                            if (Integer.valueOf(json) > 0) {

                                /**=================     调用SDK注册接口    =================*/
                                JMessageClient.register(userName, passWord, new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String registerDesc) {
                                        if (responseCode == 0) {
                                            mProgressDialog.dismiss();
                                            LoginActivity.account.setText(userName);
                                            LoginActivity.passWord.setText(passWord);
                                            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            mProgressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(RegisterActivity.this, "请刷新重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
    }

    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }
    //长度监听
    private void validate() {
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()==10){
                    Toast.makeText(RegisterActivity.this, "最长十个字符哟", Toast.LENGTH_SHORT).show();
                }
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()==10){
                    Toast.makeText(RegisterActivity.this, "最长十个字符哟", Toast.LENGTH_SHORT).show();
                }
            }
        });
        password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()==10){
                    Toast.makeText(RegisterActivity.this, "最长十个字符哟", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
