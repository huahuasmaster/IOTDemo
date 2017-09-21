package administrator.base.mqtt;

import com.lichfaker.log.Logger;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuang_ge on 2017/8/23.
 * 封装mqtt消息的类 提供方法 返回解析后的数据
 */

public class MqttMsgBean {

    public static final String DATA = "data";
    public static final String INFO = "info";
    public static final String ALERT = "alert";
    public static final String WILD_CARD = "+";//通配符
    public static final String GATE_ONLINE = "GI0001";
    public static final String DEVICE_ONLINE = "NI0001";
    //消息主题
    private String topic;

    //关键主题
    private String mainTopic;

    //消息中解析出的sn列表
    private List<String> snList;

    //sn-实际数据 映射
    private Map<String,String> dataMap;

    //消息主体
    private MqttMessage mqttMessage;

    public MqttMsgBean(String topic, MqttMessage mqttMessage) {
        this.topic = topic;//原始主题 /lpwa/app/{appId}/{data|info|alert}/{userId}
        this.mqttMessage = mqttMessage;//原始数据 sn1$23#60%|sn2$35#65%
        //解析出核心主题 {data|info|alert}
        try{
            String[] topicTemp = topic.split("/");
            mainTopic = topicTemp[topicTemp.length - 2];
        } catch (Exception e) {
            mainTopic = null;
        }
        //对数据进行解析

        try{
            //以设备为单位拆分
            dataMap = new HashMap<>();
            snList = new ArrayList<>();
            String[] deviceDatas = mqttMessage.toString().split("\\|");//sn1$23#60%、sn2$35#65%
            for(String deviceData : deviceDatas) {
                //从单个设备数据中拆分出sn与数据
                String[] snAndData = deviceData.split("\\$");//sn1
                snList.add(snAndData[0]);
                if(snAndData.length > 1) {
                    dataMap.put(snAndData[0], snAndData[1]);//(sn1,23#69%)
                }
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    public MqttMsgBean() {

    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public MqttMessage getMqttMessage() {
        return mqttMessage;
    }

    public void setMqttMessage(MqttMessage mqttMessage) {
        this.mqttMessage = mqttMessage;
    }

    public String getMainTopic() {
        return mainTopic;
    }

    public List<String> getSnList() {
        return snList;
    }

    public Map<String, String> getDataMap() {
        return dataMap;
    }
}
