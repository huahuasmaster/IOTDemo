package administrator.entity;

import java.io.Serializable;

/**
 * 设备设置页面
 * 设备的真实名字，固件版本等信息
 */

public class DeviceModelDto implements Serializable{

    private String name;

    private String remark;

    private int  hardwareVersion;

    private int softwareVersion;

    private int type;

    private int status;

    public DeviceModelDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(int hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public int getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(int softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DeviceModelDto{" +
                "name='" + name + '\'' +
                ", remark='" + remark + '\'' +
                ", hardwareVersion=" + hardwareVersion +
                ", softwareVersion=" + softwareVersion +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}
