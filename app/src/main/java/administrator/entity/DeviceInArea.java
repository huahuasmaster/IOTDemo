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
public class DeviceInArea implements Serializable{
    //设备名称
    private String deviceName;
    //用户自定义名称
    private String otherName;
    //安装位置名称
    private String areaName;
    //所处状态
    private int status;
    //数据类型
    private int type;

    private List<DeviceData> deviceDataList;

    public DeviceInArea(){}

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
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

    @Override
    public String toString() {
        return "DeviceInArea{" +
                "deviceName='" + deviceName + '\'' +
                ", otherName='" + otherName + '\'' +
                ", areaName='" + areaName + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", deviceDataList=" + deviceDataList +
                '}';
    }
}
