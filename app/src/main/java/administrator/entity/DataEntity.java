package administrator.entity;

import android.support.annotation.NonNull;

/**
 * dec:
 * createBy yjzhao
 * createTime 16/5/14 11:08
 * 折线图适配entity
 */
public class DataEntity implements Comparable<DataEntity>{
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

    @Override
    public int compareTo(@NonNull DataEntity dataEntity) {
        if(time > dataEntity.getTime()) {
            return 1;
        }else {
            return -1;
        }
    }
}
