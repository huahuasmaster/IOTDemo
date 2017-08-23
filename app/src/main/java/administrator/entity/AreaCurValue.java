package administrator.entity;


import java.util.List;

/**
 * Created by Administrator on 2017/8/17.
 * 资源-预览-房间卡片
 * 房间卡片bean 内含设备预览信息
 */
public class AreaCurValue {

    private long areaId;

    private String name;

    private List<DeviceCurValue> deviceCurValueList;

    public AreaCurValue(){}

    public long getAreaId() {
        return areaId;
    }

    public void setAreaId(long areaId) {
        this.areaId = areaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DeviceCurValue> getDeviceCurValueList() {
        return deviceCurValueList;
    }

    public void setDeviceCurValueList(List<DeviceCurValue> deviceCurValueList) {
        this.deviceCurValueList = deviceCurValueList;
    }

    @Override
    public String toString() {
        return "AreaCurValue{" +
                "areaId=" + areaId +
                ", name='" + name + '\'' +
                ", deviceCurValueList=" + deviceCurValueList +
                '}';
    }
}
