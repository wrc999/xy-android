package e.administrator.xy.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.api.BasicCallback;
import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.activity.activityDetails;
import e.administrator.xy.pojo.activity;

/**
 * Created by Administrator on 2018/6/13.
 */

public class ActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<activity> activityList;
    //普通布局的type
    static final int TYPE_ITEM = 0;
    //脚布局
    static final int TYPE_FOOTER = 1;
    //正在加载更多
    static final int LOADING_MORE = 1;
    //没有更多
    static final int NO_MORE = 2;
    //脚布局当前的状态,默认为没有更多
    int footer_state = 0;

    public ActivityAdapter(Context context, List<activity> activityList) {
        this.context = context;
        this.activityList = activityList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_activity,parent,false));
            return holder;
        } else if (viewType == TYPE_FOOTER) {
            //脚布局
            FootViewHolder holder = new FootViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.recycler_load_more_layout,parent,false));
            return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ActivityAdapter.MyViewHolder) {
            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            try {
                if (DateUtil.judgeTime(activityList.get(position).getStarttime())){
                    myViewHolder.item_activityse.setText("正在进行中");
                }else {
                    myViewHolder.item_activityse.setText("已结束");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (activityList.get(position).getaAccount()!=null){
                myViewHolder.item_activityLoveNum.setText(activityList.get(position).getaAccount().size()+"");
            }
            if (activityList.get(position).getjAccount()!=null){
                myViewHolder.item_activityJoinsNum.setText(activityList.get(position).getjAccount().size()+"");
            }
            Glide.with(context).load(activityList.get(position).getAva()).into(myViewHolder.item_activityAva);
            myViewHolder.item_activityName.setText(activityList.get(position).getName());
            myViewHolder.item_activityTime.setText(activityList.get(position).getStarttime()+"-"+activityList.get(position).getEndtime());
            myViewHolder.item_activityPlace.setText(activityList.get(position).getPlace());
            String[] keyword = activityList.get(position).getKeyword().split(",");
            //防止刷新页面后重复添加组件
            myViewHolder.item_keyWord.removeAllViews();
            for (int i=0;i<keyword.length;i++){
                TextView badge = new TextView(context);
                badge.setText(keyword[i]);
                badge.setTextSize(12);
                badge.setPadding(20,10,20,10);
                GradientDrawable drawable=new GradientDrawable();
                drawable.setCornerRadius(30);
                switch (keyword[i]){
                    case "创意":drawable.setColor(Color.rgb(138,76,75));break;
                    case "晚会":drawable.setColor(Color.rgb(118,244,148));break;
                    case "比赛":drawable.setColor(Color.rgb(214,240,237));break;
                    case "公益":drawable.setColor(Color.rgb(250,246,79));break;
                    case "运动":drawable.setColor(Color.rgb(50,205,50));break;
                    case "摄影":drawable.setColor(Color.rgb(175,81,195));break;
                    case "旅游":drawable.setColor(Color.rgb(89,106,159));break;
                    case "电影":drawable.setColor(Color.rgb(183,96,138));break;
                    case "创业":drawable.setColor(Color.rgb(55,204,151));break;
                    case "职场":drawable.setColor(Color.rgb(185,169,220));break;
                    case "讲座":drawable.setColor(Color.rgb(141,217,118));break;
                    case "沙龙":drawable.setColor(Color.rgb(229,150,180));break;
                    case "日常运动":drawable.setColor(Color.rgb(255,100,97));break;
                    case "娱乐":drawable.setColor(Color.rgb(64,224,208));break;
                    case "演唱会":drawable.setColor(Color.rgb(96,173,122));break;
                    case "其他":drawable.setColor(Color.rgb(205,200,181));break;
                }
                badge.setBackground(drawable);
                myViewHolder.item_keyWord.addView(badge);
            }
            String account = context.getSharedPreferences("data", Context.MODE_PRIVATE).getString("account", null);
            if (activityList.get(position).getAccount().equals(account)){
                myViewHolder.item_underOnlineActivity.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("是否删除该活动？").setIcon(
                                R.mipmap.logo).setNegativeButton("否", null);
                        builder.setPositiveButton("是",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        AsyncHttpClient client = new AsyncHttpClient();
                                        RequestParams params = new RequestParams();
                                        params.put("activity_id",activityList.get(position).getActivity_id());
                                        client.post(constant.BASE_URL + constant.activity_delete, params, new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                Toast.makeText(context, "请刷新重试", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                        builder.show();
                        return true;
                    }
                });
            }
            myViewHolder.item_underOnlineActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,activityDetails.class);
                    intent.putExtra("activity_id",activityList.get(position).getActivity_id());
                    intent.putExtra("account",activityList.get(position).getAccount());
                    intent.putExtra("onLine",0);
                    context.startActivity(intent);
                }
            });
        } else if (holder instanceof ActivityAdapter.FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            if (position == 0) {//如果第一个就是脚布局,,那就让他隐藏
                footViewHolder.mProgressBar.setVisibility(View.GONE);
                footViewHolder.tv_line1.setVisibility(View.GONE);
                footViewHolder.tv_line2.setVisibility(View.GONE);
                footViewHolder.tv_state.setText("");
            }
            switch (footer_state) {//根据状态来让脚布局发生改变
                case TYPE_ITEM:
                    footViewHolder.mProgressBar.setVisibility(View.GONE);
                    footViewHolder.tv_line1.setVisibility(View.GONE);
                    footViewHolder.tv_line2.setVisibility(View.GONE);
                    footViewHolder.tv_state.setText("");
                    break;
                case LOADING_MORE:
                    footViewHolder.mProgressBar.setVisibility(View.VISIBLE);
                    footViewHolder.tv_line1.setVisibility(View.GONE);
                    footViewHolder.tv_line2.setVisibility(View.GONE);
                    footViewHolder.tv_state.setText("正在加载...");
                    break;
                case NO_MORE:
                    footViewHolder.mProgressBar.setVisibility(View.GONE);
                    footViewHolder.tv_line1.setVisibility(View.VISIBLE);
                    footViewHolder.tv_line2.setVisibility(View.VISIBLE);
                    footViewHolder.tv_state.setText("我是有底线的");
                    footViewHolder.tv_state.setTextColor(Color.parseColor("#ff00ff"));
                    break;
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        //如果position加1正好等于所有item的总和,说明是最后一个item,将它设置为脚布局
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return activityList != null ? activityList.size() + 1 : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout item_underOnlineActivity,item_keyWord;
        private ImageView item_activityAva;
        private TextView item_activityName,item_activityTime,item_activityPlace
                ,item_activityJoinsNum,item_activityLoveNum,item_activityse;
        public MyViewHolder(View view) {
            super(view);
            item_underOnlineActivity = view.findViewById(R.id.item_underOnlineActivity);
            item_activityAva = view.findViewById(R.id.item_activityAva);
            item_activityName = view.findViewById(R.id.item_activityName);
            item_activityTime = view.findViewById(R.id.item_activityTime);
            item_activityPlace = view.findViewById(R.id.item_activityPlace);
            item_activityJoinsNum = view.findViewById(R.id.item_activityJoinsNum);
            item_activityLoveNum = view.findViewById(R.id.item_activityLoveNum);
            item_keyWord = view.findViewById(R.id.item_keyWord);
            item_activityse = view.findViewById(R.id.item_activityse);
        }
    }
    /**
     * 脚布局的ViewHolder
     */
    public static class FootViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar mProgressBar;
        private TextView tv_state,tv_line1,tv_line2;

        public FootViewHolder(View itemView) {
            super(itemView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
            tv_state = (TextView) itemView.findViewById(R.id.foot_view_item_tv);
            tv_line1 = (TextView) itemView.findViewById(R.id.tv_line1);
            tv_line2 = (TextView) itemView.findViewById(R.id.tv_line2);

        }
    }

    /**
     * 改变脚布局的状态的方法,在activity根据请求数据的状态来改变这个状态
     *
     * @param state
     */
    public void changeState(int state) {
        this.footer_state = state;
        notifyDataSetChanged();
    }
}
