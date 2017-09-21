package administrator.entity;

import java.io.Serializable;

public class DeviceModelDto implements Serializable{

    private String name;

    private String remark;

    private long  hardwareVersion;

    private long softwareVersion;

    private short type;

    private short status;

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

    public long getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(long hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public long getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(long softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
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
