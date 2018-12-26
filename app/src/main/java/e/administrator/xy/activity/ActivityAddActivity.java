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
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
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

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.util.DateUtil;
import e.administrator.xy.util.constant;
import e.administrator.xy.util.qiniuUtil.Auth;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ActivityAddActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private CheckBox a1,a2,a3,a4,b1,b2,b3,b4,c1,c2,c3,c4,d1,d2,d3,d4;
    private List<CheckBox> checkBoxList = new ArrayList<CheckBox>();
    private StringBuffer sb;//关键字选中结果集
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private int online;
    private EditText name,place,intro,peopleNum;
    private LinearLayout activityAddLinearAva,activityAddPlace;
    private ImageView ava,pic1,pic2,pic3;
    private TextView activityAdd,starttime,endtime;
    private RelativeLayout returnGc;
    private Uri imageUri;
    private static final int REQUEST_CAPTURE = 2;
    private static final int GALLERY_ACTIVITY_CODE = 9;
    private static final int GALLERY_ACTIVITY_PIC = 10;
    private String avaPath = null;
    private List<String> picPath;
    // 设置图片名字
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addactivity);
        intiViews();
    }

    private void intiViews() {
        methodRequiresTwoPermission();
        name = (EditText) findViewById(R.id.et_activityAddName);
        activityAddPlace = findViewById(R.id.activityAddPlace);
        place = (EditText) findViewById(R.id.et_activityAddPlace);
        starttime = (TextView) findViewById(R.id.et_activityAddStartTime);
        starttime.setOnClickListener(this);
        endtime = (TextView) findViewById(R.id.et_activityAddEndTime);
        endtime.setOnClickListener(this);
        intro = (EditText) findViewById(R.id.et_activityAddIntro);
        returnGc =  findViewById(R.id.activityAdd_returnTtab1);
        activityAdd = (TextView) findViewById(R.id.activityAdd);
        activityAddLinearAva =  findViewById(R.id.activityAddLinearAva);
        peopleNum = findViewById(R.id.et_activityAddPeopleNum);
        ava = findViewById(R.id.activityImageAva);
        pic1 = findViewById(R.id.activityPhoto1);
        pic2 = findViewById(R.id.activityPhoto2);
        pic3 = findViewById(R.id.activityPhoto3);
        pic1.setOnClickListener(this);
        activityAddLinearAva.setOnClickListener(this);
        returnGc.setOnClickListener(this);
        activityAdd.setOnClickListener(this);
        radioGroup = findViewById(R.id.activityAddOnLineRadioGroup);
        radioGroup.check(R.id.underLine);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = (RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());
                if (radioButton.getText().toString().equals("线上活动")){
                    online = 1;
                    activityAddPlace.setVisibility(View.GONE);
                }else {
                    online = 0;
                    activityAddPlace.setVisibility(View.VISIBLE);
                }
            }
        });
        a1 = findViewById(R.id.a1);
        a2 = findViewById(R.id.a2);
        a3 = findViewById(R.id.a3);
        a4 = findViewById(R.id.a4);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);
        c1 = findViewById(R.id.c1);
        c2 = findViewById(R.id.c2);
        c3 = findViewById(R.id.c3);
        c4 = findViewById(R.id.c4);
        d1 = findViewById(R.id.d1);
        d2 = findViewById(R.id.d2);
        d3 = findViewById(R.id.d3);
        d4 = findViewById(R.id.d4);
        checkBoxList.add(a1);
        checkBoxList.add(a2);
        checkBoxList.add(a3);
        checkBoxList.add(a4);
        checkBoxList.add(b1);
        checkBoxList.add(b2);
        checkBoxList.add(b3);
        checkBoxList.add(b4);
        checkBoxList.add(c1);
        checkBoxList.add(c2);
        checkBoxList.add(c3);
        checkBoxList.add(c4);
        checkBoxList.add(d1);
        checkBoxList.add(d2);
        checkBoxList.add(d3);
        checkBoxList.add(d4);
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
            case R.id.et_activityAddStartTime:pickTime(starttime);break;
            case R.id.et_activityAddEndTime:pickTime(endtime);break;
            case R.id.activityAddLinearAva:
                final List<String> stringList = new ArrayList<String>();
                stringList.add("拍照");
                stringList.add("从相册选择");

                final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(ActivityAddActivity.this, stringList);
                optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position==0){
                            openCamera();
                            optionBottomDialog.dismiss();
                        }
                        if (position==1){
                            Matisse.from(ActivityAddActivity.this)
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
            case R.id.activityPhoto1:
                final List<String> stringList1 = new ArrayList<String>();
                stringList1.add("拍照");
                stringList1.add("从相册选择");

                final OptionBottomDialog optionBottomDialog1 = new OptionBottomDialog(ActivityAddActivity.this, stringList1);
                optionBottomDialog1.setItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position==0){
                            openCamera();
                            optionBottomDialog1.dismiss();
                        }
                        if (position==1){
                            Matisse.from(ActivityAddActivity.this)
                                    .choose(MimeType.ofAll())//照片视频全部显示
                                    .countable(true)//有序选择图片
                                    .maxSelectable(3)//最大选择数量为9
                                    //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                    .gridExpectedSize(320)//每个图片方格的大小
                                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向
                                    .thumbnailScale(0.85f)//缩放比例
                                    .theme(R.style.Matisse_Zhihu)//主题
                                    .imageEngine(new GlideEngine())//加载方式
                                    .forResult(GALLERY_ACTIVITY_PIC);
                            optionBottomDialog1.dismiss();
                        }
                    }
                });
                break;
            case R.id.activityAdd:
                addActivity();
                break;
            case R.id.activityAdd_returnTtab1:
