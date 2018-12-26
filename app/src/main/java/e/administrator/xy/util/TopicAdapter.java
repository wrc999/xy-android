package e.administrator.xy.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.activity.TalkAddActivity;
import e.administrator.xy.activity.clubDetails;
import e.administrator.xy.activity.topicDetail;
import e.administrator.xy.pojo.club;
import e.administrator.xy.pojo.topic;
import e.administrator.xy.pojo.userInfo;

/**
 * Created by Administrator on 2018/10/10.
 */

public class TopicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<topic> topicList;
    private String resource;
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

    public TopicAdapter(Context context, List<topic> topicList) {
        this.context = context;
        this.topicList = topicList;
    }
    public TopicAdapter(Context context, List<topic> topicList,String resource) {
        this.context = context;
        this.topicList = topicList;
        this.resource = resource;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            TopicAdapter.MyViewHolder holder = new TopicAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_topic,parent,false));
            return holder;
        } else if (viewType == TYPE_FOOTER) {
            //脚布局
            TopicAdapter.FootViewHolder holder = new TopicAdapter.FootViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.recycler_load_more_layout,parent,false));
            return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            Glide.with(context).load(topicList.get(position).getTopicPic()).placeholder(R.mipmap.loading).into(myViewHolder.topicAva);
            myViewHolder.topicName.setText(topicList.get(position).getTopicName());
            myViewHolder.topicIntro.setText(topicList.get(position).getTopicIntro());
            myViewHolder.topicJoins.setText("讨论："+topicList.get(position).getTopicJoins()+"");
            myViewHolder.item_topic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (resource!=null){
                        if (resource.equals("talkAdd")){
                            SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("topicName", topicList.get(position).getTopicName());
                            edit.commit();
                            //调用手机自带返回键
                            Runtime runtime = Runtime.getRuntime();
                            try {
                                runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }else {
                        Intent intent = new Intent(context,topicDetail.class);
                        intent.putExtra("topicDetail",topicList.get(position));
                        context.startActivity(intent);
                    }
                }
            });
            

        } else if (holder instanceof FootViewHolder) {
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
        return topicList != null ? topicList.size() + 1 : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView topicName,topicIntro,topicJoins;
        private ImageView topicAva;
        private LinearLayout item_topic;
        public MyViewHolder(View itemView) {
            super(itemView);
            topicName = itemView.findViewById(R.id.topicName);
            topicIntro = itemView.findViewById(R.id.topicIntro);
            topicJoins = itemView.findViewById(R.id.topicJoins);
            topicAva = itemView.findViewById(R.id.topicAva);
            item_topic = itemView.findViewById(R.id.item_topic);
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
