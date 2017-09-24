package administrator.ui;

import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lichfaker.log.Logger;
import com.qrcodescan.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.base.mqtt.MqttManager;
import administrator.base.mqtt.MqttMsgBean;
import administrator.entity.DeviceDto;
import administrator.entity.DeviceInArea;
import administrator.enums.DataTypeEnum;

/**
 * 终端上线页面
 *
 * 1->校验设备信息
 * 2->判断该空间下是否有网关
 *      if(已有绑定的网关) {
 *          选择网关
 *          讲待上线终端的sn通过/lpwa/lora/grant/{网关sn}发布，数据为0${节点sn}
 *          等待 上线成功LI0001 消息
 *          if(30s内接收到) {
 *              等待用户绑定安装位置 向服务器发送请求
 *          } else {
 *              提示用户将设备靠近网关 重新等待
 *          }
 *      } else {
 *          弹窗提示先绑定网关，点击确定返回上一个页面
 *      }
 */
public class DeviceOnlineActivity extends AppCompatActivity {

    private TextView countDownTxt;
    private TextView deviceInfo;
    private TextView boundTxt;
    private Button confirmBtn;
    private Button chooseGateBtn;
    private ProgressBar progressBar;
    private ImageView checkImg;
    private MaterialDialog chooseGateDialog;
    private MaterialDialog exitDialog;

    private final int DEVICE_UNREGIST_MSG = 0x10;
    private final int TIME_OUT_MSG = 0x11;
    private final int COUNT_DOWN_MSG = 0x12;
    private final int DEVICE_ONLINE_MSG = 0x13;
    private final int GET_GATES_MSG = 0x14;

    private DeviceDto deviceDto;
    private String sn;
    private final int totalTime = 1000;
    private List<DeviceDto> mGates;
    private String[] gateNames;
    private boolean registed;
    private boolean bounded;


