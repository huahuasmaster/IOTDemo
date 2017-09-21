package administrator.entity;

/**
 * Created by Administrator on 2017/8/17.
 * 资源-预览-房间卡片-设备预览
 */
public class DeviceCurValue {

    private long deviceId;

    private int type;

    private String curValue;

    private String sn;

    public DeviceCurValue(){}

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getCurValue() {
        return curValue;
    }

    public void setCurValue(String curValue) {
        this.curValue = curValue;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    @Override
    public String toString() {
        return "DeviceCurValue{" +
                "deviceId=" + deviceId +
                ", type=" + type +
                ", curValue='" + curValue + '\'' +
                ", Sn='" + sn + '\'' +
                '}';
    }
}
