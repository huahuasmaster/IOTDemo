package administrator.base.mqtt;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by zhuang_ge on 2017/8/23.
 */

public class MqttMsgBean {
    //消息主题
    private String topic;

    //消息主体
    private MqttMessage mqttMessage;

    public MqttMsgBean(String topic, MqttMessage mqttMessage) {
        this.topic = topic;
        this.mqttMessage = mqttMessage;
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
}
