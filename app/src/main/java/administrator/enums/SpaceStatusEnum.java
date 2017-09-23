package administrator.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 空间状态：1->离家   2->回家
 * Created by zhuang_ge on 2017/8/4.
 */

public enum SpaceStatusEnum {
    LEAVE_HOME((short) 1,"离家"),
    AT_HOME((short)0,"在家")
    ;

    private short index;
    private String text;

    SpaceStatusEnum(short index, String text) {
        this.index = index;
        this.text = text;
    }

    public static List<Map<String, Object>> all() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SpaceStatusEnum e : values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("index", e.getIndex());
            map.put("text", e.getText());
            list.add(map);
        }
        return list;
    }

    public static SpaceStatusEnum indexOf(short index) {
        for (SpaceStatusEnum e : values()) {
            if (e.getIndex() == index) {
                return e;
            }
        }
        return null;
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
