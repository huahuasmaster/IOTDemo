package administrator.base.http;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

import administrator.application.ContextApplication;

/**
 * 记录、生成请求的辅助类
 * 所有网络请求地址在此处维护
 * Created by zhuang_ge on 2017/8/10.
 */

public class UrlHandler {


    private static String port = "8088";


    private static SharedPreferences loginSp = ContextApplication
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

    //修改设备的自定义名称
    public static String editDeviceName(long deviceId,String name) {
        return getHead()+"device/"+deviceId+"/"+name+"/editName";
    }

    //根据获取设备信息
    public static String getDeviceDetailDescBySn(String sn) {
        return getHead()+"device/"+sn+"/bySn";
    }

    //获取设备所处空间的所有安装位置
    public static String getAreaListByDeviceId(long deviceId) {
        return getHead()+"area/"+deviceId+"/byDevice";
    }

    //获取安装位置里所有设备少量数据
    public static String getDeviceInAreaListByAreaId(long areaId,int offset) {
        return getHead()+"area/"+areaId+"/"+offset+"/innerDevice";
    }

    //告知服务器设备上线
    public static String deviceOnline(String sn) {
        return getHead()+"device/"+getUserId()+"/"+sn+"/online";
    }

    //修改设备绑定的安装位置
    public static String changeDeviceOfArea(long deviceId,long areaId) {
        return getHead()+"device/"+deviceId+"/"+areaId+"/changeArea";
    }

    //新增安装位置同时绑定
    public static String addAreaAndBoundIt(long deviceId,String name) {
        return getHead()+"device/"+deviceId+"/"+name+"/boundDevice";
    }
    //获取默认空间下的房间以及内里设备信息
    public static String getAreaWithDeviceOfDefaultSpace(long userId) {
        return getHead()+"space/"+userId+"/areaList/preview/default";
    }

    //获取默认空间下的房间列表
    public static String getAreaOfDefaultSpace() {
        return getHead()+"area/"+getUserId()+"/allAreaWithDefault";
    }

    //删除房间
    public static String deleteArea(long areaId) {
        return getHead()+"area/"+areaId+"/deleteArea";
    }

    //新增房间
    public static String addArea(String name) {
        return getHead()+"area/"+getUserId()+"/"+name+"/addArea";
    }

    //编辑房间（名称）
    public static String editArea(long areaId,String newName) {
        return getHead()+"area/"+areaId+"/"+newName+"/editArea";
    }

    //获取当前空间下的网关
    public static String getCurrentGate() {
        return getHead()+"space/"+getUserId()+"/getDefaultWsn";
    }

    //获取用户的所有空间列表
    public static String getAllSpace() {
        return getHead()+"space/"+getUserId()+"/allSpace";
    }

    //增加空间
    public static String addSpace(String spaceName) {
        return getHead()+"space/"+getUserId()+"/"+spaceName+"/addSpace";
    }

    //删除空间
    public static String deleteSpace(long spaceId) {
        return getHead()+"space/"+spaceId+"/deleteSpace";
    }

    //编辑空间（名称）
    public static String editSpace(long spaceId,String newName) {
        return getHead()+"space/"+spaceId+"/"+newName+"/editSpace";
    }

    //修改空间的状态 离家/归家
    public static String postChangeModelTypeBySpaceId(long spaceId) {
        return getHead()+"space/"+spaceId+"/changeModel";
    }

    //获取设备code 设备sn键值对
    public static String getSnCodeMap() {
        return getHead()+"device/"+getUserId()+"/SnCodeMap";
    }

    //新增/更新阈值设置
    public static String addAlertConfig(long deviceId,String code) {
        return getHead()+"alert/"+deviceId+"/"+code+"/setThreshold";
    }

    //获取阈值
    public static String getAlertConfig(long deviceId,String code) {
        return getHead()+"alert/"+deviceId+"/"+code+"/threshold";
    }

    public static String getAlert(long spaceId) {
        return getHead()+"alert/"+spaceId+"/alert";
    }

    public static String getAlertByDefault() {
        return getHead()+"alert/"+getUserId()+"/alertByDefault";
    }

    //标注alert为已读
    public static String readAlert(long alertId,long date) {
        return getHead()+"alert/"+alertId+"/"+date+"/read";
    }

    //标注alert为已确认
    public static String processAlert(long alertId,long date) {
        return getHead()+"alert/"+alertId+"/"+date+"/process";

    }

    //批量确认alert
    public static String processAlertByDeviceIdAndDataType(long deviceId,int dataType,long date) {
        return getHead()+"alert/"+deviceId+"/"+dataType+"/"+date+"/processByDeviceAndDataType";
    }

    //获取某个设备的alert列表
    public static String getAlertByDeviceId(long deviceId,int dataType,int offset) {
        return getHead()+"alert/"+deviceId+"/"+dataType+"/"+offset+"/getList";
    }

    //获取萤石云accessToken接口
    public static String getVideoAccessToken() {
        return "https://open.ys7.com/api/lapp/token/get";
    }
    /**
     * 获取
     * @return 请求的开头ip与端口
     */
    private static String getHead() {
        return "http://"+getIp()+":"+port+"/app/";
    }
    //获取服务器ip
    public static String getIp() {
        return loginSp.getString("ip","121.40.140.223");
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

    public static String getToken(){
        return loginSp.getString("token","");
    }

    public static void setToken(String token){
        loginSp.edit().putString("token",token).apply();
    }

    public static long getUserId() {
        return loginSp.getLong("user_id",-1L);
    }

    public static void setUserId(long userId) {
        loginSp.edit().putLong("user_id",userId).apply();
    }
}
