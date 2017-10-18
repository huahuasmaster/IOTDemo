package administrator.base;

import administrator.entity.AlertDto;
import administrator.enums.AlertTypeEnum;
import administrator.enums.DataTypeEnum;

/**
 * Created by zhuang_ge on 2017/10/13.
 * 处理数据单位
 */

public class AlertToMsgUtil {
    public static String getUnit(DataTypeEnum dataTypeEnum) {
        String temp = "";
        switch (dataTypeEnum) {
            case HUMIDITY:
                temp = "%";
                break;
            case TMP_CELSIUS:
                temp = "℃";
                break;
            case TMP_K:
                temp = "K";
                break;
        }
        return temp;
    }

    public static String getContent(AlertTypeEnum alertType, AlertDto alertDto,String unit) {
        String content = "";
        switch (alertType) {
            case TMP_HIGH:
                content = "("+alertDto.getOtherName()+")温度过高("
                        +alertDto.getAlertValue()+unit+")，请通风。";break;
            case TMP_LOW:
                content = "("+alertDto.getOtherName()+")温度过低("
                        +alertDto.getAlertValue()+unit+")，请注意保暖。";break;
            case HUMIDITY_HIGH:
                content = "("+alertDto.getOtherName()+")湿度过低("
                        +alertDto.getAlertValue()+unit+")";break;
            case HUMIDITY_LOW:
                content = "("+alertDto.getOtherName()+")湿度过高("
                        +alertDto.getAlertValue()+unit+")";break;
        }
        return content;
    }
}
