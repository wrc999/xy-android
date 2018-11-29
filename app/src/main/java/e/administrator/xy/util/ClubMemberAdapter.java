package e.administrator.xy.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import e.administrator.xy.R;
import e.administrator.xy.pojo.club;
import e.administrator.xy.pojo.clubmember;
import e.administrator.xy.pojo.view;

/**
 * Created by Administrator on 2018/10/13.
 */

public class ClubMemberAdapter extends RecyclerView.Adapter<ClubMemberAdapter.MyViewHolder> {
    private Context context;
    private List<clubmember> clubMemberList;

    public ClubMemberAdapter(Context context, List<clubmember> clubMemberList) {
        this.context = context;
        this.clubMemberList = clubMemberList;
    }
    @Override
    public ClubMemberAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ClubMemberAdapter.MyViewHolder holder = new ClubMemberAdapter.MyViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clubmember,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ClubMemberAdapter.MyViewHolder holder, final int position) {
        JMessageClient.getUserInfo(clubMemberList.get(position).getAccount(), new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                Glide.with(context).load(userInfo.getAvatarFile()).into(holder.memberAva);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clubMemberList.size()>9? 7:clubMemberList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView memberAva;
        public MyViewHolder(View itemView) {
            super(itemView);
            memberAva = itemView.findViewById(R.id.clubDetailmember);
        }
    }
}
