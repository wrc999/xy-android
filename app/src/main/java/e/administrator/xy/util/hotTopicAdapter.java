package e.administrator.xy.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.activity.topicDetail;
import e.administrator.xy.pojo.topic;

/**
 * Created by Administrator on 2018/10/10.
 */

public class hotTopicAdapter extends RecyclerView.Adapter<hotTopicAdapter.MyViewHolder> {
    private Context context;
    private List<topic> topicList;

    public hotTopicAdapter(Context context, List<topic> topicList) {
        this.context = context;
        this.topicList = topicList;
    }

    @Override
    public hotTopicAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            hotTopicAdapter.MyViewHolder holder = new hotTopicAdapter.MyViewHolder(LayoutInflater.
                    from(parent.getContext()).inflate(
                    R.layout.item_hottopic,parent,false));
            return holder;
    }

    @Override
    public void onBindViewHolder(hotTopicAdapter.MyViewHolder holder, final int position) {
            Glide.with(context).load(topicList.get(position).getTopicPic()).into(holder.ava);
            holder.intro.setText(topicList.get(position).getTopicIntro());
            holder.joins.setText(topicList.get(position).getTopicJoins()+"");
            holder.item_hotTopic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,topicDetail.class);
                    intent.putExtra("topicDetail",topicList.get(position));
                    context.startActivity(intent);
                }
            });
    }

    @Override
    public int getItemCount() {
        return topicList.size()>5? 5:topicList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView ava;
        private TextView intro,joins;
        private LinearLayout item_hotTopic;
        public MyViewHolder(View itemView) {
            super(itemView);
            ava = itemView.findViewById(R.id.item_topicAva);
            intro = itemView.findViewById(R.id.item_topicIntro);
            joins = itemView.findViewById(R.id.item_topicJoins);
            item_hotTopic = itemView.findViewById(R.id.item_hotTopic);
        }
    }
}
