package administrator.entity;

import java.util.Date;

/**
 * Created by zhuang_ge on 2017/8/6.
 */

public class UniversalData<T> {
    private Date date;
    private T data;

    public UniversalData(Date date, T data) {
        this.date = date;
        this.data = data;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
