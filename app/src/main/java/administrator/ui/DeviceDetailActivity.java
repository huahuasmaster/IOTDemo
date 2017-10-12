package administrator.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.List;

import administrator.adapters.DataSimpleAdapter;
import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.entity.DeviceInArea;
import administrator.entity.UniversalData;
import administrator.enums.DataTypeEnum;

public class DeviceDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView batteryImg;
    private ImageView wifiImg;
    private RecyclerView rv;
    private Switch statusSwitch;
    private ImageView goSettingImg;
    private ImageView goBackImg;
    private long deviceId;
    private int type;
    private TextView title;
    private MaterialDialog waitDialog;//提示等待弹窗
    private DeviceInArea deviceInArea;
    private DataSimpleAdapter adapter;
    private List<UniversalData<Boolean>> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        deviceId = getIntent().getLongExtra("device_id",-1L);
        type = getIntent().getIntExtra("data_type",-1);

        findViews();

        initData();


        goBackImg.setOnClickListener(this);
        goSettingImg.setOnClickListener(this);
//        Snackbar.make(rv,position == -1 ? "未知设备" : "这是第"+(position+1)+"个卡片中的设备"
//                ,Snackbar.LENGTH_SHORT)
//                .show();
//        title.setText("第"+(position+1)+"个设备");
    }

    private void findViews() {
        batteryImg = (ImageView) findViewById(R.id.bat_img);
        wifiImg = (ImageView) findViewById(R.id.wifi_img);
        rv = (RecyclerView)findViewById(R.id.all_data_rv);
        statusSwitch = (Switch)findViewById(R.id.detail_status_switch);
        goSettingImg = (ImageView)findViewById(R.id.go_setting_img);
        goBackImg = (ImageView)findViewById(R.id.go_back);
        title = (TextView)findViewById(R.id.title_msg);
    }

    private void initViews() {
        if(deviceInArea != null) {
            title.setText(deviceInArea.getDeviceName()
                    +"("+deviceInArea.getOtherName()+")");

            statusSwitch.setChecked(deviceInArea.getStatus() == 1);

            LinearLayoutManager manager = new LinearLayoutManager(this);
            adapter  = new DataSimpleAdapter();
            adapter.setDataType(DataTypeEnum.indexOf(deviceInArea.getType()));
            adapter.setDataList(deviceInArea.getDeviceDataList());
            rv.setAdapter(adapter);
            rv.setLayoutManager(manager);
        }
        if(waitDialog.isShowing()) {
            waitDialog.dismiss();
        }
    }
    private void initData() {
        waitDialog = new MaterialDialog.Builder(this)
                .title(getResources()
                        .getString(R.string.getting_data))
                .content(getResources()
                        .getString(R.string.plz_wait))
                .progress(true,0)
                .autoDismiss(false)
                .progressIndeterminateStyle(false)
                .build();

        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                deviceInArea = new Gson().fromJson(response,DeviceInArea.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initViews();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(DeviceDetailActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
            }
        };

        waitDialog.show();

        String url = UrlHandler.getDeviceWithDataUrl(deviceId,type,10);
        HttpUtil.sendRequestWithCallback(url,listener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go_back:
                finish();break;
            case R.id.go_setting_img :
                Intent intent = new Intent(DeviceDetailActivity.this
                        ,DeviceSettingActivity.class);
                intent.putExtra("device_in_area",deviceInArea);
                startActivity(intent);
                break;
        }
    }
}
