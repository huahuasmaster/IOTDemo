package administrator.entity;

/**
 * Created by Administrator on 2017/8/16.
 * 设备设置页面
 * 设备的详细信息
 */
public class DeviceDto {

    private int id;

    private DeviceModelDto deviceModelDto;

    private String areaName;

    private String sn;

    private String createTime;

    private int creatorId;

    private String setupTime;

    private int status;

    public DeviceDto(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public String getSetupTime() {
        return setupTime;
    }

    public void setSetupTime(String setupTime) {
        this.setupTime = setupTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
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

    @Override
    public String toString() {
        return "DeviceDto{" +
                "id=" + id +
                ", deviceModelDto=" + deviceModelDto +
                ", areaName='" + areaName + '\'' +
                ", sn='" + sn + '\'' +
                ", createTime='" + createTime + '\'' +
                ", creatorId=" + creatorId +
                ", setupTime='" + setupTime + '\'' +
                ", status=" + status +
                '}';
    }
}
