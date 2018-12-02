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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
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
import com.zhihu.matisse.filter.Filter;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.fragment.Tab2Pager;
import e.administrator.xy.util.constant;
import e.administrator.xy.util.qiniuUtil.Auth;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class TalkAddActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView talkAdd,talkAdd_wordNum,topicName;
    private RelativeLayout returnXl;
    private LinearLayout selectTopic;
    private EditText talkContent;
    private ImageView talkPhoto;
    private Uri imageUri;
    private static final int REQUEST_CAPTURE = 2;
    private static final int GALLERY_ACTIVITY_CODE = 9;
    String path=null;
    // 设置图片名字
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_add);
        init();
    }

    private void init() {
        //清除SharedPreferences中topicNeme的值，防止初始赋值
        SharedPreferences sp = getSharedPreferences("data",MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.remove("topicName");
        edit.commit();
        returnXl =  findViewById(R.id.talkAdd_returnTtab1);
        talkAdd =  findViewById(R.id.talkAdd);
        selectTopic = findViewById(R.id.talkAdd_topicSelect);
        topicName = findViewById(R.id.talkAdd_topicName);
        topicName.setText("");
        talkContent =  findViewById(R.id.talkAddTalkContent);
        talkAdd_wordNum = findViewById(R.id.talkAdd_wordNum);
        //给编辑框添加子字数监听器
        talkContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int num = 100-s.length();
                talkAdd_wordNum.setText("("+num+")");
            }
        });
        talkPhoto = findViewById(R.id.talkAddTalkPhoto);
        talkPhoto.setOnClickListener(this);
        selectTopic.setOnClickListener(this);
        talkAdd.setOnClickListener(this);
        returnXl.setOnClickListener(this);
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
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.talkAddTalkPhoto:
                //todo:底部弹出框，选择拍照或本地上方的上传
                final List<String> stringList = new ArrayList<String>();
                stringList.add("拍照");
                stringList.add("从相册选择");
                final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(TalkAddActivity.this, stringList);
                optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position==0){
                            openCamera();
                            optionBottomDialog.dismiss();
                        }
                        if (position==1){
//                            Intent gallery_Intent = new Intent(Intent.ACTION_PICK, null);
//                            gallery_Intent.setType("image/*");
//                            startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);
                            Matisse.from(TalkAddActivity.this)
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
            //发布
            case R.id.talkAdd:
                addTalk();
                break;
            //选择话题
            case R.id.talkAdd_topicSelect:
                Intent intent0 = new Intent(this,MainActivity.class);
                intent0.putExtra("id",22);
                intent0.putExtra("talkAdd","talkAdd");
                startActivity(intent0);
                //onBackPressed();返回上一页面，在适配器中不适用
                break;
            //返回
            case R.id.talkAdd_returnTtab1:
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void addTalk() {
        if (talkContent==null|| talkContent.getText().toString().trim().equals("")){
            Toast.makeText(this, "分享内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        SharedPreferences sp = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        //如果用户没有选择照片则不上传图片
        if (path!=null){
            String qiniuKey = this.getString(R.string.qiniuKey);
            UploadManager uploadManager = new UploadManager();
            String key = "talk/"+sdf.format(new Date());
            String headpicPath = qiniuKey + key;
            params.put("talkphoto",headpicPath);
            uploadManager.put(path, key, Auth.create(constant.AccessKey, constant.SecretKey).uploadToken(constant.bucket), new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject res) {
                    // info.error中包含了错误信息，可打印调试
                    // 上传成功后将key值上传到自己的服务器
                    if (!info.isOK()) {
                        Toast.makeText(TalkAddActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }, null);
        }
        params.put("use_id",sp.getInt("use_id",0));
        params.put("talkcontent",talkContent.getText().toString().trim());
        params.put("topicName",topicName.getText().toString().trim());
        client.post(constant.BASE_URL+ constant.Talk_add,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    if (!json.isEmpty()){
                        Intent intent = new Intent(TalkAddActivity.this,MainActivity.class);
                        intent.putExtra("id",22);
                        startActivity(intent);
                        TalkAddActivity.this.finish();
                    }else {
                        Toast.makeText(TalkAddActivity.this, "发布失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(TalkAddActivity.this, "请刷新重试", Toast.LENGTH_SHORT).show();
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

    //从其他activity跳转回来时调用（获取焦点开始与用户交互时调用）
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences("data",MODE_PRIVATE);
        topicName.setText(sp.getString("topicName",null));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bm = null;
        ContentResolver resolver = getContentResolver();
        //resultCode==RESULT_OK不写的话返回上一页面会出错，设置返回页面
        if (requestCode == GALLERY_ACTIVITY_CODE && resultCode==RESULT_OK) {
            List<Uri> mSelected = Matisse.obtainResult(data);
            try {
                Uri originalUri = mSelected.get(0);        //获得图片的uri
                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                talkPhoto.setImageBitmap(bm);//显得到bitmap图片
                //    这里开始的第二部分，获取图片的路径：
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                //按我个人理解 这个是获得用户选择的图片的索引值
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                //最后根据索引值获取图片路径
                path = cursor.getString(column_index);
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == REQUEST_CAPTURE){
            try {
                talkPhoto.setImageURI(imageUri);
                path = file.getPath();
                Toast.makeText(this, "拍摄照片存储地址为："+path, Toast.LENGTH_SHORT).show();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