    private Timer timer = new Timer();
    private int remainTime = totalTime;
    private TimerTask task;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {

                //倒计时
                case COUNT_DOWN_MSG:
                    if (remainTime >= 0) {
                        countDownTxt.setText("" + remainTime + "S");
                        break;
                    }

                //时间结束，前去重新绑定网络
                case TIME_OUT_MSG:
                    Toast.makeText(DeviceOnlineActivity.this,"绑定失败，请将设备靠近网关，重新尝试"
                            ,Toast.LENGTH_SHORT).show();
                    showWaitText(false);
                    break;
                //上线成功
                case DEVICE_ONLINE_MSG:
                    Snackbar.make(deviceInfo,
                            R.string.online_successed,Snackbar.LENGTH_SHORT).show();
                    showWaitText(false);
                    showSuccessText();
                    if(timer != null) {
                        timer.cancel();
                    }
                    break;
                case GET_GATES_MSG:
                    chooseGateDialog = new MaterialDialog.Builder(DeviceOnlineActivity.this)
                            .title(R.string.choose_gate)
                            .items(gateNames)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                    changeGate(gateNames[position]);
                                }
                            })
                            .positiveText(R.string.cancle)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    dialog.dismiss();
                                }
                            }).build();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_online);
        initViews();

        sn = getIntent().getStringExtra("device_sn");
        deviceDto = (DeviceDto) getIntent().getSerializableExtra("device_dto");
        if(!sn.equals("") && deviceDto != null) {
            deviceInfo.setText("当前设备为：" + deviceDto.getDeviceModelDto().getName()
                    + "(" + sn + ")");
        } else {
            finish();
        }

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bounded) {
                    Intent intent = new Intent(DeviceOnlineActivity.this,
                            DeviceSettingActivity.class);
                    intent.putExtra("from_online_act",true);
                    intent.putExtra("device_id",deviceDto.getId());
                    startActivity(intent);
                } else {
                    showWaitText(true);
                    MqttManager.getInstance().publish("/lpwa/lora/grant/"
                                    + chooseGateBtn.getText().toString(),
                            0, ("0$" + deviceDto.getSn()).getBytes());
                    Logger.e("正在监听mqtt消息");
                    MqttManager.getInstance().subscribe("/lpwa/lora/info/"
                            + chooseGateBtn.getText().toString(), 0);
                }
            }
        });
        chooseGateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chooseGateDialog != null) {
                    chooseGateDialog.show();
                }
            }
        });
        showWaitText(false);
        checkGateOnline();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void checkGateOnline() {
        String url = UrlHandler.getCurrentGate();
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                mGates = new Gson().fromJson(response,
                        new TypeToken<List<DeviceDto>>(){}.getType());
                if(mGates == null || mGates.size() == 0) {
                    showExitDialog();
                } else {
                    gateNames = new String[mGates.size()];
                    for(int i = 0; i < gateNames.length;i++) {
                        gateNames[i] = mGates.get(i).getSn();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chooseGateBtn.setText(gateNames[0]);
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
//                showExitDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chooseGateBtn.setText("GATE000001");
                    }
                });
            }
        };
        HttpUtil.sendRequestWithCallback(url,listener);
    }

    private void changeGate(String gateName) {
        chooseGateBtn.setText(gateName);
    }

    private void showExitDialog() {
        Looper.prepare();
        exitDialog.show();
        Looper.loop();
    }

    private void initViews() {
        countDownTxt = (TextView) findViewById(R.id.count_down);
        deviceInfo = (TextView)findViewById(R.id.device_info);
        boundTxt = (TextView)findViewById(R.id.bound_txt);
        confirmBtn = (Button)findViewById(R.id.confirm_btn);
        chooseGateBtn = (Button)findViewById(R.id.current_gate);
        progressBar = (ProgressBar)findViewById(R.id.progressBar_do);
        checkImg = (ImageView) findViewById(R.id.check);

        exitDialog = new MaterialDialog.Builder(this)
                        .title(R.string.cant_online)
                        .autoDismiss(false)
                        .content(R.string.plz_bound_gate_before)
                        .positiveText(getString(R.string.confirm))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                DeviceOnlineActivity.this.finish();
                            }
                        }).build();
    }

    private void showWaitText(boolean show) {
        //倒计时将开始
        if(show) {
            task = new TimerTask() {
                @Override
                public void run() {
                    if(remainTime > -1) {
                        remainTime -- ;
                        Message message = new Message();
                        message.what = COUNT_DOWN_MSG;
                        handler.sendMessage(message);
                    }
                }
            };
            remainTime = totalTime;
            timer = new Timer();
            timer.schedule(task,1000,1000);
        } else {
            if(task != null) {
                task.cancel();
                task = null;
            }
            if(timer != null) {
                timer.cancel();
                timer = null;
            }
        }
        //当处于等待状态下时，以下控件将显示
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        countDownTxt.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        countDownTxt.setText(""+remainTime+"s");
        //修改文本
        String tip = show ? getString(R.string.bounding_gate)
                : getString(R.string.will_bound_this_gate);
        boundTxt.setText(tip);
        //设置按钮可按状态
        confirmBtn.setEnabled(!show);
        chooseGateBtn.setEnabled(!show);
    }

    private void showSuccessText() {
        boundTxt.setText(R.string.bound_gate_successfully);
        checkImg.setVisibility(View.VISIBLE);
        confirmBtn.setEnabled(true);
        confirmBtn.setText(R.string.go_setting);
    }

    private void sendDeviceOnlineMsg(String deviceSn) {
        Logger.i("向服务器发送消息中");
        String url = UrlHandler.deviceOnline(deviceSn);
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Snackbar.make(confirmBtn,"操作成功",Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Snackbar.make(confirmBtn,R.string.failed_work,Snackbar.LENGTH_SHORT).show();
            }
        };
        HttpUtil.sendRequestWithCallback(url,listener);
    }
    @Subscribe(priority = 100)
    public void onGateOnlineEvent(MqttMsgBean msgBean) {
        Looper.prepare();
        Logger.e("DeviceOnlineAct收到消息 主题："+msgBean.getMainTopic()
                +" 内容："+msgBean.getMqttMessage().toString());
        Toast.makeText(this,"gateOnlineAct收到消息 主题："+msgBean.getMainTopic()
                +" 内容："+msgBean.getMqttMessage().toString(),Toast.LENGTH_SHORT).show();
        if(msgBean.getMainTopic().equals(MqttMsgBean.INFO)) {
            Logger.i("判断中");
            Set<String> keySet = msgBean.getDataMap().keySet();
            for(final String sn : keySet) {
                Logger.i("sn = "+sn);
                if(sn.equals(deviceDto.getSn()) &&
                        msgBean.getDataMap().get(sn).equals(MqttMsgBean.DEVICE_ONLINE)) {
                    bounded = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWaitText(false);
                            showSuccessText();
                            sendDeviceOnlineMsg(sn);
                        }
                    });

                    break;
                }
            }
        }
        Looper.loop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
