package administrator.base.mqtt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.lichfaker.log.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Date;

import administrator.application.ContextApplication;
import administrator.base.AlertToMsgUtil;
import administrator.entity.AlertDto;
import administrator.enums.AlertTypeEnum;
import administrator.enums.DataTypeEnum;
import administrator.ui.MainActivity;

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
        MqttMsgBean bean = new MqttMsgBean(topic,message);
        SubPubQueue.getMsgQueue().add(bean);

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

}
