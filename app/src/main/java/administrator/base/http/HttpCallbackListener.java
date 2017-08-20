package administrator.base.http;

/**
 * Created by Administrator on 2017/7/14.
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
