package administrator.ui;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * 设备设置页面
 * 需求：弹窗编辑设备名称
 *      弹窗选择设备所属房间
 *          支持新增房间，与上条需求类型
 */
// TODO: 2017/8/7 考虑为选择房间弹窗增加长按效果
// TODO: 2017/8/7 优化自动填充的名称，防止出现重名的现象
public class DeviceSettingActivity extends AppCompatActivity {
    private ConstraintLayout nameItem;
    private ConstraintLayout roomItem;
    private TextView deviceName;
    private TextView roomName;
    private MaterialDialog inputNewNameDialog;//用于输入设备名称的弹窗
    private MaterialDialog inputNewRoomDialog;//用于输入新房间名称的弹窗
    private MaterialDialog chooseRoomDialog;//用于选择房间的弹窗
    private String newRoomName;
    private List<String> roomList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);

        initData();

        initViews();

        nameItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputNewNameDialog.show();
            }
        });

        roomItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseRoomDialog.show();
            }
        });
    }

    private void initViews() {
        roomItem = (ConstraintLayout) findViewById(R.id.room_item);
        nameItem = (ConstraintLayout) findViewById(R.id.name_item);
        deviceName = (TextView)findViewById(R.id.device_defined_name);
        roomName = (TextView)findViewById(R.id.room_belong_to);

        inputNewNameDialog = new MaterialDialog
                .Builder(DeviceSettingActivity.this)
                .title("新名称")
                .inputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2,16)
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
                .inputRange(2,16)
                .positiveText("确定")
                .input("请输入新名称", "我的房间", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        changeRoom(input.toString());
                    }
                }).build();

        chooseRoomDialog = new MaterialDialog
                .Builder(this)
                .title("选择房间/位置")
                .items(roomList)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        changeRoom(roomList.get(position).toString());
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
                        chooseRoomDialog.dismiss();
                    }
                })
                .neutralText("新增房间")
                .build();
    }

    private void initData() {
        roomList.add("书房");
        roomList.add("厕所");
        roomList.add("卧室");
        roomList.add("庭院");
        roomList.add("客厅");
        roomList.add("天台");
    }

    private void changeName(String name){
        deviceName.setText(name);
    }

    private void changeRoom(String room) {
        roomName.setText(room);
    }
}
