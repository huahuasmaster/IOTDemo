package administrator.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qrcodescan.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.entity.AreaDto;
import administrator.entity.DeviceDto;
import administrator.entity.DeviceInArea;

/**
 * 设备设置页面
 * 需求：弹窗编辑设备名称
 * 弹窗选择设备所属房间
 * 支持新增房间，与上条需求类型
 */
// TODO: 2017/8/7 考虑为选择房间弹窗增加长按效果
// TODO: 2017/8/7 优化自动填充的名称，防止出现重名的现象
public class DeviceSettingActivity extends AppCompatActivity {
    private ConstraintLayout nameItem;
    private ConstraintLayout areaItem;
    private TextView deviceName;
    private TextView areaNameText;
    private TextView realNameText;
    private TextView SNText;
    private TextView setTimeText;
    private TextView softwareVersionText;
    private TextView hardwareVersionText;
    private MaterialDialog inputNewNameDialog;//用于输入设备名称的弹窗
    private MaterialDialog inputNewRoomDialog;//用于输入新房间名称的弹窗
    private MaterialDialog chooseAreaDialog;//用于选择房间的弹窗
    private String newRoomName;
    private List<AreaDto> areaDtoList;
    private DeviceInArea deviceInArea;
    private DeviceDto deviceDto;
    private List<String> areaNameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);

        deviceInArea = (DeviceInArea) getIntent()
                .getSerializableExtra("device_in_area");

        initViews();

        initData();


        //把预先知道的信息 直接填充
        if(deviceInArea != null) {
            deviceName.setText(deviceInArea.getOtherName());
            areaNameText.setText(deviceInArea.getAreaName());
            realNameText.setText(deviceInArea.getDeviceName());
        }

        nameItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputNewNameDialog.show();
            }
        });
        areaItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseAreaDialog.show();
            }
        });
    }

    private void initViews() {
        areaItem = (ConstraintLayout) findViewById(R.id.room_item);
        nameItem = (ConstraintLayout) findViewById(R.id.name_item);
        deviceName = (TextView) findViewById(R.id.device_defined_name);
        areaNameText = (TextView) findViewById(R.id.room_belong_to);
        realNameText = (TextView) findViewById(R.id.device_real_name);
        SNText = (TextView) findViewById(R.id.device_sn);
        setTimeText = (TextView) findViewById(R.id.device_set_time);
        hardwareVersionText = (TextView) findViewById(R.id.device_hardware_version);
        softwareVersionText = (TextView) findViewById(R.id.device_software_version);

        inputNewNameDialog = new MaterialDialog
                .Builder(DeviceSettingActivity.this)
                .title("新名称")
                .inputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2, 16)
                .positiveText("确定")
                .input("请输入新名称", "我的设备", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        changeName(input.toString());
                    }
                }).build();

        inputNewRoomDialog = new MaterialDialog
                .Builder(DeviceSettingActivity.this)
                .title("新房间名称")
                .inputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2, 16)
                .positiveText("确定")
                .input("请输入新名称", "我的房间", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        changeRoom(input.toString());
                    }
                }).build();

        chooseAreaDialog = getChooseAreaDialog(areaNameList);
    }

    private void initData() {
        String deviceDetailUrl = UrlHandler.getDeviceDetailDesc(deviceInArea.getId());
        String areaListUrl = UrlHandler.getAreaListByDeviceId(deviceInArea.getId());

        //设备信息请求的回调函数
        HttpCallbackListener getDeviceDetailListener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                deviceDto = new Gson().fromJson(response,DeviceDto.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SNText.setText(deviceDto.getSn());
                        setTimeText.setText(deviceDto.getCreateTime());
                        hardwareVersionText.setText(""+deviceDto.getDeviceModelDto()
                                .getHardwareVersion());
                        softwareVersionText.setText(""+deviceDto.getDeviceModelDto()
                                .getSoftwareVersion());
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        };

        //安装位置列表请求的回调函数
        HttpCallbackListener getAreaListListener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                areaDtoList = new Gson().fromJson(response,
                        new TypeToken<List<AreaDto>>(){}.getType());
                areaNameList.clear();
                for(AreaDto dto : areaDtoList) {
                    areaNameList.add(dto.getName());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chooseAreaDialog = getChooseAreaDialog(areaNameList);
                    }
                });


            }

            @Override
            public void onError(Exception e) {

            }
        };

        HttpUtil.sendRequestWithCallback(deviceDetailUrl,getDeviceDetailListener);
        HttpUtil.sendRequestWithCallback(areaListUrl,getAreaListListener);
    }

    private MaterialDialog getChooseAreaDialog(final List<String> areaNameList) {
        return new MaterialDialog
                .Builder(DeviceSettingActivity.this)
                .title("选择房间/位置")
                .items(areaNameList)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        changeRoom(areaNameList.get(position).toString());
                    }
                })
                .itemsLongCallback(new MaterialDialog.ListLongCallback() {
                    @Override
                    public boolean onLongSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        return false;
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                inputNewRoomDialog.show();
                            }
                        });
                        chooseAreaDialog.dismiss();
                    }
                })
                .neutralText("新增房间")
                .build();
    }

    private void changeName(String name) {
        deviceName.setText(name);
    }

    private void changeRoom(String room) {
        areaNameText.setText(room);
    }
}
