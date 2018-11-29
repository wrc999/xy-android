package e.administrator.xy.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.jiguang.imui.messages.MessageList;
import cn.jiguang.imui.messages.MsgListAdapter;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;
import e.administrator.xy.R;
import e.administrator.xy.pojo.DefaultUser;
import e.administrator.xy.pojo.MyMessage;
import e.administrator.xy.util.Message2MyMessage;

public class ChatRoomActivity extends AppCompatActivity {
    private TextView tvName;
    private RecyclerView rvShowMeg;
    private EditText etMeg;
    private Button btnSendMeg;
    private ImageView ivReturn,ivPicture,chatroom_memberInfo;
    private MessageList msg_list;
    private MsgListAdapter adapter;
    private long groupId;
    private String userName;
    private String nickName;
    private static final int GALLERY_ACTIVITY_CODE = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JMessageClient.registerEventReceiver(this);
        setContentView(R.layout.activity_chat_room);
        initView();
        initData();
    }

    private void initView(){
        ivReturn = findViewById(R.id.iv_return);
        ivReturn.setOnClickListener(new View.OnClickListener() {
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
        chatroom_memberInfo = findViewById(R.id.chatroom_memberInfo);
        tvName = findViewById(R.id.tv_name);
        etMeg = findViewById(R.id.et_meg);
        if (getIntent().getStringExtra("type").equals("single")){
            userName = getIntent().getStringExtra("userName");
            nickName = getIntent().getStringExtra("nickName");
            tvName.setText(nickName);
            //进入用户详情页
            chatroom_memberInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ChatRoomActivity.this,friendInfo.class);
                    intent.putExtra("userName",userName);
                    startActivity(intent);
                }
            });
        }
        if (getIntent().getStringExtra("type").equals("group")){
            groupId = getIntent().getLongExtra("groupId",0);
            tvName.setText(getIntent().getStringExtra("groupName"));
            //查看群成员
            chatroom_memberInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ChatRoomActivity.this,clubMember.class);
                    intent.putExtra("resource","club");
                    intent.putExtra("groupID",groupId);
                    startActivity(intent);
                }
            });
        }
        msg_list = (MessageList) findViewById(R.id.msg_list);
        msg_list.setShowReceiverDisplayName(false);
        msg_list.setShowSenderDisplayName(true);
        final float density = getResources().getDisplayMetrics().density;
        final float MIN_WIDTH = 60 * density;
        final float MAX_WIDTH = 200 * density;
        final float MIN_HEIGHT = 60 * density;
        final float MAX_HEIGHT = 200 * density;
        adapter = new MsgListAdapter<>("0", new MsgListAdapter.HoldersConfig(), new cn.jiguang.imui.commons.ImageLoader() {
            @Override
            public void loadAvatarImage(ImageView avatarImageView, String string) {
                Glide.with(ChatRoomActivity.this).load(string).into(avatarImageView);
            }

            @Override
            public void loadImage(final ImageView imageView, String string) {
                Glide.with(ChatRoomActivity.this).load(string).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        int imageWidth = resource.getWidth();
                        int imageHeight = resource.getHeight();

                        // 裁剪 bitmap
                        float width, height;
                        if (imageWidth > imageHeight) {
                            if (imageWidth > MAX_WIDTH) {
                                float temp = MAX_WIDTH / imageWidth * imageHeight;
                                height = temp > MIN_HEIGHT ? temp : MIN_HEIGHT;
                                width = MAX_WIDTH;
                            } else if (imageWidth < MIN_WIDTH) {
                                float temp = MIN_WIDTH / imageWidth * imageHeight;
                                height = temp < MAX_HEIGHT ? temp : MAX_HEIGHT;
                                width = MIN_WIDTH;
                            } else {
                                float ratio = imageWidth / imageHeight;
                                if (ratio > 3) {
                                    ratio = 3;
                                }
                                height = imageHeight * ratio;
                                width = imageWidth;
                            }
                        } else {
                            if (imageHeight > MAX_HEIGHT) {
                                float temp = MAX_HEIGHT / imageHeight * imageWidth;
                                width = temp > MIN_WIDTH ? temp : MIN_WIDTH;
                                height = MAX_HEIGHT;
                            } else if (imageHeight < MIN_HEIGHT) {
                                float temp = MIN_HEIGHT / imageHeight * imageWidth;
                                width = temp < MAX_WIDTH ? temp : MAX_WIDTH;
                                height = MIN_HEIGHT;
                            } else {
                                float ratio = imageHeight / imageWidth;
                                if (ratio > 3) {
                                    ratio = 3;
                                }
                                width = imageWidth * ratio;
                                height = imageHeight;
                            }
                        }
                        ViewGroup.LayoutParams params = imageView.getLayoutParams();
                        params.width = (int) width;
                        params.height = (int) height;
                        imageView.setLayoutParams(params);
                        Matrix matrix = new Matrix();
                        float scaleWidth = width / imageWidth;
                        float scaleHeight = height / imageHeight;
                        matrix.postScale(scaleWidth, scaleHeight);
                        imageView.setImageBitmap(Bitmap.createBitmap(resource, 0, 0, imageWidth, imageHeight, matrix, true));
                    }
                });
            }

            @Override
            public void loadVideo(ImageView imageCover, String uri) {
                Glide.with(ChatRoomActivity.this).load(uri).into(imageCover);
            }
        });
        //OnMsgClickListener: 点击消息触发
        adapter.setOnMsgClickListener(new MsgListAdapter.OnMsgClickListener<MyMessage>() {
            @Override
            public void onMessageClick(MyMessage message) {
                if (message.getMediaFilePath()!=null&message.getMediaFilePath()!=""){
                    ArrayList<String> imageList = new ArrayList<String>();
                    imageList.add(message.getMediaFilePath());
                    Intent intent = new Intent(ChatRoomActivity.this, ImageBrowseActivity.class);
                    intent.putStringArrayListExtra("imageList", imageList);
                    startActivity(intent);
                }
            }
        });
        //OnAvatarClickListener: 点击头像触发
        adapter.setOnAvatarClickListener(new MsgListAdapter.OnAvatarClickListener<MyMessage>() {
            @Override
            public void onAvatarClick(MyMessage message) {
                DefaultUser userInfo = (DefaultUser) message.getFromUser();
                Intent intent = new Intent(ChatRoomActivity.this,friendInfo.class);
                intent.putExtra("userName",userInfo.getId());
                startActivity(intent);
            }
        });
        //OnMsgLongClickListener: 长按消息触发
        adapter.setMsgLongClickListener(new MsgListAdapter.OnMsgLongClickListener<MyMessage>() {
            @Override
            public void onMessageLongClick(View view,MyMessage message) {
                Toast.makeText(ChatRoomActivity.this, "您长按了"+message.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        msg_list.setAdapter(adapter);
    }
    private void initData() {
        btnSendMeg = findViewById(R.id.btn_send_message);
        //点击发送消息
        btnSendMeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String meg = etMeg.getText().toString().trim();
                sendMessage(userName, meg);
            }
        });
        //点击发送图片
        ivPicture = findViewById(R.id.ivpicture);
        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matisse.from(ChatRoomActivity.this)
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
            }
        });
        initConversion();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_ACTIVITY_CODE && resultCode==RESULT_OK) {
            List<Uri> mSelected = Matisse.obtainResult(data);
            try {
                Uri uri = mSelected.get(0);        //获得图片的uri
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(uri, proj, null, null, null);
                //按我个人理解 这个是获得用户选择的图片的索引值
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                //最后根据索引值获取图片路径
                final String path = cursor.getString(column_index);
                if (!TextUtils.isEmpty(userName) && path!=null) {
                    File file = new File(path);
                    ImageContent image = new ImageContent(file);
                    //获取当前会话消息
                    Conversation mConversation = JMessageClient.getSingleConversation(userName, "");
                    if (mConversation == null) {
                        mConversation = Conversation.createSingleConversation(userName, "");
                    }
                    Message message = mConversation.createSendMessage(image);
                    message.setOnSendCompleteCallback(new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                Toast.makeText(ChatRoomActivity.this, "图片发送成功", Toast.LENGTH_SHORT).show();
                                //刷新消息界面
                                initConversion();
                            } else {
                                Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    //设置消息发送时的一些控制参数
                    MessageSendingOptions options = new MessageSendingOptions();
                    options.setNeedReadReceipt(true);//是否需要对方用户发送消息已读回执
                    options.setRetainOffline(true);//是否当对方用户不在线时让后台服务区保存这条消息的离线消息
                    options.setShowNotification(true);//是否让对方展示sdk默认的通知栏通知
                    //发送消息
                    JMessageClient.sendMessage(message, options);
                }
                if (groupId>0 && path!=null) {
                    File file = new File(path);
                    ImageContent image = new ImageContent(file);
                    //获取当前会话消息
                    Conversation mConversation = JMessageClient.getGroupConversation(groupId);
                    if (mConversation == null) {
                        mConversation = Conversation.createGroupConversation(groupId);
                    }
                    Message message = mConversation.createSendMessage(image);
                    message.setOnSendCompleteCallback(new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                Toast.makeText(ChatRoomActivity.this, "图片发送成功", Toast.LENGTH_SHORT).show();
                                //刷新消息界面
                                initConversion();
                            } else {
                                Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    //设置消息发送时的一些控制参数
                    MessageSendingOptions options = new MessageSendingOptions();
                    options.setNeedReadReceipt(true);//是否需要对方用户发送消息已读回执
                    options.setRetainOffline(true);//是否当对方用户不在线时让后台服务区保存这条消息的离线消息
                    options.setShowNotification(true);//是否让对方展示sdk默认的通知栏通知
                    //发送消息
                    JMessageClient.sendMessage(message, options);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(final String userName, final String meg){
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(meg)) {
            //获取当前会话消息
            Conversation mConversation = JMessageClient.getSingleConversation(userName, "");
            //创建单聊会话，如果本地已存在对应会话，则不会重新创建，直接返回本地会话对象。
            if (mConversation == null) {
                mConversation = Conversation.createSingleConversation(userName, "");
            }
            //构造message content对象
            TextContent textContent = new TextContent(meg);
            //创建message实体，设置消息发送回调。
            Message message = mConversation.createSendMessage(textContent);

            message.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        etMeg.setText("");
                        //刷新消息界面
                        initConversion();
                    } else {
                        Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //设置消息发送时的一些控制参数
            MessageSendingOptions options = new MessageSendingOptions();
            options.setNeedReadReceipt(true);//是否需要对方用户发送消息已读回执
            options.setRetainOffline(true);//是否当对方用户不在线时让后台服务区保存这条消息的离线消息
            options.setShowNotification(true);//是否让对方展示sdk默认的通知栏通知
            //发送消息
            JMessageClient.sendMessage(message, options);
        }
        if (groupId>0 && !TextUtils.isEmpty(meg)) {
            //获取当前会话消息
            Conversation mConversation = JMessageClient.getGroupConversation(groupId);
            //创建单聊会话，如果本地已存在对应会话，则不会重新创建，直接返回本地会话对象。
            if (mConversation == null) {
                mConversation = Conversation.createGroupConversation(groupId);
            }
            //构造message content对象
            TextContent textContent = new TextContent(meg);
            //创建message实体，设置消息发送回调。
            Message message = mConversation.createSendMessage(textContent);

            message.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        etMeg.setText("");
                        //刷新消息界面
                        initConversion();
                    } else {
                        Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //设置消息发送时的一些控制参数
            MessageSendingOptions options = new MessageSendingOptions();
            options.setNeedReadReceipt(true);//是否需要对方用户发送消息已读回执
            options.setRetainOffline(true);//是否当对方用户不在线时让后台服务区保存这条消息的离线消息
            options.setShowNotification(true);//是否让对方展示sdk默认的通知栏通知
            //发送消息
            JMessageClient.sendMessage(message, options);
        }
    }
    //后台监听是否有信息，如果有则重新接受新消息
    public void onEventMainThread(MessageEvent event) {
        initConversion();
    }

    public void initConversion() {
        adapter.clear();
        Conversation conversation = null;
        if (!TextUtils.isEmpty(userName)){
            conversation = JMessageClient.getSingleConversation(userName);
        }
        if (groupId>0){
            conversation = JMessageClient.getGroupConversation(groupId);
        }
        if (conversation != null) {
            List<Message> messagelist = conversation.getAllMessage();
            List<MyMessage> myMessageList = Message2MyMessage.list(messagelist);
            for (MyMessage m : myMessageList){
                adapter.addToStart(m,true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }
}
