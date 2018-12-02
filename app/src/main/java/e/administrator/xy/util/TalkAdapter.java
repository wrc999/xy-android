package e.administrator.xy.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nex3z.flowlayout.FlowLayout;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.activity.ImageBrowseActivity;
import e.administrator.xy.activity.friendInfo;
import e.administrator.xy.pojo.Talk;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2018/6/3.
 */

public class TalkAdapter extends RecyclerView.Adapter<TalkAdapter.MyViewHolder>{
    private Context context;
    private List<Talk> talkList;
    private Inter inter;

    public TalkAdapter(Context context, List<Talk> talkList) {
        this.context = context;
        this.talkList = talkList;
    }

    public interface Inter{
        void refresh();
    }

    public void setOnRefreshListener(Inter inter){
        this.inter = inter;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate
                (R.layout.item_xl,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.talkPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> imageList = new ArrayList<String>();
                imageList.add(talkList.get(position).getTalkphoto());
                Intent intent = new Intent(context, ImageBrowseActivity.class);
                intent.putStringArrayListExtra("imageList", imageList);
                context.startActivity(intent);
            }
        });
        holder.user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,friendInfo.class);
                intent.putExtra("userName",talkList.get(position).getAccount());
                context.startActivity(intent);
            }
        });
        holder.talkTime.setText(talkList.get(position).getTalktime().toString().substring(0, 19));
        holder.talkContent.setText(talkList.get(position).getTalkcontent());
        holder.school.setText(talkList.get(position).getSchool());
        //当用户上传了照片时，则显示，否则隐藏图片控件
        if (talkList.get(position).getTalkphoto()!=null){
            Glide.with(context).load(talkList.get(position).getTalkphoto()).dontAnimate().placeholder(R.mipmap.loading).into(holder.talkPhoto);
        }else {
            holder.talkPhoto.setVisibility(View.GONE);
        }
        SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        if (talkList.get(position).getUse_id()==sp.getInt("use_id",0)){
            holder.talkDelete.setVisibility(View.VISIBLE);
        }
        holder.talkDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("talk_id",talkList.get(position).getTalk_id());
                client.post(constant.BASE_URL + constant.Talk_delete, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String json = new String(responseBody);
                        if (json.equals("1")){
                            inter.refresh();
                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(context, "请刷新重试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        //todo:通过账号名读取极光中的用户头像
        JMessageClient.getUserInfo(talkList.get(position).getAccount(), new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                Glide.with(context).load(userInfo.getAvatarFile()).into(holder.user_pic);
                holder.nickName.setText(userInfo.getNickname());
            }
        });
        if (talkList.get(position).getReplyTalkList()!=null){
            //todo:动态添加评论控件
            for (int i=0;i<talkList.get(position).getReplyTalkList().size();i++){
                //todo:强制关闭复用，防止各用户之间评论控件重复
                holder.setIsRecyclable(false);
                FlowLayout linear = new FlowLayout(context);
                TextView showView1 = new TextView(context);
                showView1.setText(JMessageClient.getMyInfo().getNickname()
                        +" :   "
                        +talkList.get(position).getReplyTalkList().get(i).getReplycontent());
                linear.addView(showView1);
                holder.xlReply.addView(linear);
            }
        }
        if (talkList.get(position).getPraiseList()!=null){
            //todo:动态添加点赞控件
            for (int i=0;i<talkList.get(position).getPraiseList().size();i++){
                //todo:强制关闭复用，防止各用户之间点赞控件重复
                holder.setIsRecyclable(false);
                TextView showView1 = new TextView(context);
                showView1.setId(talkList.get(position).getPraiseList().get(i).getPraise_id());
                showView1.setText(talkList.get(position).getPraiseList().get(i).getNickName()+"、");
                //如果分享中的点赞名单与登录用户的昵称相同，则更改点赞颜色
                if (talkList.get(position).getPraiseList().get(i).getNickName().equals(sp.getString("nickName",null))){
                    holder.praise.setImageResource(R.mipmap.likes);
                }
                holder.xlPraise.addView(showView1);
                holder.xlPraise.setVisibility(View.VISIBLE);
            }
        }
        if (talkList.get(position).getTopicName()!=null){
            //todo:动态改变话题
            holder.sharing_topic.setVisibility(View.VISIBLE);
            holder.sharing_topicName.setText(talkList.get(position).getTopicName());
        }
        //todo:点赞点击事件
        holder.praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否存在点赞，如不存在则直接添加点赞，存在的话判断是否已点赞
                if (holder.xlPraise.getChildCount()>1){
                    //判断点赞的人数
                    for (int i = 1;i<holder.xlPraise.getChildCount();i++){
                        TextView childAt = (TextView) holder.xlPraise.getChildAt(i);
                        //判断当前用户是否已经点赞，如果已经点赞，则下一次点击为取消点赞
                        if (childAt.getText().equals(JMessageClient.getMyInfo().getNickname()+"、")){
                            AsyncHttpClient client = new AsyncHttpClient();
                            RequestParams params = new RequestParams();
                            params.put("praise_id",holder.xlPraise.getChildAt(i).getId());
                            client.post(constant.BASE_URL+constant.Talk_praiseDelete,params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    try {
                                        String json = new String(responseBody,"utf-8");
                                        if (!json.isEmpty()){
                                            holder.praise.setImageResource(R.mipmap.like);
                                            Toast.makeText(context, "已取消点赞", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(context, "取消失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    Toast.makeText(context, "请刷新重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                            holder.xlPraise.removeView(childAt);
                            if (holder.xlPraise.getChildCount()<2){
                                holder.xlPraise.setVisibility(View.GONE);
                            }
                            //如果点赞列表中不存在时，则添加点赞
                        }else if (i==(holder.xlPraise.getChildCount()-1)){
                            //使点赞框可视
                            holder.xlPraise.setVisibility(View.VISIBLE);
                            SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                            AsyncHttpClient client = new AsyncHttpClient();
                            RequestParams params = new RequestParams();
                            params.put("talk_id",talkList.get(position).getTalk_id());
                            params.put("use_id",sp.getInt("use_id",0));
                            params.put("nickName",sp.getString("nickName",null));
                            client.post(constant.BASE_URL+constant.Talk_praiseAdd,params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    try {
                                        String json = new String(responseBody,"utf-8");
                                        if (!json.isEmpty()){
                                            holder.praise.setImageResource(R.mipmap.likes);
                                            holder.setIsRecyclable(false);
                                            TextView showView1 = new TextView(context);
                                            showView1.setId(Integer.parseInt(json));
                                            showView1.setText(JMessageClient.getMyInfo().getNickname()+"、");
                                            holder.xlPraise.addView(showView1);

                                            Toast.makeText(context, "已点赞", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(context, "点赞失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    Toast.makeText(context, "请刷新重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }else {
                    //使点赞框可视
                    holder.xlPraise.setVisibility(View.VISIBLE);
                    SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.put("talk_id",talkList.get(position).getTalk_id());
                    params.put("use_id",sp.getInt("use_id",0));
                    params.put("nickName",sp.getString("nickName",null));
                    client.post(constant.BASE_URL+constant.Talk_praiseAdd,params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                String json = new String(responseBody,"utf-8");
                                if (!json.isEmpty()){
                                    holder.praise.setImageResource(R.mipmap.likes);
                                    holder.setIsRecyclable(false);
                                    TextView showView1 = new TextView(context);
                                    showView1.setId(Integer.parseInt(json));
                                    showView1.setText(JMessageClient.getMyInfo().getNickname()+"、");
                                    holder.xlPraise.addView(showView1);

                                    Toast.makeText(context, "已点赞", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context, "点赞失败", Toast.LENGTH_SHORT).show();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(context, "请刷新重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        //todo:回复点击事件
        holder.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText inputServer = new EditText(context);
                inputServer.setFocusable(true);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("请输入发布内容").setIcon(
                        R.mipmap.logo).setView(inputServer).setNegativeButton("取消", null);
                builder.setPositiveButton("发布",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sp = context.getSharedPreferences("data",Context.MODE_PRIVATE);
                                final String replycontent = inputServer.getText().toString();
                                AsyncHttpClient client = new AsyncHttpClient();
                                RequestParams params = new RequestParams();
                                params.put("use_id",sp.getInt("use_id",0));
                                params.put("talk_id",talkList.get(position).getTalk_id());
                                params.put("replycontent",replycontent);
                                client.post(constant.BASE_URL + constant.ReplyTalk_add, params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        try {
                                            String json = new String(responseBody,"utf-8");
                                            if (!json.isEmpty()){
                                                holder.setIsRecyclable(false);
                                                FlowLayout linear = new FlowLayout(context);
                                                TextView showView1 = new TextView(context);
                                                showView1.setText(JMessageClient.getMyInfo().getNickname()+" :   "+replycontent);
                                                linear.addView(showView1);
                                                holder.xlReply.addView(linear);
                                                Toast.makeText(context, "发布成功", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                    }
                                });
                            }
                        });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return  talkList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView nickName,talkTime,talkContent,school,sharing_topicName;
        private LinearLayout sharing_topic,xlReply;
        private ImageView praise,reply,user_pic,talkDelete;
        private ImageView talkPhoto;
        private FlowLayout xlPraise;
        public MyViewHolder(View itemView) {
            super(itemView);
            nickName = (TextView) itemView.findViewById(R.id.nickName);
            talkTime = (TextView) itemView.findViewById(R.id.talkTime);
            talkContent = (TextView) itemView.findViewById(R.id.talkContent);
            school = (TextView) itemView.findViewById(R.id.school);
            user_pic = (ImageView) itemView.findViewById(R.id.xl_pic);
            talkPhoto = (ImageView) itemView.findViewById(R.id.talkPhoto);
            praise = (ImageView) itemView.findViewById(R.id.praise);
            reply = (ImageView) itemView.findViewById(R.id.reply);
            talkDelete = (ImageView) itemView.findViewById(R.id.talkDelete);
            xlReply = itemView.findViewById(R.id.xl_Reply);
            xlPraise = (FlowLayout) itemView.findViewById(R.id.xl_praise);
            sharing_topicName = itemView.findViewById(R.id.sharing_topicName);
            sharing_topic = itemView.findViewById(R.id.sharing_topic);
        }
    }
}
