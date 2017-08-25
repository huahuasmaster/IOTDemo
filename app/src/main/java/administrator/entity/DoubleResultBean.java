package administrator.entity;

/**
 * 返回包含两条不同数据的json 使用较少
 */
public class DoubleResultBean<T1,T2> {

    private boolean success;

    private T1 mainData;

    private T2 minorData;

    private String error;

    public DoubleResultBean(boolean success, T1 mainData, T2 minorData) {
        this.success = success;
        this.mainData = mainData;
        this.minorData = minorData;
    }

    public DoubleResultBean(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public DoubleResultBean() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T1 getMainData() {
        return mainData;
    }

    public void setMainData(T1 mainData) {
        this.mainData = mainData;
    }

    public T2 getMinorData() {
        return minorData;
    }

    public void setMinorData(T2 minorData) {
        this.minorData = minorData;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
