package com.ben.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: Message
 * @author: benjamin
 * @version: 1.0
 * @description: 用户消息表
 * @createTime: 2019/08/20/10:59
 */

public class Message implements Serializable {
    private int id;
    private int fromId;
    private int toId;
    private String content;
    private Date createdDate;
    private int hasRead;
    private String conversationId; // 从A到B的消息和B到A的消息，只存一份，需要将get方法进行重写

    // 注意这里的id用来存放会话次数，因此要重写get和set方法，记住了。
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getHasRead() {
        return hasRead;
    }

    public void setHasRead(int hasRead) {
        this.hasRead = hasRead;
    }

    public String getConversationId() {
        if (fromId < toId) {
            return String.format("%d_%d", fromId, toId);
        }
        return String.format("%d_%d", toId, fromId);
    }
    public void setConversationId(){
        this.conversationId = conversationId;
    }

    // 这里可以注释掉，当时测试从数据库查到的数据，打印到控制台上
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", content='" + content + '\'' +
                ", createdDate=" + createdDate +
                ", hasRead=" + hasRead +
                ", conversationId='" + conversationId + '\'' +
                '}';
    }
}
