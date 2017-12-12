package administrator.entity;

/**
 * dec:
 * createBy yjzhao
 * createTime 16/5/14 11:08
 * 折线图适配entity
 */
public class DataEntity {
    private Long time;
    private Float mFloat;

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Float getFloat() {
        return mFloat;
    }

    public void setFloat(Float aFloat) {
        mFloat = aFloat;
    }

    @Override
    public String toString() {
        return "DataEntity{" +
                "time=" + time +
                ", mFloat=" + mFloat +
                '}';
    }
}
