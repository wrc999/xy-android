package e.administrator.xy.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import e.administrator.xy.R;
import e.administrator.xy.activity.ImageBrowseActivity;
import e.administrator.xy.activity.MainActivity;
import e.administrator.xy.activity.ViewListActivity;
import e.administrator.xy.pojo.activity;
import e.administrator.xy.pojo.view;

/**
 * Created by Administrator on 2018/6/14.
 */

public class viewAdapter extends RecyclerView.Adapter<viewAdapter.MyViewHolder> {
    private Context context;
    private List<String> viewList;

    public viewAdapter(Context context, List<String> viewList) {
        this.context = context;
        this.viewList = viewList;
    }

    @Override
    public viewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        viewAdapter.MyViewHolder holder = new viewAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(viewAdapter.MyViewHolder holder, final int position) {
        Glide.with(context).load(viewList.get(position)).placeholder(R.mipmap.loading).into(holder.view);
        //todo:单击放大查看(根据原图自适应宽高)
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImageBrowseActivity.class);
                intent.putStringArrayListExtra("imageList", (ArrayList<String>) viewList);
                intent.putExtra("index", position);
                context.startActivity(intent);
                //自己敲的预览图片，不太好
//                final AlertDialog dialog = new AlertDialog.Builder(context).create();
//                ImageView bigImage = getImageView();
//                dialog.setView(bigImage,0,0,0,0);
//                dialog.show();
//            }
//
//            private ImageView getImageView() {
//                ImageView iv = new ImageView(context);
//                //fitXY 这种图片的显示方式的效果是：根据 ImageView 设置的大小拉伸图片以填充满空间，（单独设置此属性时）图片会变形。
//                iv.setScaleType(ImageView.ScaleType.FIT_XY);
//                //adjustViewBounds 是限制图片在显示时保持原图比例。（和 fitXY 显示方式合用能到达自适应的效果）
//                iv.setAdjustViewBounds(true);
//                //宽高
//                iv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                Glide.with(context).load(viewList.get(position).getPhoto()).thumbnail( 0.2f ).into(iv);
//                return iv;
//            }
            }
        });
        //todo:长按下载
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("享悦").setIcon(
                        R.mipmap.logo).setMessage("是否下载").setNegativeButton("取消啊", null);
                builder.setPositiveButton("确定啊", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        String downUrl = viewList.get(position);
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.get(downUrl, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                if (responseBody != null){
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                                    sdf.format(new Date());
                                    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"xyPic";
                                    File file = new File(path);
                                    if (!file.exists()){
                                        file.mkdir();
                                    }
                                    String fileName = sdf.format(new Date())+".jpg";
                                    File pic = new File(file,fileName);
                                    try {
                                        FileOutputStream fos = new FileOutputStream(pic);
                                        fos.write(responseBody, 0, responseBody.length);
                                        fos.flush();
                                        fos.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(context, "下载成功"+responseBody.length, Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
    });
    }

    @Override
    public int getItemCount() {
        return viewList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView view;
        public MyViewHolder(View itemView) {
            super(itemView);
            view = (ImageView) itemView.findViewById(R.id.viewList_pic);
        }
    }
}
