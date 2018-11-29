package e.administrator.xy.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import e.administrator.xy.R;
import e.administrator.xy.activity.activityDetails;
import e.administrator.xy.pojo.activity;
import e.administrator.xy.pojo.clubmember;

/**
 * Created by Administrator on 2018/10/13.
 */

public class ActivityOnLineAdapter extends RecyclerView.Adapter<ActivityOnLineAdapter.MyViewHolder> {
    private Context context;
    private List<activity> activityList;

    public ActivityOnLineAdapter(Context context, List<activity> activityList) {
        this.context = context;
        this.activityList = activityList;
    }
    @Override
    public ActivityOnLineAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ActivityOnLineAdapter.MyViewHolder holder = new ActivityOnLineAdapter.MyViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activityonline,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ActivityOnLineAdapter.MyViewHolder holder, final int position) {
        JMessageClient.getUserInfo(activityList.get(position).getAccount(), new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                Glide.with(context).load(userInfo.getAvatarFile()).into(holder.activityAva);
            }
        });
        Glide.with(context).load(activityList.get(position).getAva()).into(holder.activityPic);
        holder.activityName.setText(activityList.get(position).getName());
        holder.activityTime.setText(activityList.get(position).getStarttime()+"-"+activityList.get(position).getEndtime());
        holder.item_activityOnLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,activityDetails.class);
                intent.putExtra("activity_id",activityList.get(position).getActivity_id());
                intent.putExtra("account",activityList.get(position).getAccount());
                intent.putExtra("onLine",0);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView activityAva,activityPic;
        private TextView activityName,activityTime;
        private RelativeLayout item_activityOnLine;
        public MyViewHolder(View itemView) {
            super(itemView);
            activityAva = itemView.findViewById(R.id.activityOnLine_ava);
            activityPic = itemView.findViewById(R.id.activityOnLine_pic);
            activityName = itemView.findViewById(R.id.activityOnLine_name);
            activityTime = itemView.findViewById(R.id.activityOnLine_time);
            item_activityOnLine = itemView.findViewById(R.id.item_activityOnLine);
        }
    }
}
