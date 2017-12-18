package administrator.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/8/17.
 * 1.房间卡片点击后 进入的卡片展示页面 用于展示少量数据
 * 一个DeviceInArea类对应一张卡片
 * 一个设备可能对应多张卡片，以数据的类型数量为准
 * 2.点击卡片的“查看详情后进入的页面”，用于展示大量数据，同样只展示一种类型
 */
public class DeviceInArea implements Serializable {

    private long id;
    //设备名称
    private String deviceName;
    //设备sn
    private String Sn;
    //用户自定义名称
    private String otherName;
    //安装位置名称
    private String areaName;
    //所处状态
    private short status;
    //数据类型
    private int type;
    //阈值 max
    private double maxValue;
    //阈值 min
    private double minValue;

    private List<DeviceData> deviceDataList;

    public DeviceInArea() {
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<DeviceData> getDeviceDataList() {
        return deviceDataList;
    }

    public void setDeviceDataList(List<DeviceData> deviceDataList) {
        this.deviceDataList = deviceDataList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSn() {
        return Sn;
    }

    public void setSn(String sn) {
        Sn = sn;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    @Override
    public String toString() {
        return "DeviceInArea{" +
                "id=" + id +
                ", deviceName='" + deviceName + '\'' +
                ", Sn='" + Sn + '\'' +
                ", otherName='" + otherName + '\'' +
                ", areaName='" + areaName + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", deviceDataList=" + deviceDataList +
                '}';
    }
}
