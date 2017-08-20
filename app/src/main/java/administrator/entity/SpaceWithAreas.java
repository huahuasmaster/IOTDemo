package administrator.entity;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/14.
 */
public class SpaceWithAreas {

    private String spaceId;

    private String name;

    private int status;

    private int isDefault;

    private List<AreaDto> areaList;

    public SpaceWithAreas(){
        areaList = new ArrayList<>();
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public List<AreaDto> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<AreaDto> areaList) {
        this.areaList = areaList;
    }

    @Override
    public String toString() {
        return "SpaceWithAreas{" +
                "spaceId='" + spaceId + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", isDefault=" + isDefault +
                ", areaList=" + areaList +
                '}';
    }
}