//                Intent intent = new Intent(ActivityAddActivity.this,MainActivity.class);
//                //todo:跳转到Tab3Fragment
//                intent.putExtra("id",3);
//                startActivity(intent);
//                ActivityAddActivity.this.finish();
                //调用手机自带返回键
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

    private void addActivity() {
        if (avaPath==null || "".equals(avaPath.toString())){
            Toast.makeText(getApplicationContext(), "请上传活动海报", Toast.LENGTH_SHORT).show();
            return;
        }
        if (picPath==null || picPath.size()!=3){
            Toast.makeText(getApplicationContext(), "请上传三张活动照片", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name==null|| name.getText().toString().trim().equals("")){
            Toast.makeText(this, "活动名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (place==null|| place.getText().toString().trim().equals("")){
            Toast.makeText(this, "活动地点不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (intro==null|| intro.getText().toString().trim().equals("")){
            Toast.makeText(this, "活动介绍不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sb==null || "".equals(sb.toString())){
            Toast.makeText(getApplicationContext(), "请至少选择一个标签", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sp = this.getSharedPreferences("data", Context.MODE_PRIVATE);
        AsyncHttpClient client = new AsyncHttpClient();
        final RequestParams params = new RequestParams();
        //如果用户没有选择照片则不上传活动海报
        if (avaPath!=null){
            String qiniuKey = this.getString(R.string.qiniuKey);
            UploadManager uploadManager = new UploadManager();
            String key = "activityAva/"+sdf.format(new Date());
            params.put("ava",qiniuKey + key);
            uploadManager.put(avaPath, key, Auth.create(constant.AccessKey, constant.SecretKey).uploadToken(constant.bucket), new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject res) {
                    // info.error中包含了错误信息，可打印调试
                    // 上传成功后将key值上传到自己的服务器
                    if (!info.isOK()) {
                        Toast.makeText(ActivityAddActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }, null);
        }
        //上传活动照片
        if (picPath!=null){
            for (int i=0;i<picPath.size();i++){
                if (picPath.get(i)!=null){
                    UploadManager uploadManager = new UploadManager();
                    String qiniuKey = this.getString(R.string.qiniuKey);
                    String key = "activityPic/"+sdf.format(new Date());
                    if (i ==0){params.put("pic1",qiniuKey + key);}
                    if (i ==1){params.put("pic2",qiniuKey + key);}
                    if (i ==2){params.put("pic3",qiniuKey + key);}
                    uploadManager.put(picPath.get(i), key, Auth.create(constant.AccessKey, constant.SecretKey).uploadToken(constant.bucket), new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject res) {
                            // info.error中包含了错误信息，可打印调试
                            // 上传成功后将key值上传到自己的服务器
                            if (info.isOK()) {
                            }
                        }
                    }, null);
                }
            }
        }

        params.put("account",sp.getString("account",null));
        params.put("name",name.getText());
        params.put("online",online);
        params.put("starttime",starttime.getText());
        params.put("endtime",endtime.getText());
        params.put("place",place.getText());
        params.put("peopleNum",peopleNum.getText());
        params.put("intro",intro.getText());
        params.put("keyword",sb.toString().substring(0,sb.length()-1));
        client.post(constant.BASE_URL+constant.activity_add,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody,"utf-8");
                    if (!json.isEmpty()){
                        Toast.makeText(ActivityAddActivity.this, "发布成功，请等待管理员审核", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ActivityAddActivity.this,MainActivity.class);
                        intent.putExtra("id",3);
                        startActivity(intent);
                        ActivityAddActivity.this.finish();
                    }else {
                        Toast.makeText(ActivityAddActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(ActivityAddActivity.this, "请刷新重试", Toast.LENGTH_SHORT).show();
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
        //本地选择活动照片
        if (requestCode == GALLERY_ACTIVITY_PIC && resultCode==RESULT_OK) {
            picPath= new ArrayList<String>();
            List<Uri> mSelected = Matisse.obtainResult(data);
            //清空图片栏
            pic1.setImageBitmap(null);
            pic2.setImageBitmap(null);
            pic3.setImageBitmap(null);
                for (int i=0;i<mSelected.size();i++){
                    if (mSelected.get(i)!=null){
                        Uri originalUri = mSelected.get(i);        //获得图片的uri
                        try {
                            bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (i==0){pic1.setImageBitmap(bm);}//显得到bitmap图片
                        if (i==1){pic2.setImageBitmap(bm);pic2.setVisibility(View.VISIBLE);}
                        if (i==2){pic3.setImageBitmap(bm);pic3.setVisibility(View.VISIBLE);}

                        //    这里开始的第二部分，获取图片的路径：
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                        //按我个人理解 这个是获得用户选择的图片的索引值
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        //最后根据索引值获取图片路径
                        picPath.add(cursor.getString(column_index));
                    }
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
                    if (i<4){
                        sb.append(checkbox.getText().toString() + ",");
                    }else {
                        checkbox.setChecked(false);
                        Toast.makeText(this, "标签最多选择三个", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
    }

    public void pickTime(final TextView view){
        TimePickerView pvTime = new TimePickerBuilder(ActivityAddActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                try {
                    view.setText(DateUtil.getSelectDotTime(date));
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
}