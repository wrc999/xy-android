package e.administrator.xy.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.activity.friendInfo;
import e.administrator.xy.pojo.userInfo;

/**
 * Created by Administrator on 2018/10/15.
 */

public class activityMemberListAdapter extends RecyclerView.Adapter<activityMemberListAdapter.MyViewHolder>  {
    private Context context;
    private List<String> activityMemberList;
    private String applyPeople;

    public activityMemberListAdapter(Context context, List<String> activityMemberList,String applyPeople) {
        this.context = context;
        this.activityMemberList = activityMemberList;
        this.applyPeople = applyPeople;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        activityMemberListAdapter.MyViewHolder holder = new activityMemberListAdapter.MyViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clubmemberlist,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //设置发起人徽章
        String account = activityMemberList.get(position);
        if (account.equals(applyPeople)){
            holder.item_memberleader.setImageResource(R.mipmap.activityapply);
            holder.item_memberleader.setVisibility(View.VISIBLE);
            holder.clubMemberNickname.setTextColor(context.getResources().getColor(R.color.blue));
            holder.clubMemberSchool.setTextColor(context.getResources().getColor(R.color.blue));
        }
        holder.item_clubMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,friendInfo.class);
                intent.putExtra("userName",activityMemberList.get(position));
                context.startActivity(intent);
            }
        });

        JMessageClient.getUserInfo(activityMemberList.get(position), new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                Glide.with(context).load(userInfo.getAvatarFile()).into(holder.memberAva);
                holder.clubMemberNickname.setText(userInfo.getNickname());
                holder.clubMemberSchool.setText(userInfo.getExtra("school"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return activityMemberList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView memberAva,item_memberleader;
        private TextView clubMemberNickname,clubMemberSchool;
        private LinearLayout item_clubMember;
        public MyViewHolder(View itemView) {
            super(itemView);
            memberAva = itemView.findViewById(R.id.clubDetailMemberList);
            clubMemberNickname = itemView.findViewById(R.id.clubMemberNickname);
            clubMemberSchool = itemView.findViewById(R.id.clubMemberSchool);
            item_clubMember = itemView.findViewById(R.id.item_clubMember);
            item_memberleader = itemView.findViewById(R.id.item_memberleader);
        }
    }
}
