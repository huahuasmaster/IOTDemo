package administrator.base.mqtt;

import android.util.Log;
import android.widget.Toast;

import com.lichfaker.log.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;

import administrator.application.ContextApplication;

/**
 * 使用EventBus分发事件
 *
 * @author LichFaker on 16/3/25.
 * @Email lichfaker@gmail.com
 */
public class MqttCallbackBus implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {
        Logger.e(cause.getMessage());

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        Logger.e(topic + "====" + message.toString()+
                "==== 当前队列长度:"+SubPubQueue.getMsgQueue().size());
//        Log.e("mqtt","新消息");
        SubPubQueue.getMsgQueue().add(new MqttMsgBean(topic,message));

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

}
