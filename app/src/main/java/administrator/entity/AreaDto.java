package administrator.entity;

/**
 * Created by Administrator on 2017/8/14.
 */
public class AreaDto{

    private int id;

    private int spaceId;

    private String name;

    private String creatTime;

    private int creatId;

    private int status;

    public AreaDto(){}

    public AreaDto(int id, int spaceId, String name, String creatTime, int creatId, int status){
        this.id = id;
        this.spaceId = spaceId;
        this.name = name;
        this.creatTime = creatTime;
        this.creatId = creatId;
        this.status = status;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public int getCreatId() {
        return creatId;
    }

    public void setCreatId(int creatId) {
        this.creatId = creatId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AreaDto{" +
                "id=" + id +
                ", spaceId=" + spaceId +
                ", name='" + name + '\'' +
                ", creatTime='" + creatTime + '\'' +
                ", creatId=" + creatId +
                ", status=" + status +
                '}';
    }
}
