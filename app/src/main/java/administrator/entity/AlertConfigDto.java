package administrator.entity;

import java.util.Date;

public class AlertConfigDto {
    private long id;

    private long deviceConfigId;

    private String singleCode;

    private double minValue;

    private double maxValue;

    private short alertStatus;

    private short type;

    private String configTime;

    private long configerId;

    private short status;

    public AlertConfigDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDeviceConfigId() {
        return deviceConfigId;
    }

    public void setDeviceConfigId(long deviceConfigId) {
        this.deviceConfigId = deviceConfigId;
    }

    public String getSingleCode() {
        return singleCode;
    }

    public void setSingleCode(String singleCode) {
        this.singleCode = singleCode;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public short getAlertStatus() {
        return alertStatus;
    }

    public void setAlertStatus(short alertStatus) {
        this.alertStatus = alertStatus;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public String getConfigTime() {
        return configTime;
    }

    public void setConfigTime(String configTime) {
        this.configTime = configTime;
    }

    public long getConfigerId() {
        return configerId;
    }

    public void setConfigerId(long configerId) {
        this.configerId = configerId;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }
}
