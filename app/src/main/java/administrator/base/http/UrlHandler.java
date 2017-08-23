package administrator.base.http;

import android.content.Context;
import android.content.SharedPreferences;

import administrator.application.ContextApplication;

/**
 * 记录、生成请求的辅助类
 * 所有网络请求地址在此处维护
 * Created by zhuang_ge on 2017/8/10.
 */

public class UrlHandler {


    public static String port = "8088";


    public static SharedPreferences loginSp = ContextApplication
            .getContext()
            .getSharedPreferences("login_data", Context.MODE_PRIVATE);

    //登录链接 需填充表单数据
    public static String getLoginUrl() {
        return getHead()+"user/login";
    }

    //获取空间卡片列表链接
    public static String getSpaceWithAreasListUrl() {
        return getHead()+"space/"+getUserId()+"/withAreas";
    }

    //获取房间卡片列表链接
    public static String getAreaWithInnerDevicePreviewUrl(long spaceId) {
        return getHead()+"space/"+spaceId+"/areaList/preview";
    }

    //获取附带数据list的设备信息
    public static String getDeviceWithDataUrl(long deviceId,int type,int offset) {
        return getHead()+"device/"+deviceId+"/"+type+"/"+offset;
    }

    //获取设备的详细信息
    public static String getDeviceDetailDesc(long deviceId) {
        return getHead()+"device/"+deviceId;
    }

    //获取设备所处空间的所有安装位置
    public static String getAreaListByDeviceId(long deviceId) {
        return getHead()+"area/"+deviceId+"/byDevice";
    }

    //获取安装位置里所有设备少量数据
    public static String getDeviceInAreaListByAreaId(long areaId,int offset) {
        return getHead()+"area/"+areaId+"/"+offset+"/innerDevice";
    }

    //获取默认空间下的房间以及内里设备信息
    public static String getAreaWithDeviceOfDefaultSpace(long userId) {
        return getHead()+"space/"+userId+"/areaList/preview/default";
    }
    /**
     * 获取请求的开头ip与端口
     * @return
     */
    public static String getHead() {
        return "http://"+getIp()+":"+port+"/";
    }
    //获取服务器ip
    public static String getIp() {
        return loginSp.getString("ip","192.168.0.101");
    }
    //设置服务器ip
    public static void setIp(String ip) {
        loginSp.edit().putString("ip",ip).apply();
    }

    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        UrlHandler.port = port;
    }

    public static long getUserId() {
        return loginSp.getLong("user_id",-1L);
    }

    public static void setUserId(long userId) {
        loginSp.edit().putLong("user_id",userId).apply();
    }
}
