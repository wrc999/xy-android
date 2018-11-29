package e.administrator.xy.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jiguang.imui.commons.models.IMessage;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.PromptContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import e.administrator.xy.pojo.DefaultUser;
import e.administrator.xy.pojo.MyMessage;

/**
 * Created by Administrator on 2018/10/25.
 */

public class Message2MyMessage {

    //将聊天记录包装成MyMessage
    public static List<MyMessage> list(List<Message> messageList){
        List<MyMessage> myMessageList = new ArrayList<MyMessage>();
        final UserInfo mine = JMessageClient.getMyInfo();
        for (Message m:messageList) {
            String info = null;
            int type = 0;
            if (m.getContentType().toString().equals("eventNotification")) {
                info = m.getFromUser().getUserName()+":欢迎"+((EventNotificationContent)m.getContent()).getUserDisplayNames()+"加入群组";
                type = IMessage.MessageType.EVENT.ordinal();
            }
            if (m.getFromUser().getUserName().equals(mine.getUserName()) && m.getContentType().toString().equals("text")) {
                info = ((TextContent) m.getContent()).getText();
                type = IMessage.MessageType.SEND_TEXT.ordinal();
            }
            if (!m.getFromUser().getUserName().equals(mine.getUserName()) && m.getContentType().toString().equals("text")) {
                info = ((TextContent) m.getContent()).getText();
                type = IMessage.MessageType.RECEIVE_TEXT.ordinal();
            }
            if (m.getFromUser().getUserName().equals(mine.getUserName()) && m.getContentType().toString().equals("image")) {
                info = ((ImageContent) m.getContent()).getLocalThumbnailPath();
                type = IMessage.MessageType.SEND_IMAGE.ordinal();
            }
            if (!m.getFromUser().getUserName().equals(mine.getUserName()) && m.getContentType().toString().equals("image")) {
                info = ((ImageContent) m.getContent()).getLocalThumbnailPath();
                type = IMessage.MessageType.RECEIVE_IMAGE.ordinal();
            }
            MyMessage message = new MyMessage(info, type);
            message.setMediaFilePath(info);
            //除了系统消息都需要添加发布消息的用户
            if (!m.getContentType().toString().equals("eventNotification")){
                message.setUserInfo(new DefaultUser(m.getFromUser().getUserName(), m.getFromUser().getNickname(), m.getFromUser().getAvatarFile().getPath().toString()));
            }
            message.setMessageStatus(IMessage.MessageStatus.SEND_SUCCEED);
            try {
                message.setTimeString(DateUtil.getSelectDateTime(new Date(m.getCreateTime())));
            } catch (Exception e) {
                e.printStackTrace();
            }
            myMessageList.add(message);
        }
        return myMessageList;
    }

}
