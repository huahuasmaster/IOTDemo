package administrator.entity;

/**
 * Created by Administrator on 2017/8/17.
 * 资源-预览-房间卡片-设备预览
 */
public class DeviceCurValue {

    private int deviceId;

    private int type;

    private String curValue;

    public DeviceCurValue(){}

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getCurValue() {
        return curValue;
    }

    public void setCurValue(String curValue) {
        this.curValue = curValue;
    }

    @Override
    public String toString() {
        return "DeviceCurValue{" +
                "deviceId=" + deviceId +
                ", curValue='" + curValue + '\'' +
                '}';
    }
}