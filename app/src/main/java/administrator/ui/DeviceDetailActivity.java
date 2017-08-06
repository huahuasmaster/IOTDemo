package administrator.ui;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import administrator.adapters.DoorDataAdapter;
import administrator.entity.UniversalData;

public class DeviceDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView batteryImg;
    private ImageView wifiImg;
    private RecyclerView rv;
    private Switch aSwitch;
    private ImageView goSettingImg;
    private ImageView goBackImg;
    private int position;
    private TextView title;
    private DoorDataAdapter adapter;
    private List<UniversalData<Boolean>> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        initViews();

        initData();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter  = new DoorDataAdapter();
        adapter.setDoorData(dataList);
        rv.setAdapter(adapter);
        rv.setLayoutManager(manager);

        goBackImg.setOnClickListener(this);
        goSettingImg.setOnClickListener(this);
        position = getIntent().getIntExtra("position",-1);
        Snackbar.make(rv,position == -1 ? "未知设备" : "这是第"+(position+1)+"个卡片中的设备"
                ,Snackbar.LENGTH_SHORT)
                .show();
        title.setText("第"+(position+1)+"个设备");
    }

    private void initViews() {
        batteryImg = (ImageView) findViewById(R.id.bat_img);
        wifiImg = (ImageView) findViewById(R.id.wifi_img);
        rv = (RecyclerView)findViewById(R.id.all_data_rv);
        aSwitch = (Switch)findViewById(R.id.detail_switch);
        goSettingImg = (ImageView)findViewById(R.id.go_setting_img);
        goBackImg = (ImageView)findViewById(R.id.go_back);
        title = (TextView)findViewById(R.id.title);
    }

    private void initData() {
        dataList.add(new UniversalData<Boolean>(new Date(),true));
        dataList.add(new UniversalData<Boolean>(new Date(),false));
        dataList.add(new UniversalData<Boolean>(new Date(),true));
        dataList.add(new UniversalData<Boolean>(new Date(),false));
        dataList.add(new UniversalData<Boolean>(new Date(),true));
        dataList.add(new UniversalData<Boolean>(new Date(),false));
        dataList.add(new UniversalData<Boolean>(new Date(),true));
        dataList.add(new UniversalData<Boolean>(new Date(),false));
        dataList.add(new UniversalData<Boolean>(new Date(),true));
        dataList.add(new UniversalData<Boolean>(new Date(),false));
        dataList.add(new UniversalData<Boolean>(new Date(),true));
        dataList.add(new UniversalData<Boolean>(new Date(),false));
        dataList.add(new UniversalData<Boolean>(new Date(),true));
        dataList.add(new UniversalData<Boolean>(new Date(),false));
        dataList.add(new UniversalData<Boolean>(new Date(),true));
        dataList.add(new UniversalData<Boolean>(new Date(),false));
        dataList.add(new UniversalData<Boolean>(new Date(),true));
        dataList.add(new UniversalData<Boolean>(new Date(),false));
        dataList.add(new UniversalData<Boolean>(new Date(),true));
        dataList.add(new UniversalData<Boolean>(new Date(),false));
        dataList.add(new UniversalData<Boolean>(new Date(),true));
        dataList.add(new UniversalData<Boolean>(new Date(),false));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go_back:
                finish();
            case R.id.go_setting_img :
                Intent intent = new Intent(DeviceDetailActivity.this
                        ,DeviceSettingActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
        }
    }
}
