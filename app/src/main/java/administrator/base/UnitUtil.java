package administrator.base;

import administrator.enums.DataTypeEnum;

/**
 * Created by zhuang_ge on 2017/10/13.
 * 处理数据单位
 */

public class UnitUtil {
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
}
