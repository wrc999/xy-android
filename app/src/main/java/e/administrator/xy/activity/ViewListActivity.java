package e.administrator.xy.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.view;
import e.administrator.xy.util.Cn2Spell;
import e.administrator.xy.util.JsonUtil;
import e.administrator.xy.util.constant;
import e.administrator.xy.util.qiniuUtil.Auth;
import e.administrator.xy.util.viewAdapter;
import e.administrator.xy.view.RecycleViewDivider;

public class ViewListActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView rRecyclerView;
    private List<view> viewLists = new ArrayList<view>();
    private RelativeLayout returnclubDetail;
    private TextView uploadClubPic;

    private List<String> path = new ArrayList<String>();
    private String clubName;//接收上一个页面传过来的社团名，并将它转为拼音首字母
    private int club_id;//接收上一个页面传过来的社团名，并将它转为拼音首字母

    private int success,fail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);
        init();
        initView();
    }

    private void init() {
        returnclubDetail = findViewById(R.id.clubPic_goBack);
        returnclubDetail.setOnClickListener(this);
        uploadClubPic = findViewById(R.id.uploadClubPic);
        uploadClubPic.setOnClickListener(this);
        clubName = Cn2Spell.getPinYinHeadChar(getIntent().getStringExtra("clubName"));
        club_id = getIntent().getIntExtra("club_id",0);
        rRecyclerView = (RecyclerView) findViewById(R.id.viewList);
        rRecyclerView.setLayoutManager(new GridLayoutManager(ViewListActivity.this,3));
        rRecyclerView.addItemDecoration(new RecycleViewDivider(ViewListActivity.this, LinearLayoutManager.HORIZONTAL,1,getResources().getColor((R.color.txtGray))));
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.clubPic_goBack:
                    Runtime runtime = Runtime.getRuntime();
                    try {
                        runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.uploadClubPic:
                    Matisse.from(ViewListActivity.this)
                            .choose(MimeType.ofAll())//照片视频全部显示
                            .countable(true)//有序选择图片
                            .maxSelectable(9)//最大选择数量为9
                            //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                            .gridExpectedSize(320)//每个图片方格的大小
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向
                            .thumbnailScale(0.85f)//缩放比例
                            .theme(R.style.Matisse_Zhihu)//主题
                            .imageEngine(new GlideEngine())//加载方式
                            .forResult(9);
                    break;
            }
        }

    private void initView() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("clubName",clubName);
        client.post(constant.BASE_URL+constant.view_get,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    if (json!=null){
                        viewLists = JsonUtil.getListFromJson(json, new TypeToken<List<view>>(){}.getType());
                        List<String> imageList = new ArrayList<String>();
                        for(view v : viewLists){
                            imageList.add(v.getPhoto());
                        }
                        if (viewLists!=null){
                            rRecyclerView.setAdapter(new viewAdapter(ViewListActivity.this,imageList));
                        }else {
                            Toast.makeText(ViewListActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(ViewListActivity.this, "请刷新重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver resolver = getContentResolver();
        //本地选取活动海报
        if (requestCode == 9 && resultCode==RESULT_OK) {
            List<Uri> mSelected = Matisse.obtainResult(data);
            try {
                for (int i=0;i<mSelected.size();i++) {
                    Uri originalUri = mSelected.get(i);        //获得图片的uri
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(originalUri, proj, null, null, null);
                    //按我个人理解 这个是获得用户选择的图片的索引值
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    //最后根据索引值获取图片路径
                    String path = cursor.getString(column_index);
                    addView(path,i);
                }
                initView();
                Toast.makeText(this, "成功上传"+mSelected.size()+"张照片", Toast.LENGTH_SHORT).show();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void addView(String path,int index) {
        //因为循环获取的当前时间相同，所以需要添加一个索引index
        UploadManager uploadManager = new UploadManager();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String key = "view/"+sdf.format(new Date())+String.format("%02d",index);
        uploadManager.put(path, key, Auth.create(constant.AccessKey, constant.SecretKey).uploadToken(constant.bucket), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject res) {
                // info.error中包含了错误信息，可打印调试
                // 上传成功后将key值上传到自己的服务器
                if (info.isOK()) {
                    String qiniuKey = ViewListActivity.this.getString(R.string.qiniuKey);
                    String headpicPath = qiniuKey + key;
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    SharedPreferences sp = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
                    params.put("use_id",sp.getInt("use_id",0));
                    params.put("club_id",club_id);
                    params.put("clubName",clubName);
                    params.put("photo",headpicPath);
                    client.post(constant.BASE_URL+ constant.view_add,params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            success++;
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(ViewListActivity.this, "请刷新重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    fail++;
                }
            }
        }, null);
    }
}
