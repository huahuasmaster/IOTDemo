package administrator.entity;

/**
 * Created by zhuang_ge on 2017/8/8.
 * 智能家居：房间
 */

public class Room {
    private int id;
    private String name;
    private Space space;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public Room(int id, String name, Space space) {

        this.id = id;
        this.name = name;
        this.space = space;
    }

    public Room() {

    }
}
