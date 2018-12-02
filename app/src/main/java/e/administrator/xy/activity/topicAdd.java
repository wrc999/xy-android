package e.administrator.xy.activity;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
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

public class topicAdd extends AppCompatActivity implements View.OnClickListener {

    private ImageView ava;
    private TextView topicAdd;
    private RelativeLayout returnGc;
    private EditText name,intro;
    private Uri imageUri;
    private static final int REQUEST_CAPTURE = 2;
    private static final int GALLERY_ACTIVITY_CODE = 9;
    private String avaPath;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_add);
        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {
        returnGc = findViewById(R.id.topicAdd_return);
        topicAdd = findViewById(R.id.topicAdd);
        name = findViewById(R.id.topicAddName);
        intro = findViewById(R.id.topicAddIntro);
        ava = findViewById(R.id.topicAddAva);
        returnGc.setOnClickListener(this);
        topicAdd.setOnClickListener(this);
        ava.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.topicAdd_return:
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.topicAdd:
                addTopic();
                break;
            case R.id.topicAddAva:
                Matisse.from(topicAdd.this)
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
            default:break;
        }
    }

    private void addTopic() {
        if (avaPath==null || "".equals(avaPath.toString())){
            Toast.makeText(getApplicationContext(), "请上传话题封面", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name==null|| name.getText().toString().trim().equals("")){
            Toast.makeText(this, "话题名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (intro==null|| intro.getText().toString().trim().equals("")){
            Toast.makeText(this, "话题导语不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sp = this.getSharedPreferences("data", Context.MODE_PRIVATE);
        AsyncHttpClient client = new AsyncHttpClient();
        final RequestParams params = new RequestParams();
        //如果用户没有选择照片则不上传话题海报
        if (avaPath!=null){
            String qiniuKey = this.getString(R.string.qiniuKey);
            UploadManager uploadManager = new UploadManager();
            String key = "topicAva/"+sdf.format(new Date());
            params.put("topicPic",qiniuKey+ key);
            uploadManager.put(avaPath, key, Auth.create(constant.AccessKey, constant.SecretKey).uploadToken(constant.bucket), new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject res) {
                    // info.error中包含了错误信息，可打印调试
                    // 上传成功后将key值上传到自己的服务器
                    if (!info.isOK()) {
                        Toast.makeText(topicAdd.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }, null);
        }else {
            return;
        }

        params.put("account",sp.getString("account",null));
        params.put("topicName",name.getText().toString().trim());
        params.put("topicIntro",intro.getText().toString().trim());
        client.post(constant.BASE_URL+constant.topicAdd,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    if (!json.isEmpty()){
                        Intent intent = new Intent(topicAdd.this,MainActivity.class);
                        startActivity(intent);
                        topicAdd.this.finish();
                    }else {
                        Toast.makeText(topicAdd.this, "发布失败", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(topicAdd.this, "请刷新重试", Toast.LENGTH_SHORT).show();
            }
        });
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
    }
}
