package e.administrator.xy.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
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

import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.activity.clubDetails;
import e.administrator.xy.pojo.club;

/**
 * Created by Administrator on 2018/10/10.
 */

public class ClubAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<club> clubList;
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

    public ClubAdapter(Context context, List<club> clubList) {
        this.context = context;
        this.clubList = clubList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_club,parent,false));
            return holder;
        } else if (viewType == TYPE_FOOTER) {
            //脚布局
            FootViewHolder holder = new FootViewHolder(LayoutInflater.from(parent.getContext()).inflate
                    (R.layout.recycler_load_more_layout,parent,false));
            return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            final SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_PRIVATE);
            Glide.with(context).load(clubList.get(position).getClubAva()).into(myViewHolder.clubAva);
            myViewHolder.clubName.setText(clubList.get(position).getClubName());
            myViewHolder.clubSchool.setText(clubList.get(position).getClubSchool());
            myViewHolder.clubIntro.setText(clubList.get(position).getClubIntro());
            if (clubList.get(position).getFocusList()!=null){
                //todo:动态改变关注控件
                for (int i=0;i<clubList.get(position).getFocusList().size();i++){
                    //如果分享中的点赞名单与登录用户的昵称相同，则更改点赞颜色
                    if (clubList.get(position).getFocusList().get(i).getAccount().equals(sp.getString("account",null))){
                        myViewHolder.focus.setText("已关注");
                        myViewHolder.focus.setTag("unfocus");
                        myViewHolder.focus.setBackgroundResource(R.drawable.radiusgray);
                    }
                }
            }
            myViewHolder.focus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断是否关注
                    if (myViewHolder.focus.getTag().equals("focus")){
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        params.put("account",sp.getString("account",null));
                        params.put("club_id",clubList.get(position).getClub_id());
                        client.post(constant.BASE_URL + constant.club_focus, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try {
                                    String json = new String(responseBody,"utf-8");
                                    if (!json.isEmpty()){
                                        myViewHolder.focus.setText("已关注");
                                        myViewHolder.focus.setTag("unfocus");
                                        myViewHolder.focus.setBackgroundResource(R.drawable.radiusgray);
                                    }else {
                                        Toast.makeText(context, "取消失败", Toast.LENGTH_SHORT).show();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(context, "请刷新重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        params.put("account",sp.getString("account",null));
                        params.put("club_id",clubList.get(position).getClub_id());
                        client.post(constant.BASE_URL + constant.club_unfocus, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try {
                                    String json = new String(responseBody,"utf-8");
                                    if (!json.isEmpty()){
                                        myViewHolder.focus.setText("+关注");
                                        myViewHolder.focus.setTag("focus");
                                        myViewHolder.focus.setBackgroundResource(R.drawable.radius);
                                    }else {
                                        Toast.makeText(context, "关注失败", Toast.LENGTH_SHORT).show();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(context, "请刷新重试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });
            myViewHolder.item_club.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent0 = new Intent(context, clubDetails.class);
                    intent0.putExtra("club_id",clubList.get(position).getClub_id());
                    intent0.putExtra("account",clubList.get(position).getAccount());
                    context.startActivity(intent0);
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
        return clubList != null ? clubList.size() + 1 : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView clubName,clubIntro,clubSchool;
        private ImageView clubAva;
        private Button focus;
        private LinearLayout item_club;
        public MyViewHolder(View itemView) {
            super(itemView);
            clubName = itemView.findViewById(R.id.clubName);
            clubIntro = itemView.findViewById(R.id.clubIntro);
            clubSchool = itemView.findViewById(R.id.clubSchool);
            clubAva = itemView.findViewById(R.id.clubAva);
            focus = itemView.findViewById(R.id.focus);
            item_club = itemView.findViewById(R.id.item_club);
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
