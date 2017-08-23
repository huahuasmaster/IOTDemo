package administrator.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;

/**
 * 用于接受wsn数据 通过广播通知视图进行更新
 */
public class WsnDataService extends Service {
    public WsnDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    /**
     * 此方法在每次service启动时执行
     * 在此处进行mqtt注册
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: 2017/8/22 mqtt主题订阅
        broadcastSend("lalala");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //通过广播 向activity里的接收器发送信息
    private void broadcastSend(String msg) {
        Intent intent = new Intent();//创建intent对象
        intent.setAction("administrator.service.WsnDataService");
        intent.putExtra("msg",msg);
        sendBroadcast(intent);
    }
}
