package administrator.base;

import administrator.application.ContextApplication;
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
                temp = "";
                break;
            case TMP_CELSIUS:
                temp = "℃";
                break;
            case TMP_K:
                temp = "K";
                break;
            case SEC:
                temp = "S";
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
                content = "("+alertDto.getOtherName()+")湿度过高("
                        +alertDto.getAlertValue()+unit+")";break;
            case HUMIDITY_LOW:
                content = "("+alertDto.getOtherName()+")湿度过低("
                        +alertDto.getAlertValue()+unit+")";break;
            case MOVE_OVER_DISTANCE:
            case MOVE_OVER_TIME:
                content = "("+alertDto.getOtherName()+")被连续移动了"
                        +alertDto.getAlertValue()+unit;break;
            case DOOR_OPEN:
                content = " "+alertDto.getOtherName()+" 在离家期间被打开!";
            default:break;
        }
        return content;
    }

    public static String getIconName(AlertTypeEnum alertTypeEnum) {
        String iconName;
        switch (alertTypeEnum) {
            case TMP_HIGH:iconName = "ic_hot";break;
            case DOOR_OPEN:iconName = "ic_thief";break;
            default:iconName = "ic_warnning_circle";break;
        }
        return iconName;
    }
}
