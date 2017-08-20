package administrator.base.http;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/14.
 */

public class HttpUtil {
    static RequestBody requestBody;
    static Response response;

    /**
     * 本方法可传入回调函数
     * 用于无参数的got请求
     * 无需定义新线程，但是请注意在回调函数中使用RunOnUiThread避免出错
     * @param address
     * @param listener
     */
    public static void sendRequestWithCallback(final String address,
                                               final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(address).build();
                try {
                    String result = getResponse(request);
                    if(listener != null) {
                        listener.onFinish(result);
                    }
                } catch (IOException e) {
                    if(listener != null) {
                        listener.onError(e);
                    }
                }
            }
        }).start();

    }

    /**
     * 本方法可传入回调函数
     * 用于带参数的post请求，请事先准备好RequestBody
     * 无需定义新线程，但是请注意在回调函数中使用RunOnUiThread避免出错
     * @param address
     * @param listener
     */
    public static void sendRequestWithCallback(final String address, final RequestBody requestBody,
                                               final HttpCallbackListener listener
                                               ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(address)
                        .post(requestBody)
                        .build();
                try {
                    String result = getResponse(request);
                    if(listener != null) {
                        listener.onFinish(result);
                    }
                } catch (IOException e) {
                    if(listener != null) {
                        listener.onError(e);
                    }
                }
            }
        }).start();

    }

    /**
     * 此函数用于got请求，并直接返回String
     * 请在新线程中调用本方法，并对抛出的io错误进行处理
     * @param address
     * @return
     * @throws IOException
     */
    public static String sendRequest(String address) throws IOException {
        Request request = new Request.Builder()
                .url(address)
                .build();
        return getResponse(request);
    }
    /**
     * 此函数用于带参数的post请求，并直接返回String
     * 请事先准备好RequestBody
     * 请在新线程中调用本方法，并对抛出的io错误进行处理
     * @param address
     * @return
     * @throws IOException
     */
    public static String sendRequest(String address, RequestBody requestbody) throws IOException {
        Request request = new Request.Builder()
                .url(address)
                .post(requestbody)
                .build();
        return getResponse(request);
    }

    /**
     * 实际执行response的方法
     * @param request
     * @return
     * @throws IOException
     */
    private static String getResponse(Request request) throws IOException {
        OkHttpClient client = new OkHttpClient();
        response = client.newCall(request).execute();
        return response.body().string();
    }
}
