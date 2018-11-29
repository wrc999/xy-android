package e.administrator.xy.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.qiniu.android.storage.UploadManager;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.userInfo;
import e.administrator.xy.util.DateUtil;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.constant;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class userInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView userPic;
    private EditText nickName,sex,school,academy,major,hobby,perSign,homeTown;
    private TextView birth,entryYear;
    private TextView save;
    private RelativeLayout returnYj;
    private static final int GALLERY_ACTIVITY_CODE = 9;
    String path = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    UploadManager uploadManager = new UploadManager();
    String key = "userHeadPic/"+sdf.format(new Date());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo);
        SharedPreferences sp = getApplicationContext().getSharedPreferences("data",Context.MODE_PRIVATE);
        init();
        initData();
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

    //获得界面控件
    private void init() {
        SharedPreferences sp = userInfoActivity.this.getSharedPreferences("data", Context.MODE_PRIVATE);
        path = sp.getString("avaPath",null);
        userPic = findViewById(R.id.iv_userPic);
        nickName = findViewById(R.id.et_nickName);
        sex = findViewById(R.id.et_sex);
        school = findViewById(R.id.et_school);
        academy = findViewById(R.id.et_academy);
        major = findViewById(R.id.et_major);
        birth = findViewById(R.id.et_birth);
        entryYear = findViewById(R.id.et_entryyear);
        hobby = findViewById(R.id.et_hobby);
        perSign = findViewById(R.id.et_persign);
        homeTown = findViewById(R.id.et_hometown);
        save = findViewById(R.id.tv_save_data);
        returnYj = findViewById(R.id.tv_go_back);
        userPic = findViewById(R.id.iv_userPic);
        save.setOnClickListener(this);
        returnYj.setOnClickListener(this);
        userPic.setOnClickListener(this);
        birth.setOnClickListener(this);
        entryYear.setOnClickListener(this);
    }
    public void pickTime(final TextView view){
        TimePickerView pvTime = new TimePickerBuilder(userInfoActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                try {
                    view.setText(DateUtil.getSelectTime(date));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        })
                .setCancelText("取消")
                .setSubmitText("确认")
                .build();
        pvTime.show();
    }

    //初始化界面数据
    private void initData() {
        SharedPreferences sp = userInfoActivity.this.getSharedPreferences("data", Context.MODE_PRIVATE);
        if (JMessageClient.getMyInfo().getAvatarFile() != null){
            Glide.with(this).load(JMessageClient.getMyInfo().getAvatarFile().getPath()).placeholder(R.mipmap.loading).into(userPic);
        }else {
            Glide.with(this).load(R.mipmap.activity_people).into(userPic);
        }
        nickName.setText(sp.getString("nickName",null)) ;
        sex.setText(sp.getString("sex",null));
        school.setText(sp.getString("school",null));
        academy.setText(sp.getString("academy",null));
        major.setText(sp.getString("major",null));
        birth.setText(sp.getString("birth",null));
        entryYear.setText(sp.getString("entryYear",null));
        hobby.setText(sp.getString("hobby",null));
        perSign.setText(sp.getString("perSign",null));
        homeTown.setText(sp.getString("homeTown",null));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.et_birth:pickTime(birth);break;
            case R.id.et_entryyear:pickTime(entryYear);break;
            //选择本地照片进行上传
            case R.id.iv_userPic:
                Matisse.from(userInfoActivity.this)
                        .choose(MimeType.ofAll())//照片视频全部显示
                        .countable(true)//有序选择图片
                        .maxSelectable(1)//最大选择数量为9
                        //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                        .gridExpectedSize(320)//每个图片方格的大小
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向
                        .thumbnailScale(0.85f)//缩放比例
                        .theme(R.style.Matisse_Zhihu)//主题
                        .imageEngine(new GlideEngine())//加载方式
                        .forResult(GALLERY_ACTIVITY_CODE);
                break;
            //保存修改信息
            case R.id.tv_save_data:
                saveInfo();
                Intent intent0 = new Intent(this,MainActivity.class);
                intent0.putExtra("id",4);
                startActivity(intent0);
                break;
            //返回上一界面
            case R.id.tv_go_back:
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    //用户信息保存函数
    private void saveInfo() {
        //将用户信息备份到数据库中去
        SharedPreferences sp = userInfoActivity.this.getSharedPreferences("data", Context.MODE_PRIVATE);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("use_id",sp.getInt("use_id",0));
        params.put("avaPath","http://p9sertmb8.bkt.clouddn.com/" + key);
        params.put("nickName",nickName.getText().toString().trim());
        params.put("sex",sex.getText().toString().trim());
        params.put("birth",birth.getText().toString().trim());
        params.put("school",school.getText().toString().trim());
        params.put("academy",academy.getText().toString().trim());
        params.put("major",major.getText().toString().trim());
        params.put("hobby",hobby.getText().toString().trim());
        params.put("homeTown",homeTown.getText().toString().trim());
        params.put("entryYear",entryYear.getText().toString().trim());
        params.put("perSign",perSign.getText().toString().trim());
        client.post(constant.BASE_URL+constant.user_edit,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = null;
                try {
                    json = new String(responseBody, "utf-8");
                    userInfo userInfo = JsonUtil.getObjectFromJson(json, userInfo.class);
                if (userInfo != null) {
                    SharedPreferences sp = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putInt("use_id", userInfo.getUse_id());
                    edit.putString("passWord", userInfo.getPassWord());
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
                }
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
});

        //用用户昵称保存到极光后台中去
        UserInfo userInfo = JMessageClient.getMyInfo();
        userInfo.setNickname(nickName.getText().toString().trim());
        userInfo.setUserExtras("birth",birth.getText().toString().trim());
        userInfo.setUserExtras("sex",sex.getText().toString().trim());
        userInfo.setUserExtras("school",school.getText().toString().trim());
        userInfo.setUserExtras("academy",academy.getText().toString().trim());
        userInfo.setUserExtras("major",major.getText().toString().trim());
        userInfo.setUserExtras("entryYear",entryYear.getText().toString().trim());
        userInfo.setUserExtras("perSign",perSign.getText().toString().trim());
        userInfo.setUserExtras("hobby",hobby.getText().toString().trim());
        userInfo.setUserExtras("homeTown",homeTown.getText().toString().trim());
        JMessageClient.updateMyInfo(UserInfo.Field.extras, userInfo, new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
            }
        });
        JMessageClient.updateMyInfo(UserInfo.Field.nickname, userInfo, new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
            }
        });
        //将头像存储到极光后台中
        if(path !=null){
            JMessageClient.updateUserAvatar(new File(path), new BasicCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage) {
                    if (responseCode == 0){
                        Toast.makeText(userInfoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(userInfoActivity.this, MainActivity.class);
                        intent.putExtra("id", 4);
                        startActivity(intent);
                        userInfoActivity.this.finish();
                    }
                }
            });
        }
        Toast.makeText(this, "资料修改成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    //选择本地照片的回调函数
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bm = null;
        ContentResolver resolver = getContentResolver();
        if (requestCode == GALLERY_ACTIVITY_CODE && resultCode==RESULT_OK) {
            List<Uri> mSelected = Matisse.obtainResult(data);
            try {
                Uri originalUri = mSelected.get(0);        //获得图片的uri
                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                userPic.setImageBitmap(bm);//显得到bitmap图片
                //    这里开始的第二部分，获取图片的路径：
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                //按我个人理解 这个是获得用户选择的图片的索引值
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                //最后根据索引值获取图片路径
                path = cursor.getString(column_index);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}