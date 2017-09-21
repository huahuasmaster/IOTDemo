package administrator.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/8/22.
 */
public class DeviceRegistDto implements Serializable{

    private long id;

    private String remark;

    private String setTime;

    private long appRegistId;

    private long deviceId;

    private long setterId;

    private short status;

    public DeviceRegistDto(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSetTime() {
        return setTime;
    }

    public void setSetTime(String setTime) {
        this.setTime = setTime;
    }

    public long getAppRegistId() {
        return appRegistId;
    }

    public void setAppRegistId(long appRegistId) {
        this.appRegistId = appRegistId;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public long getSetterId() {
        return setterId;
    }

    public void setSetterId(long setterId) {
        this.setterId = setterId;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DeviceRegistDto{" +
                "id=" + id +
                ", remark='" + remark + '\'' +
                ", setTime='" + setTime + '\'' +
                ", appRegistId=" + appRegistId +
                ", deviceId=" + deviceId +
                ", setterId=" + setterId +
                ", status=" + status +
                '}';
    }
}
