package administrator.entity;

import java.util.Date;

/**
 * Created by Administrator on 2017/8/14.
 * 房间信息
 */
public class AreaDto{

    private long id;

    private long spaceId;

    private String name;

    private String createTime;

    private long creatorId;

    private short status;

    public AreaDto(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(long spaceId) {
        this.spaceId = spaceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "AreaDto{" +
                "id=" + id +
                ", spaceId=" + spaceId +
                ", name='" + name + '\'' +
                ", createTime='" + createTime + '\'' +
                ", creatorId=" + creatorId +
                ", status=" + status +
                '}';
    }
}
