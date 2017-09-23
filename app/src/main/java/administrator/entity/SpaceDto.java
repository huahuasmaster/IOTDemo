package administrator.entity;

/**
 * Created by Administrator on 2017/8/16.
 */
public class SpaceDto {

    private long id;

    private String name;

    private long locationId;

    private long userId;

    private short isDefault;

    private short modelTypa;

    private long creatorId;

    private String creatTime;

    private short status;

    public SpaceDto(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public short getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(short isDefault) {
        this.isDefault = isDefault;
    }

    public short getModelTypa() {
        return modelTypa;
    }

    public void setModelTypa(short modelTypa) {
        this.modelTypa = modelTypa;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SpaceDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", locationId=" + locationId +
                ", userId=" + userId +
                ", isDefault=" + isDefault +
                ", modelTypa=" + modelTypa +
                ", creatorId=" + creatorId +
                ", creatTime='" + creatTime + '\'' +
                ", status=" + status +
                '}';
    }
}
