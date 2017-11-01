package administrator.enums;

public enum  AlertTypeEnum {
    TMP_LOW(1,"温度过低",""),
    TMP_HIGH(2,"温度过高",""),
    HUMIDITY_HIGH(3,"湿度过高",""),
    HUMIDITY_LOW(4,"湿度过低",""),
    GAS_LEAKS(5,"气体泄漏",""),
    MOVE_OVER_TIME(6,"移动时间过长",""),
    MOVE_OVER_DISTANCE(7,"移动距离过长","");

    private int index;
    //警告的标题
    private String title;
    //警告的内容
    private String content;

    /**
     * 根据序号查找对应的值
     * @param index
     * @return
     */
    public static AlertTypeEnum indexOf(int index) {
        for(AlertTypeEnum e :values()) {
            if(e.index == index) {
                return e;
            }
        }
        return null;
    }

    AlertTypeEnum(int index, String title, String content) {
        this.index = index;
        this.title = title;
        this.content = content;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
