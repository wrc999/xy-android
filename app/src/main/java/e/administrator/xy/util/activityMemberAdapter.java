package e.administrator.xy.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import e.administrator.xy.pojo.clubmember;
import e.administrator.xy.pojo.userInfo;

/**
 * Created by Administrator on 2018/10/13.
 */

public class activityMemberAdapter extends RecyclerView.Adapter<activityMemberAdapter.MyViewHolder> {
    private Context context;
    private List<String> activityMemberList;

    public activityMemberAdapter(Context context, List<String> activityMemberList) {
        this.context = context;
        this.activityMemberList = activityMemberList;
    }
    @Override
    public activityMemberAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        activityMemberAdapter.MyViewHolder holder = new activityMemberAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clubmember,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final activityMemberAdapter.MyViewHolder holder, final int position) {
        JMessageClient.getUserInfo(activityMemberList.get(position), new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                Glide.with(context).load(userInfo.getAvatarFile()).into(holder.memberAva);
            }
        });
    }

    @Override
    public int getItemCount() {
        return activityMemberList.size()>9? 7:activityMemberList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView memberAva;
        public MyViewHolder(View itemView) {
            super(itemView);
            memberAva = itemView.findViewById(R.id.clubDetailmember);
        }
    }
}
