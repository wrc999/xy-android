package e.administrator.xy.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import e.administrator.xy.R;

/**
 * Created by 13650 on 2018/6/13.
 * 会话列表
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.FriendView>{

    private Context context;
    private List<Conversation> conversationList;
    private OnItemClickListener mOnItemClickListener = null;

    //用一个集合将适配中创建的所有 holder对象存储到这个容器中，因为本类中有对holder中的控件创建了监听事件

    public ConversationAdapter(Context context, List<Conversation> conversationList){
        this.context = context;
        this.conversationList = conversationList;
    }

    @Override
    public FriendView onCreateViewHolder(ViewGroup parent, int viewType) {
        FriendView friendView = new FriendView(LayoutInflater.from(
                context).inflate(R.layout.item_conversation,parent,false));
        return friendView;
    }

    @Override
    public void onBindViewHolder(final FriendView holder, final int position) {
        Conversation conversation = conversationList.get(position);
        Glide.with(context).load(conversation.getAvatarFile()).placeholder(R.mipmap.loading).into(holder.friendImage);
        holder.userName.setText(conversation.getTitle());
        if (conversation.getUnReadMsgCnt()==0){
            holder.unReadMessage.setText(null);
        }else{
            holder.unReadMessage.setText(conversation.getUnReadMsgCnt()+"");
        }
        Date date0 = new Date(conversation.getLatestMessage().getCreateTime());
        SimpleDateFormat sdf1=new SimpleDateFormat("MM-dd HH:mm");
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        String date1 = sdf1.format(date0);//加日期
        String date = sdf.format(date0);//不加日期
        //判断是否是同一天的消息，如是，则不加日期，否则加
        if (new SimpleDateFormat("dd").format(new Date()).equals(date1.substring(3,5))){
            holder.lastMessageTime.setText(date);
        }else{
            holder.lastMessageTime.setText(date1);
        }

        Message message = conversation.getLatestMessage();
        MessageContent content = message.getContent();
        if (content.getContentType().toString()=="text"){
            holder.talk.setText(((TextContent)content).getText());
        }else if (content.getContentType().toString()=="image"){
            holder.talk.setText("(图片)");
        }else{
            holder.talk.setText("");
        }
        if( mOnItemClickListener!= null){
            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
            holder. itemView.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onLongClick(position);
                    //return false;
                    return true;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }


    static class FriendView extends RecyclerView.ViewHolder{
        ImageView friendImage;
        TextView userName,talk,lastMessageTime,unReadMessage;
        public FriendView(View itemView) {
            super(itemView);
            friendImage = itemView.findViewById(R.id.iv_head_image);
            userName = itemView.findViewById(R.id.tv_friend_name);
            talk = itemView.findViewById(R.id.tv_friend_message);
            lastMessageTime = itemView.findViewById(R.id.lastMessageTime);
            unReadMessage = itemView.findViewById(R.id.unReadMessage);
        }
    }

    /*设置监听*/
    public interface OnItemClickListener{
        void onClick(int position);
        void onLongClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this.mOnItemClickListener=onItemClickListener;
    }

}
