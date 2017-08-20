package administrator.enums;

/**
 * Created by zhuang_ge on 2017/8/9.
 */

public enum MsgTypeEnum {
    DEVICE_OFF_LINE((short)1,"设备离线"),
    DEVICE_OUT_OF_BATTARY((short)2,"设备电源耗尽"),
    WARNNING((short)3,"警告"),
    COMMON((short)0,"")
    ;


    private short index;

    private String text;

    MsgTypeEnum(short index, String text) {
        this.index = index;
        this.text = text;
    }

    public short getIndex() {
        return index;
    }

    public void setIndex(short index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
