package administrator.entity;

import java.util.Date;

/**
 * Created by Administrator on 2017/8/2.
 * 智能家居：空间
 */

public class Space {

    private int id;//空间主键

    private String name;//名称

    private String location;//空间所在位置

    private int userId;//创建者id

    private Date createDate;//创建时间

    private short status;//空间的状态：离家、回家……

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public Space() {
    }

    public Space(int id, String name, String location, int userId, Date createDate) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.userId = userId;
        this.createDate = createDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
