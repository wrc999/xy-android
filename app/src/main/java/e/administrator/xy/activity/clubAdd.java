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
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.CreateGroupCallback;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.util.constant;
import e.administrator.xy.util.qiniuUtil.Auth;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class clubAdd extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private CheckBox e1,e2,e3,e4,f1,f2,f3,f4,g1,g2,g3,g4;
    private List<CheckBox> checkBoxList = new ArrayList<CheckBox>();
    private StringBuffer sb;//关键字选中结果集
    private EditText name,slogan,intro;
    private LinearLayout clubAddLinearAva;
    private ImageView ava;
    private TextView clubAdd;
    private RelativeLayout returnGc;
    private Uri imageUri;
    private static final int REQUEST_CAPTURE = 2;
    private static final int GALLERY_ACTIVITY_CODE = 9;
    private static final int GALLERY_ACTIVITY_PIC = 10;
    private String avaPath = null;
    // 设置图片名字
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_add);
        intiView();
    }

    private void intiView() {
        methodRequiresTwoPermission();
        name = (EditText) findViewById(R.id.clubAddName);
        slogan = (EditText) findViewById(R.id.et_clubAddSlogan);
        intro = (EditText) findViewById(R.id.et_clubAddIntro);
        returnGc =  findViewById(R.id.clubAdd_returnTtab1);
        clubAdd = (TextView) findViewById(R.id.clubAdd);
        clubAddLinearAva =  findViewById(R.id.clubAddLinearAva);
        ava = findViewById(R.id.clubAddAva);
        clubAddLinearAva.setOnClickListener(this);
        returnGc.setOnClickListener(this);
        clubAdd.setOnClickListener(this);
        e1 = findViewById(R.id.e1);
        e2 = findViewById(R.id.e2);
        e3 = findViewById(R.id.e3);
        e4 = findViewById(R.id.e4);
        f1 = findViewById(R.id.f1);
        f2 = findViewById(R.id.f2);
        f3 = findViewById(R.id.f3);
        f4 = findViewById(R.id.f4);
        g1 = findViewById(R.id.g1);
        g2 = findViewById(R.id.g2);
        g3 = findViewById(R.id.g3);
        g4 = findViewById(R.id.g4);
        checkBoxList.add(e1);
        checkBoxList.add(e2);
        checkBoxList.add(e3);
        checkBoxList.add(e4);
        checkBoxList.add(f1);
        checkBoxList.add(f2);
        checkBoxList.add(f3);
        checkBoxList.add(f4);
        checkBoxList.add(g1);
        checkBoxList.add(g2);
        checkBoxList.add(g3);
        checkBoxList.add(g4);
        for (CheckBox checkbox: checkBoxList) {
            checkbox.setOnCheckedChangeListener(this);
        }
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
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clubAddLinearAva:
                final List<String> stringList = new ArrayList<String>();
                stringList.add("拍照");
                stringList.add("从相册选择");

                final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(clubAdd.this, stringList);
                optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position==0){
                            openCamera();
                            optionBottomDialog.dismiss();
                        }
                        if (position==1){
                            Matisse.from(clubAdd.this)
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
                            optionBottomDialog.dismiss();
                        }
                    }
                });
                break;
            case R.id.clubAdd:
                addClub();
                break;
            case R.id.clubAdd_returnTtab1:
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:break;
        }
    }

    private void addClub() {
        if (avaPath==null || "".equals(avaPath.toString())){
            Toast.makeText(getApplicationContext(), "请上传活动海报", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name==null|| name.getText().toString().trim().equals("")){
            Toast.makeText(this, "社团名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (slogan==null|| slogan.getText().toString().trim().equals("")){
            Toast.makeText(this, "社团口号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (intro==null|| intro.getText().toString().trim().equals("")){
            Toast.makeText(this, "社团介绍不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sb==null || "".equals(sb.toString())){
            Toast.makeText(getApplicationContext(), "请选择一个标签", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sp = this.getSharedPreferences("data", Context.MODE_PRIVATE);
        AsyncHttpClient client = new AsyncHttpClient();
        final RequestParams params = new RequestParams();
        //如果用户没有选择照片则不上传活动海报
        if (avaPath!=null){
            UploadManager uploadManager = new UploadManager();
            String qiniuKey = this.getString(R.string.qiniuKey);
            String key = "clubAva/"+sdf.format(new Date());
            params.put("clubAva",qiniuKey + key);
            uploadManager.put(avaPath, key, Auth.create(constant.AccessKey, constant.SecretKey).uploadToken(constant.bucket), new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject res) {
                    // info.error中包含了错误信息，可打印调试
                    // 上传成功后将key值上传到自己的服务器
                    if (info.isOK()) {
                    }
                }
            }, null);
        }

        params.put("account",sp.getString("account",null));
        params.put("clubSchool",sp.getString("school",null));
        params.put("clubName",name.getText());
        params.put("clubSlogan",slogan.getText());
        params.put("clubIntro",intro.getText());
        params.put("sort",sb.toString().trim());
        client.post(constant.BASE_URL+constant.club_add,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    if (!json.isEmpty()){
                        //创建社团聊天群
                        JMessageClient.createGroup(name.getText().toString().trim(), intro.getText().toString().trim(),new File(avaPath),"png",new CreateGroupCallback() {
                            @Override
                            public void gotResult(int i, String s, long l) {
                                    if (i==0){
                                        //社团聊天群创建成功后发送一条信息显示出来
                                        Conversation mConversation = JMessageClient.getGroupConversation(l);
                                        if (mConversation == null) {
                                            mConversation = Conversation.createGroupConversation(l);
                                        }
                                        TextContent textContent = new TextContent("成功创建");
                                        //创建message实体，设置消息发送回调。
                                        Message message = mConversation.createSendMessage(textContent);
                                        //设置消息发送时的一些控制参数
                                        MessageSendingOptions options = new MessageSendingOptions();
                                        options.setNeedReadReceipt(true);//是否需要对方用户发送消息已读回执
                                        options.setRetainOffline(true);//是否当对方用户不在线时让后台服务区保存这条消息的离线消息
                                        options.setShowNotification(true);//是否让对方展示sdk默认的通知栏通知
                                        //发送消息
                                        JMessageClient.sendMessage(message, options);
                                    }
                            }
                        });
                        Intent intent = new Intent(clubAdd.this,MainActivity.class);
                        startActivity(intent);
                        clubAdd.this.finish();
                    }else {
                        Toast.makeText(clubAdd.this, "发布成功", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(clubAdd.this, "请刷新重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //todo:调用相机拍照
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File("sdcard/DCIM/Camera/"+sdf.format(new Date())+".png"); //工具类稍后会给出
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  //针对Android7.0，需要通过FileProvider封装过的路径，提供给外部调用
            imageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);//通过FileProvider创建一个content类型的Uri，进行封装
        } else { //7.0以下，如果直接拿到相机返回的intent值，拿到的则是拍照的原图大小，很容易发生OOM，所以我们同样将返回的地址，保存到指定路径，返回到Activity时，去指定路径获取，压缩图片
            imageUri = Uri.fromFile(file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        startActivityForResult(intent, REQUEST_CAPTURE);//启动拍照
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bm = null;
        ContentResolver resolver = getContentResolver();
        //本地选取活动海报
        if (requestCode == GALLERY_ACTIVITY_CODE && resultCode==RESULT_OK) {
            List<Uri> mSelected = Matisse.obtainResult(data);
            try {
                Uri originalUri = mSelected.get(0);        //获得图片的uri
                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                ava.setImageBitmap(bm);//显得到bitmap图片
                //    这里开始的第二部分，获取图片的路径：
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                //按我个人理解 这个是获得用户选择的图片的索引值
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                //最后根据索引值获取图片路径
                avaPath = cursor.getString(column_index);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        //拍照上传活动海报
        if (requestCode == REQUEST_CAPTURE){
            try {
                ava.setImageURI(imageUri);
                avaPath = file.getPath();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int i=0;
        sb = new StringBuffer();
        //遍历集合中的checkBox,判断是否选择，获取选中的文本
        for (CheckBox checkbox : checkBoxList) {
            if (checkbox.isChecked()){
                i++;
                if (i<2){
                    sb.append(checkbox.getText().toString());
                }else {
                    checkbox.setChecked(false);
                    Toast.makeText(this, "标签最多选择一个", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }
}
