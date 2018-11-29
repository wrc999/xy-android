package e.administrator.xy.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
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

import org.w3c.dom.Text;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.activity.MyFriend;
import e.administrator.xy.activity.friendInfo;
import e.administrator.xy.pojo.clubmember;
import e.administrator.xy.pojo.userInfo;

/**
 * Created by Administrator on 2018/10/15.
 */

public class clubMemberListAdapter extends RecyclerView.Adapter<clubMemberListAdapter.MyViewHolder>  {
    private Context context;
    private List<UserInfo> clubMemberList;
    private Long groupID;

    public clubMemberListAdapter(Context context, List<UserInfo> clubMemberList,Long groupID) {
        this.context = context;
        this.clubMemberList = clubMemberList;
        this.groupID = groupID;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        clubMemberListAdapter.MyViewHolder holder = new clubMemberListAdapter.MyViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clubmemberlist,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //设置会长徽章
        JMessageClient.getGroupInfo(groupID, new GetGroupInfoCallback() {
            @Override
            public void gotResult(int i, String s, GroupInfo groupInfo) {
                String account = clubMemberList.get(position).getUserName();
                if (groupInfo.getGroupOwner().equals(account)){
                    holder.item_memberleader.setImageResource(R.mipmap.king);
                    holder.item_memberleader.setVisibility(View.VISIBLE);
                    holder.clubMemberNickname.setTextColor(context.getResources().getColor(R.color.blue));
                    holder.clubMemberSchool.setTextColor(context.getResources().getColor(R.color.blue));
                }
            }
        });
        holder.item_clubMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,friendInfo.class);
                intent.putExtra("userName",clubMemberList.get(position).getUserName());
                intent.putExtra("groupID",groupID);
                context.startActivity(intent);
            }
        });
        holder.item_clubMember.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                JMessageClient.getGroupInfo(groupID, new GetGroupInfoCallback() {
                            @Override
                            public void gotResult(int i, String s, GroupInfo groupInfo) {
                                //获取当前登录用户的账号
                                String account = JMessageClient.getMyInfo().getUserName();
                                //如果该登录用户为群主，则可以移交社长职位，否则不行
                                if (groupInfo.getGroupOwner().equals(account)){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("是否将该成员设为会长？").setIcon(
                                            R.mipmap.logo).setNegativeButton("否", null);
                                    builder.setPositiveButton("是",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    AsyncHttpClient client = new AsyncHttpClient();
                                                    RequestParams params = new RequestParams();
                                                    params.put("account",clubMemberList.get(position).getUserName());
                                                    params.put("groupId",groupID);
                                                    client.post(constant.BASE_URL + constant.club_changeOwner, params, new AsyncHttpResponseHandler() {
                                                        @Override
                                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                            JMessageClient.getGroupInfo(groupID, new GetGroupInfoCallback() {
                                                                @Override
                                                                public void gotResult(int i, String s, GroupInfo groupInfo) {
                                                                    groupInfo.changeGroupAdmin(clubMemberList.get(position).getUserName(), "", new BasicCallback() {
                                                                        @Override
                                                                        public void gotResult(int i, String s) {
                                                                            if (i==0){
                                                                                Toast.makeText(context, "移交成功，您将成为普通成员", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                            Toast.makeText(context, "请刷新重试", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                    builder.show();
                                }else {
                                    Toast.makeText(context, "您暂未开通该权限", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                
                return true;
            }
        });
        Glide.with(context).load(clubMemberList.get(position).getAvatarFile()).into(holder.memberAva);
        holder.clubMemberNickname.setText(clubMemberList.get(position).getNickname());
        holder.clubMemberSchool.setText(clubMemberList.get(position).getExtra("school"));
    }

    @Override
    public int getItemCount() {
        return clubMemberList!=null? clubMemberList.size():0;
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
