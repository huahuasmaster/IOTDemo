package administrator.entity;

/**
 * 封装所有的json结果
 * @param <T>
 */
public class ResultBean<T> {

    //操作是否成功
    private boolean success;

    //实际数据
    private T data;

    //若失败 返回的错误信息
    private String error;

    public ResultBean(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public ResultBean(boolean success, T data) {

        this.success = success;
        this.data = data;
    }

    public ResultBean() {

    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
