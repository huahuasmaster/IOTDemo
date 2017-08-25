package administrator.base.http;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import administrator.application.ContextApplication;
import administrator.entity.ResultBean;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/7/14.
 * 在此处统一处理请求是否成功
 */

public class HttpUtil {
    static RequestBody requestBody;
    static Response response;

    /**
     * 本方法可传入回调函数
     * 用于无参数的got请求
     * 无需定义新线程，但是请注意在回调函数中使用RunOnUiThread避免出错
     * 可自选是否需要完整的数据 后者经过处理后的数据
     * @param address
     * @param listener
     */
    public static void sendRequestWithCallback(final String address,
                                               final HttpCallbackListener listener,
                                               final boolean wholeData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(address).build();
                try {
                    String result = getResponse(request);
                    if(listener != null) {
                        if(wholeData) {
                            listener.onFinish(result);
                        } else {
                            //不需要完整的数据 则进行处理
                            JSONObject jsonObject = new JSONObject(result);//转换为json
                            boolean success = jsonObject.getBoolean("success");
                            if(success) {
                                listener.onFinish(jsonObject.getString("data"));
                            } else {
                                //如果是服务器处理好的错误 则直接显示
                                Toast.makeText(ContextApplication.getContext(),
                                        jsonObject.getString("error"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (IOException | JSONException e ) {
                    if(listener != null) {
                        listener.onError(e);
                    }
                }
            }
        }).start();

    }

    public static void sendRequestWithCallback(final String address,
                                               final HttpCallbackListener listener) {
        sendRequestWithCallback(address,listener,false);
    }
    /**
     * 本方法可传入回调函数
     * 用于带参数的post请求，请事先准备好RequestBody
     * 无需定义新线程，但是请注意在回调函数中使用RunOnUiThread避免出错
     * @param address
     * @param listener
     */
    public static void sendRequestWithCallback(final String address,
                                               final RequestBody requestBody,
                                               final HttpCallbackListener listener,
                                               final boolean wholeData
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
                        if(wholeData) {
                            listener.onFinish(result);
                        } else {
                            //不需要完整的数据 则进行处理
                            JSONObject jsonObject = new JSONObject(result);//转换为json
                            boolean success = jsonObject.getBoolean("success");
                            if(success) {
                                listener.onFinish(jsonObject.getString("data"));
                            } else {
                                //如果是服务器处理好的错误 则直接显示
                                Toast.makeText(ContextApplication.getContext(),
                                        jsonObject.getString("error"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (IOException | JSONException e ) {
                    if(listener != null) {
                        listener.onError(e);
                    }
                }
            }
        }).start();

    }

    public static void sendRequestWithCallback(final String address,
                                               final RequestBody requestBody,
                                               final HttpCallbackListener listener) {
        sendRequestWithCallback(address,requestBody,listener,false);
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
