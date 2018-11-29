package e.administrator.xy.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import e.administrator.xy.R;
import e.administrator.xy.pojo.clubmember;
import e.administrator.xy.pojo.view;

/**
 * Created by Administrator on 2018/10/13.
 */

public class ClubPicAdapter extends RecyclerView.Adapter<ClubPicAdapter.MyViewHolder> {
    private Context context;
    private List<view> clubPicList;

    public ClubPicAdapter(Context context, ArrayList<view> clubPicList) {
        this.context = context;
        this.clubPicList = clubPicList;
    }
    @Override
    public ClubPicAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ClubPicAdapter.MyViewHolder holder = new ClubPicAdapter.MyViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clubpic,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ClubPicAdapter.MyViewHolder holder, final int position) {
        Glide.with(context).load(clubPicList.get(position).getPhoto()).into(holder.pic);
    }

    @Override
    public int getItemCount() {
        return clubPicList.size()>3? 3:clubPicList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView pic;
        public MyViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.item_clubPic);
        }
    }
}
