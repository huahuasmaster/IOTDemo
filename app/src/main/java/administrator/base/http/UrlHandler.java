package administrator.base.http;

/**
 * 记录、生成请求的辅助类
 * 所有网络请求地址在此处维护
 * Created by zhuang_ge on 2017/8/10.
 */

public class UrlHandler {

    public static String ip = "192.168.0.108";

    public static String port = "8088";

    public static int userId = 7;

    //登录链接 需填充表单数据
    public static String getLoginUrl() {
        return getHead()+"user/login";
    }

    //获取空间卡片列表链接
    public static String getSpaceWithAreasListUrl() {
        return getHead()+"space/"+userId+"/withAreas";
    }

    //获取房间卡片列表链接
    public static String getAreaWithInnerDevicePreviewUrl(int spaceId) {
        return getHead()+"space/"+spaceId+"/areaList/preview";
    }

    //获取附带数据list的设备信息
    public static String getDeviceWithDataUrl(int deviceId,int type,int offset) {
        return getHead()+"device/"+deviceId+"/"+type+"/"+offset;
    }

    //获取设备的详细信息
    public static String getDeviceDetailDesc(int deviceId) {
        return getHead()+"device/"+deviceId;
    }

    //获取设备所处空间的所有安装位置
    public static String getAreaListByDeviceId(int deviceId) {
        return getHead()+"area/"+deviceId+"/byDevice";
    }

    //获取安装位置里所有设备少量数据
    public static String getDeviceInAreaListByAreaId(int areaId,int offset) {
        return getHead()+"area/"+areaId+"/"+offset+"/innerDevice";
    }

    /**
     * 获取请求的开头ip与端口
     * @return
     */
    public static String getHead() {
        return "http://"+ip+":"+port+"/";
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        UrlHandler.ip = ip;
    }

    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        UrlHandler.port = port;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        UrlHandler.userId = userId;
    }
}
