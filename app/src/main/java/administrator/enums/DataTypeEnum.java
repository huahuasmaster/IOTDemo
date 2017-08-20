package administrator.enums;

/**
 * 设备数据类型的枚举（温度、湿度、门开关....）
 */
public enum DataTypeEnum {
    TMP_CELSIUS(1,"摄氏温度","TMP1"),
    TMP_K(2,"开氏温度","TMP2"),
    HUMIDITY(3,"湿度","HUM"),
    POS_GPS(4,"GPS位置","POS1"),
    POS_BEIDOU(5,"北斗位置","POS2"),
    DOOR_OPEN_CLOSE(6,"门开关","DOOR");

    private int index;

    private String type;

    private String code;

    /**
     * 根据序号查找对应的值
     * @param index
     * @return
     */
    public static DataTypeEnum indexOf(int index) {
        for(DataTypeEnum e :values()) {
            if(e.index == index) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据编码code查找对应的类型
     * @param code
     * @return
     */
    public static DataTypeEnum findByCode(String code) {
        for(DataTypeEnum e : values()) {
            if(e.getCode().equals(code)){
                return e;
            }
        }
        return null;
    }

    DataTypeEnum(int index, String type, String code) {
        this.index = index;
        this.type = type;
        this.code = code;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
