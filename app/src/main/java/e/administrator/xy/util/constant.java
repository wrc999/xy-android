package e.administrator.xy.util;

/**
 * Created by Administrator on 2018/6/11.
 */

public class constant {
    public static final String AccessKey = "wV7P1wLrSxWeuuYMPBlixj7Rm_ioc0iZqA-5hNhf";//此处填你自己的AccessKey
    public static final String SecretKey = "dw74d1cWt56_TxXg4ZcC6JfbR1gPFV-f98sCTgim";//此处填你自己的SecretKey
    public static final String bucket = "wrc999";//七牛中的存储控件

    public static final String BASE_URL1 = "http://192.168.208.1/xy/";//ip地址
    public static final String BASE_URL = "http://101.132.192.120/xy/";//阿里云服务器地址

    public static final String user_login = "user/login";//登录接口
    public static final String user_add = "user/add";//用户添加接口
    public static final String user_edit = "user/edit";//用户资料更新接口
    public static final String user_get = "user/get";//查询用户接口
    public static final String user_getByAccount= "user/getByAccount";//查询用户接口
    public static final String user_changePassWord= "user/changePassWord";//修改密码接口
    public static final String user_suggestAdd= "user/suggestAdd";//意见反馈接口

    public static final String Talk_add = "talk/add";//分享发布接口
    public static final String Talk_delete = "talk/delete";//分享发布接口
    public static final String Talk_get = "talk/get";//分享查询接口
    public static final String ReplyTalk_add = "talk/replyAdd";//回复添加
    public static final String Talk_praiseAdd = "talk/praiseAdd";//添加点赞
    public static final String Talk_praiseDelete = "talk/praiseDelete";//删除点赞
    public static final String TalkCollect_add = "talk/collectAdd";//分享收藏
    public static final String TalkCollect_find = "talk/collectGet";//分享收藏获取
    public static final String TalkCollect_delete = "talk/collectDelete";//删除收藏
    public static final String topicAdd = "talk/topicAdd";//发布话题
    public static final String topicGet = "talk/topicGet";//获取话题

    public static final String activity_add = "activity/add";//活动发布
    public static final String activity_getByAccount = "activity/getByAccount";//根据用户id查询活动
    public static final String activity_getOnLine = "activity/getOnLine";//查询所有线上活动
    public static final String activity_getUnderLine = "activity/getUnderLine";//查询所有线下活动
    public static final String activity_getByActivityId = "activity/getByActivityId";//根据用户id查询活动
    public static final String activity_activitySearch = "activity/activitySearch";//根据用户id查询活动
    public static final String activity_delete = "activity/delete";//最火活动查询
    public static final String activity_joinsAdd = "activity/joinsAdd";//活动报名
    public static final String activity_joinsDelete = "activity/joinsDelete";//活动报名
    public static final String activity_getActivityJoins = "activity/getActivityJoins";//活动报名名单获取
    public static final String activity_praiseAdd = "activity/praiseAdd";//活动报名
    public static final String activity_praiseDelete = "activity/praiseDelete";//活动报名
    public static final String activity_getExamine = "activity/getExamine";//获取未审核
    public static final String activity_getFalseExamine = "activity/getFalseExamine";//获取审核未通过

    public static final String club_add="club/add";//创建社团
    public static final String club_delete="club/delete";//删除社团
    public static final String club_get="club/get";//查询社团
    public static final String club_changeOwner="club/changeOwner";//改变社长
    public static final String club_findByAccount="club/findByAccount";//查询用户社团
    public static final String club_findById="club/findById";//查询社团
    public static final String club_search="club/search";//搜索社团
    public static final String club_focus="club/focus";//关注社团
    public static final String club_findMyFocus="club/findMyFocus";//查询我的社团关注
    public static final String club_unfocus="club/unfocus";//取消关注社团
    public static final String club_join="club/join";//加入社团
    public static final String club_quit="club/quit";//加入社团

    public static final String view_add = "view/add";//照片上传
    public static final String view_get = "view/get";//照片获取

    public static final String message_get = "message/messageSelect";//信息获取
    public static final String message_delete = "message/messageDelete";//删除
    public static final String message_edit = "message/messageEdit";//修改
    public static final String message_add = "message/messageAdd";//增加
}
