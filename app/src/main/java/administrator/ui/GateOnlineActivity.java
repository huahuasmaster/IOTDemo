package administrator.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lichfaker.log.Logger;
import com.qrcodescan.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import administrator.base.mqtt.MqttMsgBean;
import administrator.entity.DeviceDto;

/**
 * 关于绑定->网关在device regist里没有记录 则视为未绑定
 *
 * 网关上线流程 扫码后等待网关上线消息
 *
 * 来自smartconfig页面意味着 smartconfig已经成功 网关已连接到wifi
 *
 * 直接向服务器请求 查询是否绑定有该网关
 * if(已绑定 或 来自smartconfig){
 *  倒计时等待网关的上线成功消息
 *      if(倒计时结束但没有收到上线消息)
 *          {跳转到smartconfig页面进行上线}}
 *          else if(收到上线消息){
 *              if(已绑定) {
 *                  返回
 *              } else {
 *                  自动为用户绑定当前空间并返回
 *              }
 *          }
 *      }
 * else(未绑定 且 不是来自smartconfig){直接跳转到smartconfig页面进行上线}
 */
public class GateOnlineActivity extends AppCompatActivity {

    ImageView checkImg;

    TextView waitTxt;

    ProgressBar progressBar;

    TextView successTxt;

    TextView infoTxt;

    TextView countDownTxt;

    Timer timer;

    private DeviceDto deviceDto;//设备信息

    private boolean isFromSmartConfig = false;//是否来自smartconfig页面

    private boolean registed = false;

    private String sn = "";

    private final int TIME_OUT_MSG = 0x11;

    private final int COUNT_DOWN_MSG = 0x12;

    private final int GATE_UNREGISTED_MSG = 0x16;

    private final int GATE_ONLINE_MSG = 0x14;

    private int remainTime = 30;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                //时间结束，前去重新绑定网络
                case TIME_OUT_MSG:
                    gotoSmartConfig(true);
                    break;
                //倒计时
                case COUNT_DOWN_MSG:
                    if (remainTime >= 0) {
                        countDownTxt.setText("" + remainTime + "S");
                    } else {
                        gotoSmartConfig(true);
                        task.cancel();
                    } break;
                //上线成功
                case GATE_ONLINE_MSG:
                    Snackbar.make(infoTxt, R.string.online_successed,Snackbar.LENGTH_SHORT).show();
                    showWaitTxt(false);
                    showSuccessTxt(true);
                    if(timer != null) {
                        timer.cancel();
                    }
                    break;
                case GATE_UNREGISTED_MSG:
                    gotoSmartConfig(false);
                    task.cancel();
            }
            return false;
        }

    });

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if(remainTime > -1) {
                remainTime--;
                Message message = new Message();
                message.what = COUNT_DOWN_MSG;
                handler.sendMessage(message);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_online);
        initViews();

        sn = getIntent().getStringExtra("device_sn");
        deviceDto = (DeviceDto) getIntent().getSerializableExtra("device_dto");
        registed = deviceDto.getDeviceRegistDto() != null && deviceDto.getAreaName() != null;
        if (!sn.equals("") && deviceDto != null) {
            infoTxt.setText("当前设备为：" + deviceDto.getDeviceModelDto().getName()
                    + "(" + sn + ")");
        } else {
            finish();
        }
        timer = new Timer();
        timer.schedule(task,1000,1000);
        checkRegist();
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        Logger.e("注册eventbus成功");

    }

    private void gotoSmartConfig(boolean bounded) {
        Intent intent = new Intent(GateOnlineActivity.this,SmartConfigActivity.class);
        intent.putExtra("msg",bounded);
        String tip = bounded ? getString(R.string.SC_again) :
                getString(R.string.first_SC);
        Toast.makeText(GateOnlineActivity.this,
                tip,Toast.LENGTH_SHORT).show();
        startActivityForResult(intent,1);
    }

    private void initViews() {
        checkImg = (ImageView) findViewById(R.id.check);
        infoTxt = (TextView) findViewById(R.id.device_info);
        waitTxt = (TextView) findViewById(R.id.wait_plz);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_sr);
        successTxt = (TextView)findViewById(R.id.online_successful);
        countDownTxt = (TextView)findViewById(R.id.count_down);

        showWaitTxt(true);
        showSuccessTxt(false);
    }

    /**
     * 检查网关是否注册过
     */
    private void checkRegist() {
        if(!registed) {
            Message message = new Message();
            message.what = GATE_UNREGISTED_MSG;
            handler.sendMessage(message);
        }
    }

    private void showWaitTxt(boolean show) {
        int visible = show ? View.VISIBLE : View.INVISIBLE;
        countDownTxt.setVisibility(visible);
        progressBar.setVisibility(visible);
        waitTxt.setVisibility(visible);
    }

    private void showSuccessTxt(boolean show) {
        int visible = show ? View.VISIBLE : View.INVISIBLE;
        checkImg.setVisibility(visible);
        successTxt.setVisibility(visible);
    }

    @Subscribe(priority = 100)
    public void onGateOnlineEvent(MqttMsgBean msgBean) {
        Looper.prepare();
        Logger.e("gateOnlineAct收到消息 主题："+msgBean.getMainTopic()
                +" 内容："+msgBean.getMqttMessage().toString());
        if (msgBean.getMainTopic().equals(MqttMsgBean.INFO)) {
            Map<String,String> map = msgBean.getDataMap();
            Logger.e(map.toString());
            Set<String> keySet = map.keySet();
            //遍历map寻找需要的信息
            for (String key : keySet) {
                if (key.equals(sn) && map.get(key).equals(MqttMsgBean.GATE_ONLINE)) {
                    Message message = new Message();
                    message.what = GATE_ONLINE_MSG;
                    handler.sendMessage(message);
                    break;
                }
            }
        }
        Looper.loop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK) {
                    //未注册网关配置wifi成功 等待网关发来上线成功消息
                    isFromSmartConfig = true;
                    remainTime = 30;
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
