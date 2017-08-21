package administrator.entity;

import java.io.Serializable;
import java.sql.Date;

/**
 * Created by Administrator on 2017/8/17.
 * 供用户查看的数据单项
 */
public class DeviceData implements Serializable{

    private String value;

    private String receiveTime;

    public DeviceData(){}

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    @Override
    public String toString() {
        return "DeviceData{" +
                " value='" + value + '\'' +
                ", receiveTime=" + receiveTime +
                '}';
    }
}
