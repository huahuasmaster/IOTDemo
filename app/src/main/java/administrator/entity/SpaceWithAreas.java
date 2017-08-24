package administrator.entity;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/14.
 * 空间以及空间内房间列表
 */
public class SpaceWithAreas {

    private long id;

    private String name;

    //空间状态 停用、正常……
    private short status;

    //空间模式 离家、归家……
    private  short modelType;

    private short isDefault;

    private List<AreaDto> areaList;

    public SpaceWithAreas(){
        areaList = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public short getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(short isDefault) {
        this.isDefault = isDefault;
    }

    public List<AreaDto> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<AreaDto> areaList) {
        this.areaList = areaList;
    }

    public short getModelType() {
        return modelType;
    }

    public void setModelType(short modelType) {
        this.modelType = modelType;
    }

    @Override
    public String toString() {
        return "SpaceWithAreas{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", modelType=" + modelType +
                ", isDefault=" + isDefault +
                ", areaList=" + areaList +
                '}';
    }
}
