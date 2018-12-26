package e.administrator.xy.util;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import e.administrator.xy.pojo.activity;
import e.administrator.xy.pojo.club;
import e.administrator.xy.pojo.Talk;
import e.administrator.xy.pojo.topic;

/**
 * Created by Administrator on 2018/6/3.
 */

public class JsonUtil {
    /**
     * 将Json字符串解析为Object对象
     * @param json : 待解析的Json字符串
     * @param tClass : Object对象的类型
     */
    public static <T> T getObjectFromJson(String json, Class<T> tClass) throws IOException {
        // 解析Json字符串
        Gson gson = new Gson();
        T t = gson.fromJson(json, tClass);
        return t;
    }
    /**
     * 将Json字符串解析为List集合
     */
    public static <T> List<T> getListFromJson(String json, Type type) throws IOException {
        // 解析Json字符串
        Gson gson = new Gson();
        List<T> list = gson.fromJson(json, type);   // 解析Json
        return list;
    }
    //将json字符串按talk_id(时间)进行排序
    public static List<Talk> JsonSortTalk(List<Talk> list){
        for (int i=0;i<list.size()-1;i++){
            for (int j=i+1;j<list.size();j++){
                if (list.get(i).getTalk_id()<list.get(j).getTalk_id()){
                    Talk t = new Talk();
                    t=list.get(i);
                    list.set(i,list.get(j));
                    list.set(j,t);
                }
            }
        }
        return list;
    }
    //将json字符串按话题发布时间进行排序
    public static List<topic> JsonSortTopic(List<topic> list){
        for (int i=0;i<list.size()-1;i++){
            for (int j=i+1;j<list.size();j++){
                if (list.get(i).getTopic_id()<list.get(j).getTopic_id()){
                    topic t = new topic();
                    t=list.get(i);
                    list.set(i,list.get(j));
                    list.set(j,t);
                }
            }
        }
        return list;
    }
    //将json字符串按成员数量进行排序
    public static List<club> JsonSortClub(List<club> list){
        for (int i=0;i<list.size()-1;i++){
            for (int j=i+1;j<list.size();j++){
                if (list.get(i).getMemberList().size()<list.get(j).getMemberList().size()){
                    club t = new club();
                    t=list.get(i);
                    list.set(i,list.get(j));
                    list.set(j,t);
                }
            }
        }
        return list;
    }

    //将活动按时间进行排序
    public static List<activity> JsonSortActivity(List<activity> list){
        for (int i=0;i<list.size()-1;i++){
            for (int j=i+1;j<list.size();j++){
                if (list.get(i).getActivity_id()<list.get(j).getActivity_id()){
                    activity t = new activity();
                    t=list.get(i);
                    list.set(i,list.get(j));
                    list.set(j,t);
                }
            }
        }
        return list;
    }
    //将活动按参与人数与点赞人数进行排序
    public static List<activity> JsonSortHotActivity(List<activity> list){
        List<activity> activities = JsonSortActivity(list);
        for (int i=0;i<activities.size()-1;i++){
            for (int j=i+1;j<activities.size();j++){
                if (activities.get(i).getjAccount().size()<activities.get(j).getjAccount().size()){
                    activity t = new activity();
                    t=activities.get(i);
                    activities.set(i,activities.get(j));
                    activities.set(j,t);
                }else if (activities.get(i).getaAccount().size()<activities.get(j).getaAccount().size()){
                    activity t = new activity();
                    t=activities.get(i);
                    activities.set(i,activities.get(j));
                    activities.set(j,t);
                }
            }
        }
        return activities;
    }
    //将已结束活动进行输出
    public static List<activity> JsonSortEndActivity(List<activity> list) throws Exception {
        List<activity> activities = JsonSortActivity(list);
        List<activity> activityList = new ArrayList<activity>();
        for (int i=0;i<activities.size();i++){
            int endTime = Integer.parseInt(activities.get(i).getEndtime().replace("-",""));
            int currentDate = Integer.parseInt(DateUtil.getCurrentDate().replace("-",""));
            if (endTime<currentDate){
                activityList.add(activities.get(i));
            }
            }
        return activityList;
    }

}
