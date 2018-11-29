package e.administrator.xy.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupIDListCallback;
import cn.jpush.im.android.api.callback.GetGroupMembersCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.pojo.message;

/**
 * Created by 13650 on 2018/6/13.
 * 会话列表
 */

public class systemMessageAdapter extends RecyclerView.Adapter<systemMessageAdapter.FriendView>{

        private Context context;
        private List<message> messageList;

        //用一个集合将适配中创建的所有 holder对象存储到这个容器中，因为本类中有对holder中的控件创建了监听事件

        public systemMessageAdapter(Context context, List<message> messageList){
            this.context = context;
            this.messageList = messageList;
        }

        @Override
        public FriendView onCreateViewHolder(ViewGroup parent, int viewType) {
            FriendView friendView = new FriendView(LayoutInflater.from(
                    context).inflate(R.layout.item_system_message,parent,false));
            return friendView;
        }

        @Override
        public void onBindViewHolder(final FriendView holder, final int position) {
            JMessageClient.getUserInfo(messageList.get(position).getSendAccount(), new GetUserInfoCallback() {
                @Override
                public void gotResult(int i, String s, UserInfo userInfo) {
                    Glide.with(context).load(userInfo.getAvatarFile()).into(holder.ava);
                    holder.nickName.setText(userInfo.getNickname());
                }
            });
            holder.root.setText(messageList.get(position).getRoot());
            //如果是好友申请，则附加消息为添加好友原因
            if (messageList.get(position).getRoot().equals("好友申请")) {
                holder.reason.setText(messageList.get(position).getReason());
            }
            //如果是社团申请，则附加消息为所申请的社团名
            if (messageList.get(position).getRoot().equals("社团申请")) {
                holder.reason.setText(messageList.get(position).getReason());
            }
            //如果是活动申请，则附加消息为所申请的活动名
            if (messageList.get(position).getRoot().equals("活动申请")) {
                holder.reason.setText(messageList.get(position).getReason());
            }
            if (messageList.get(position).getResult()==1){
                holder.result.setVisibility(View.VISIBLE);
                holder.accept.setVisibility(View.GONE);
                holder.refuse.setVisibility(View.GONE);
                holder.result.setText("已同意");
            }
            if (messageList.get(position).getResult()==2){
                holder.result.setVisibility(View.VISIBLE);
                holder.accept.setVisibility(View.GONE);
                holder.refuse.setVisibility(View.GONE);
                holder.result.setText("已拒绝");
            }
            if (messageList.get(position).getResult()==0){
                holder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //同意社团申请
                        if (messageList.get(position).getRoot().equals("社团申请")) {
                            final List<String> list = new ArrayList<String>();
                            list.add(messageList.get(position).getSendAccount());
                            AsyncHttpClient client1 = new AsyncHttpClient();
                            RequestParams params1 = new RequestParams();
                            params1.put("account",messageList.get(position).getSendAccount());
                            params1.put("club_id",messageList.get(position).getClub_id());
                            client1.post(constant.BASE_URL + constant.club_join, params1, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    String json = new String(responseBody);
                                    if (json!=null){
                                        JMessageClient.addGroupMembers(Long.parseLong(json), list, new BasicCallback() {
                                            @Override
                                            public void gotResult(int i, String s) {
                                                if (i==0){
                                                    Toast.makeText(context, "已同意该申请", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    Toast.makeText(context, "请重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        //同意活动申请
                        if (messageList.get(position).getRoot().equals("活动申请")) {

                        }
                        //同意好友申请
                        if (messageList.get(position).getRoot().equals("好友申请")){
                            ContactManager.acceptInvitation(messageList.get(position).getSendAccount(),"", new BasicCallback() {
                                @Override
                                public void gotResult(int i, String s) {
                                    if (i == 0) {
                                        AsyncHttpClient client = new AsyncHttpClient();
                                        RequestParams params = new RequestParams();
                                        params.put("message_id",messageList.get(position).getMessage_id());
                                        params.put("result",1);
                                        client.post(constant.BASE_URL + constant.message_edit, params, new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                String json = new String(responseBody);
                                                if (json!=null){
                                                    Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                Toast.makeText(context, "请刷新重试", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(context, "添加失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        //设置该消息已读
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        params.put("message_id",messageList.get(position).getMessage_id());
                        params.put("result",1);
                        client.post(constant.BASE_URL + constant.message_edit, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String json = new String(responseBody);
                                if (json!=null){
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(context, "请刷新重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                        holder.result.setVisibility(View.VISIBLE);
                        holder.accept.setVisibility(View.GONE);
                        holder.refuse.setVisibility(View.GONE);
                        holder.result.setText("已同意");
                    }
                });
                holder.refuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //设置该消息已读
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        params.put("message_id",messageList.get(position).getMessage_id());
                        params.put("result",2);
                        client.post(constant.BASE_URL + constant.message_edit, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String json = new String(responseBody);
                                if (json!=null){
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(context, "请刷新重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                        //拒绝好友申请
                        if (messageList.get(position).getRoot().equals("好友申请")) {
                            ContactManager.declineInvitation(messageList.get(position).getSendAccount(),"", "", new BasicCallback() {
                                @Override
                                public void gotResult(int i, String s) {
                                    if (i == 0) {
                                        AsyncHttpClient client = new AsyncHttpClient();
                                        RequestParams params = new RequestParams();
                                        params.put("message_id",messageList.get(position).getMessage_id());
                                        params.put("result",2);
                                        client.post(constant.BASE_URL + constant.message_edit, params, new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                String json = new String(responseBody);
                                                if (json!=null){
                                                    Toast.makeText(context, "拒绝成功", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                Toast.makeText(context, "请刷新重试", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(context, "拒绝失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        holder.result.setVisibility(View.VISIBLE);
                        holder.accept.setVisibility(View.GONE);
                        holder.refuse.setVisibility(View.GONE);
                        holder.result.setText("已拒绝");
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }


        static class FriendView extends RecyclerView.ViewHolder{
            ImageView ava;
            TextView nickName,root,reason,accept,refuse,result;
            public FriendView(View itemView) {
                super(itemView);
                ava = itemView.findViewById(R.id.item_systemMessage_ava);
                nickName = itemView.findViewById(R.id.item_systemMessage_nickName);
                root = itemView.findViewById(R.id.item_systemMessage_root);
                reason = itemView.findViewById(R.id.item_systemMessage_reason);
                accept = itemView.findViewById(R.id.item_systemMessage_accept);
                refuse = itemView.findViewById(R.id.item_systemMessage_refuse);
                result = itemView.findViewById(R.id.item_systemMessage_result);
            }
        }
}
