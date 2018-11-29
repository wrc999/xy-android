package e.administrator.xy.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.jpush.im.android.api.model.UserInfo;
import e.administrator.xy.R;
import e.administrator.xy.pojo.User;

/**
 * Created by 13650 on 2018/6/13.
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendView>{

    private Context context;
    private List<User> list;
    private OnItemClickListener mOnItemClickListener = null;

    //用一个集合将适配中创建的所有 holder对象存储到这个容器中，因为本类中有对holder中的控件创建了监听事件

    public FriendListAdapter(Context context, List<User> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public FriendView onCreateViewHolder(ViewGroup parent, int viewType) {
        FriendView friendView = new FriendView(LayoutInflater.from(
                context).inflate(R.layout.item_myfriend,parent,false));
        return friendView;
    }

    @Override
    public void onBindViewHolder(final FriendView holder, final int position) {
        final User user = list.get(position);
        final String userName = user.getName();
        Glide.with(context).load(list.get(position).getAva()).into(holder.friendImage);
        holder.userName.setText(userName);
        //根据position获取首字母作为目录catalog
        String catalog = list.get(position).getFirstLetter();

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(catalog)){
            holder.catalog.setVisibility(View.VISIBLE);
            holder.catalog.setText(user.getFirstLetter().toUpperCase());
        }else{
            holder.catalog.setVisibility(View.GONE);
        }
        if( mOnItemClickListener!= null){
            holder.userName.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(user.getAccount());

                }
            });
            holder.userName.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onLongClick(user.getAccount());
                    //return false;
                    return true;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class FriendView extends RecyclerView.ViewHolder{
        ImageView friendImage;
        TextView userName,catalog;
        public FriendView(View itemView) {
            super(itemView);
            friendImage = itemView.findViewById(R.id.ava);
            userName = itemView.findViewById(R.id.name);
            catalog = itemView.findViewById(R.id.catalog);
        }
    }

    /*设置监听*/
    public interface OnItemClickListener{
        void onClick(String userName);
        void onLongClick(String userName);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this.mOnItemClickListener=onItemClickListener;
    }
    /**
     * 获取catalog首次出现位置
     */
    public int getPositionForSection(String catalog) {
        for (int i = 0; i < list.size(); i++) {
            String sortStr = list.get(i).getFirstLetter();
            if (catalog.equalsIgnoreCase(sortStr)) {
                return i;
            }
        }
        return -1;
    }
}
