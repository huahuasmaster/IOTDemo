package administrator.entity;


import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/16.
 */
public class DeviceDto implements Serializable{

    private long id;

    private DeviceModelDto deviceModelDto;

    private DeviceRegistDto deviceRegistDto;

    private String areaName;

    private String sn;

    private String createTime;

    private long creatorId;

    private String setupTime;

    private short status;

    public DeviceDto(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public String getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(String setupTime) {
        this.setupTime = setupTime;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public DeviceModelDto getDeviceModelDto() {
        return deviceModelDto;
    }

    public void setDeviceModelDto(DeviceModelDto deviceModelDto) {
        this.deviceModelDto = deviceModelDto;
    }

    public DeviceRegistDto getDeviceRegistDto() {
        return deviceRegistDto;
    }

    public void setDeviceRegistDto(DeviceRegistDto deviceRegistDto) {
        this.deviceRegistDto = deviceRegistDto;
    }

    @Override
    public String toString() {
        return "DeviceDto{" +
                "id=" + id +
                ", deviceModelDto=" + deviceModelDto +
                ", deviceRegistDto=" + deviceRegistDto +
                ", areaName='" + areaName + '\'' +
                ", sn='" + sn + '\'' +
                ", createTime='" + createTime + '\'' +
                ", creatorId=" + creatorId +
                ", setupTime='" + setupTime + '\'' +
                ", status=" + status +
                '}';
    }
}
