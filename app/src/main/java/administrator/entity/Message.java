package administrator.entity;

import java.util.Date;

/**
 * Created by zhuang_ge on 2017/8/9.
 */

public class Message {

    private int id;

    private String content;

    private short type;

    private Date sendDate;

    private boolean isChecked;

    private int recipientId;

    public Message() {
    }

    public Message(int id, String content, short type, Date sendDate, boolean isChecked, int recipientId) {
        this.id = id;
        this.content = content;
        this.type = type;
        this.sendDate = sendDate;
        this.isChecked = isChecked;
        this.recipientId = recipientId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }
}
