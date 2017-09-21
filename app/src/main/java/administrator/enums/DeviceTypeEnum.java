package administrator.enums;

/**
 * Created by thinkpad on 2017/8/10.
 */
public enum DeviceTypeEnum {
    GATEWAY(0,"网关"),
    NODE(1,"结点"),
    REPEAT(3,"中继器");
    int status;
    String type;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    DeviceTypeEnum(int status, String type) {

        this.status = status;
        this.type = type;
    }
    public static DeviceTypeEnum stateOf (int index){
        for(DeviceTypeEnum state:values()){
            if(state.getStatus() == index){
                return state;
            }
        }
        return null;
    }
}
