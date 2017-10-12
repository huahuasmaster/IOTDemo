package administrator.entity;

public class AlertDto {

    private long id;

    private long deviceId;

    private String otherName;

    private long alertConfigId;
    //数据类型
    private int dataType;

    private String alertValue;

    private String alertTime;

    private String readTime;

    private String processTime;
    //告警种类
    private short alertType;

    private short status;

    public AlertDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public long getAlertConfigId() {
        return alertConfigId;
    }

    public void setAlertConfigId(long alertConfigId) {
        this.alertConfigId = alertConfigId;
    }

    public String getAlertValue() {
        return alertValue;
    }

    public void setAlertValue(String alertValue) {
        this.alertValue = alertValue;
    }

    public String getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(String alertTime) {
        this.alertTime = alertTime;
    }

    public String getReadTime() {
        return readTime;
    }

    public void setReadTime(String readTime) {
        this.readTime = readTime;
    }

    public String getProcessTime() {
        return processTime;
    }

    public void setProcessTime(String processTime) {
        this.processTime = processTime;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public short getAlertType() {
        return alertType;
    }

    public void setAlertType(short alertType) {
        this.alertType = alertType;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    @Override
    public String toString() {
        return "AlertDto{" +
                "id=" + id +
                ", deviceId=" + deviceId +
                ", otherName='" + otherName + '\'' +
                ", alertConfigId=" + alertConfigId +
                ", dataType=" + dataType +
                ", alertValue='" + alertValue + '\'' +
                ", alertTime='" + alertTime + '\'' +
                ", readTime='" + readTime + '\'' +
                ", processTime='" + processTime + '\'' +
                ", alertType=" + alertType +
                ", status=" + status +
                '}';
    }
}
