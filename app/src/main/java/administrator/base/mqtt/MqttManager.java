package administrator.base.mqtt;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.lichfaker.log.Logger;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.greenrobot.eventbus.EventBus;

import administrator.application.ContextApplication;
import administrator.base.http.UrlHandler;

/**
 * 管理mqtt的连接,发布,订阅,断开连接, 断开重连等操作
 *
 * 需要根据用户的id订阅对应的主题 用户信息储存在“login_data”sharedpreferences中
 *
 * @author LichFaker on 16/3/24.
 * @Email lichfaker@gmail.com
 */
public class MqttManager {



    //用于存取数据
    private static SharedPreferences sp;

    private static SharedPreferences.Editor editor;

    //mqtt服务器端口
    private static String port = "1883";

    //appId
    private static String appId = "1";

    // 单例
    private static MqttManager mInstance = null;

    // 回调
    private MqttCallback mCallback;

    // Private instance variables
    private MqttClient client;
    private MqttConnectOptions conOpt;
    private boolean clean = true;

    private MqttManager() {
        mCallback = new MqttCallbackBus();
    }

    public static MqttManager getInstance() {
        if (null == mInstance) {
            mInstance = new MqttManager();
            sp = ContextApplication
                    .getContext()
                    .getSharedPreferences("login_data", Context.MODE_PRIVATE);
        }
        return mInstance;
    }

    public  void startQueue() {
        Thread thread = new Thread(){
            @Override
            public void run() {

                while(true){
                    if(!SubPubQueue.getMsgQueue().isEmpty()){
                        try {
                            final MqttMsgBean topicMessage = SubPubQueue.getMsgQueue().take();
                                Logger.e("广播消息:" + topicMessage.getMqttMessage().toString());
                                //广播消息
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        EventBus.getDefault().post(topicMessage);
                                    }
                                }).start();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }
            }
        };
        Logger.e("队列开始启动");
        thread.start();
    }
    /**
     * 释放单例, 及其所引用的资源
     */
    public static void release() {
        try {
            if (mInstance != null) {
                mInstance.disConnect();
                mInstance = null;
            }
        } catch (Exception e) {

        }
    }

    /**
     * 获取url
     * @return
     */
    private static String getUrl() {
        return "tcp://"+ UrlHandler.getIp()+":"+port;
    }

    /**
     * 获取特定mqtt主题-设备数据
     */
    public static String getDeviceDataTopic() {
        return "/lpwa/app/"+appId+"/data/"+sp.getLong("user_id",-1L);
    }

    /**
     * 获取特定mqtt主题-设备信息
     */
    public static String getDeviceInfoTopic() {
        return "/lpwa/app/"+appId+"/info/"+sp.getLong("user_id",-1L);
    }

    /**
     * 使用默认信息进行mqtt连接
     */
    public boolean creatConnect(){
        return creatConnect(getUrl(),
                /*sp.getString("account",""),*/
                /*sp.getString("password",""),*/
                null,
                null,
                String.valueOf(sp.getLong("user_id",-1L)));
    }
    /**
     * 创建Mqtt 连接
     *
     * @param brokerUrl Mqtt服务器地址(tcp://xxxx:1863)
     * @param userName  用户名
     * @param password  密码
     * @param clientId  clientId
     * @return
     */
    public boolean creatConnect(String brokerUrl, String userName, String password, String clientId) {
        boolean flag = false;
        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);

        try {
            // Construct the connection options object that contains connection parameters
            // such as cleanSession and LWT
            conOpt = new MqttConnectOptions();
            conOpt.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            conOpt.setCleanSession(clean);
            if (password != null) {
                conOpt.setPassword(password.toCharArray());
            }
            if (userName != null) {
                conOpt.setUserName(userName);
            }

            // Construct an MQTT blocking mode client
            client = new MqttClient(brokerUrl, clientId, dataStore);

            // Set this wrapper as the callback handler
            client.setCallback(mCallback);
            flag = doConnect();
        } catch (MqttException e) {
            Logger.e(e.getMessage());
        }

        return flag;
    }

    /**
     * 建立连接
     *
     * @return
     */
    public boolean doConnect() {
        boolean flag = false;
        if (client != null) {
            try {
                client.connect(conOpt);
                Logger.e("Connected to " + client.getServerURI() + " with client ID " + client.getClientId());
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * Publish / send a message to an MQTT server
     *
     * @param topicName the name of the topic to publish to
     * @param qos       the quality of service to delivery the message at (0,1,2)
     * @param payload   the set of bytes to send to the MQTT server
     * @return boolean
     */
    public boolean publish(String topicName, int qos, byte[] payload) {

        boolean flag = false;

        if (client != null && client.isConnected()) {

            Logger.d("Publishing to topic \"" + topicName + "\" qos " + qos);

            // Create and configure a message
            MqttMessage message = new MqttMessage(payload);
            message.setQos(qos);

            // Send the message to the server, control is not returned until
            // it has been delivered to the server meeting the specified
            // quality of service.
            try {
                Logger.e("topic = "+topicName+"- msg = "+message);
                client.publish(topicName, message);
                flag = true;
            } catch (MqttException e) {

            }

        }

        return flag;
    }

    /**
     * 使用默认信息进行mqtt主题注册
     * 目前为根据用户id注册对应的data、info主题
     */
    public boolean subscribe() {
        Logger.e(getDeviceDataTopic()+"-------"+getDeviceInfoTopic());
        return subscribe(getDeviceDataTopic(),0)
                && subscribe(getDeviceInfoTopic(),0);
    }

    /**
     * Subscribe to a topic on an MQTT server
     * Once subscribed this method waits for the messages to arrive from the server
     * that match the subscription. It continues listening for messages until the enter key is
     * pressed.
     *
     * @param topicName to subscribe to (can be wild carded)
     * @param qos       the maximum quality of service to receive messages at for this subscription
     * @return boolean
     */
    public boolean subscribe(String topicName, int qos) {

        boolean flag = false;

        if (client != null && client.isConnected()) {
            // Subscribe to the requested topic
            // The QoS specified is the maximum level that messages will be sent to the client at.
            // For instance if QoS 1 is specified, any messages originally published at QoS 2 will
            // be downgraded to 1 when delivering to the client but messages published at 1 and 0
            // will be received at the same level they were published at.
            Logger.e("Subscribing to topic \"" + topicName + "\" qos " + qos);
            try {
                client.subscribe(topicName, qos);
                flag = true;
            } catch (MqttException e) {

            }
        }

        return flag;

    }

    /**
     * 取消连接
     *
     * @throws MqttException
     */
    public void disConnect() throws MqttException {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
    }

    /**
     * 重新连接
     */
    public void reConnect() throws MqttException {
        disConnect();
        creatConnect();
    }

    /**
     * 是否连接
     */
    public boolean isConnecting() {
        if(client != null) {
            return client.isConnected();
        } return false;
    }
}
