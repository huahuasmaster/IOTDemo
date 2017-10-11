package administrator.ui;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaygoo.widget.RangeSeekBar;
import com.lichfaker.log.Logger;
import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.List;

import administrator.adapters.DeviceCardAdapter;
import administrator.adapters.listener.DeviceCardCallbackListener;
import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.entity.DeviceInArea;
import administrator.enums.DataTypeEnum;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class AreaDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewPager viewPager;
    private LayoutInflater inflater;
    private Button goCheckBtn;
    private MaterialDialog thresholdSetDialog;
    private MaterialDialog waitDialog;
    private DeviceCardAdapter adapter;
    private long deviceId;
    private int type;
    private int targetNth = 0;
    private long areaId;

    public static final int DEAULT_OFFSCEEN_LIMIT = 3;
    private List<View> viewList = new ArrayList<>();
    private List<DeviceInArea> deviceInAreaList = new ArrayList<>();
    private boolean isFirst = true;

    private DeviceCardCallbackListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_detail);

        areaId = getIntent().getLongExtra("area_id",-1L);
        deviceId = getIntent().getLongExtra("device_id",-1L);
        type = getIntent().getIntExtra("data_type",-1);


        viewPager = (ViewPager) findViewById(R.id.device_pager);
        initViews();
        initData();
        adapter = new DeviceCardAdapter(this);
        adapter.setListener(listener);
        adapter.setList(viewList);
        viewPager.setAdapter(adapter);
        viewPager.setPageMargin(50);
        viewPager.setOffscreenPageLimit(DEAULT_OFFSCEEN_LIMIT);
    }

    //根据各项数据 提供不同布局 暂时只需要一种布局
    private List<View> getPageViews(List<DeviceInArea> deviceInAreas) {
        inflater = LayoutInflater.from(this);
        List<View> views = new ArrayList<>();
        for(DeviceInArea deviceInArea : deviceInAreas) {
            views.add(inflater.inflate(R.layout.device_card_item,null));
        }
        return views;
    }

    //根据上一个页面传过来的数据 确定显示第几个卡片
    private int getTargetNth() {
        //如果deviceid 或者 type有一个不确定 则直接显示第一个
        if(deviceId != -1 && type != -1) {
            for(int i = 0; i < adapter.getDeviceInAreaList().size();i++) {
                DeviceInArea deviceInArea = adapter.getDeviceInAreaList().get(i);
                //寻找type和设备都对应的卡片
                if(deviceInArea.getType() == type && deviceInArea.getId() == deviceId) {
                    return i;
                }
            }
        }

        return 0;
    }
    private void initViews() {

        listener = new DeviceCardCallbackListener() {
            @Override
            public void onBack() {
                finish();
            }

            @Override
            public void onThreshold(int position) {
                showThresholdSetDialog(position);
            }

            @Override
            public void onCheck(int position) {
                Intent intent = new Intent(
                        AreaDetailActivity.this,DeviceDetailActivity.class);
                intent.putExtra("device_id",deviceInAreaList.get(position).getId());
                intent.putExtra("data_type",deviceInAreaList.get(position).getType());
                startActivity(intent);
            }
        };
    }
    //进行网络请求 获取必须数据
    private void initData(){
        String url = UrlHandler.getDeviceInAreaListByAreaId(areaId,20);
        waitDialog = new MaterialDialog.Builder(this)
                            .title(getResources()
                                    .getString(R.string.getting_data))
                            .content(getResources()
                                    .getString(R.string.plz_wait))
                            .progress(true,0)
                            .progressIndeterminateStyle(false)
                            .build();
        //回调函数 在获取数据之后更新试图  如果是第一次 则帮用户跳转到对应卡片
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                deviceInAreaList = new Gson().fromJson(response,
                        new TypeToken<List<DeviceInArea>>(){}.getType());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setDeviceInAreaList(deviceInAreaList);
                        adapter.setList(getPageViews(deviceInAreaList));
                        adapter.notifyDataSetChanged();
                        waitDialog.dismiss();
                        if (isFirst) {
                            viewPager.setCurrentItem(getTargetNth());
                            isFirst = false;
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        waitDialog.dismiss();
                    }
                });
            }
        };
        waitDialog.show();
        HttpUtil.sendRequestWithCallback(url,listener);

    }
    /**
     * 弹出阈值设置弹窗
     * @param position
     */
    private void showThresholdSetDialog(final int position) {

        DataTypeEnum type = DataTypeEnum
                .indexOf(adapter.getDeviceInAreaList().get(position).getType());
        String title = "自定义适宜";
        String temp = "";
        switch (type) {
            case HUMIDITY:title += "湿度";temp = "%";break;
            case TMP_CELSIUS: title += "温度";temp = "℃";break;
            case TMP_K:title += "温度";temp = "K";break;
        }
        final String unit = temp;
        thresholdSetDialog = new MaterialDialog.Builder(this)
                .title(title)
                .customView(R.layout.threshold_set_single,false)
                .positiveText("确定")
                .negativeText("取消")
                .build();

        final RangeSeekBar seekBar = (RangeSeekBar)thresholdSetDialog
                                .getCustomView().findViewById(R.id.seekbar);
        final TextView range = (TextView)thresholdSetDialog.getCustomView()
                            .findViewById(R.id.range);

        // TODO: 2017/8/8 初始化范围值 后续应该用网络请求代替
        seekBar.setValue(19f,27f);

        range.setText((int)seekBar
                .getCurrentRange()[0]+ unit+"-"+(int)seekBar.getCurrentRange()[1]+unit);
        seekBar.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser) {
                if (isFromUser) {
                    range.setText((int)min+ unit+"-"+(int)max+unit);
                }
            }
        });

        MDButton positiveBtn = thresholdSetDialog.getActionButton(DialogAction.POSITIVE);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetThreshold(position,(int)seekBar.getCurrentRange()[0],(int)seekBar.getCurrentRange()[1]);
                thresholdSetDialog.dismiss();
            }
        });
        thresholdSetDialog.show();

    }
    /**
     * 实际进行网络请求调整阈值的方法
     */
    private void resetThreshold(int position,int min,int max) {
//        Snackbar.make(viewPager,"max："+max+" min:"+min,Snackbar.LENGTH_SHORT).show();
        DeviceInArea mDia = adapter.getDeviceInAreaList().get(position);
        String url = UrlHandler.addAlertConfig(mDia.getId(),
                DataTypeEnum.indexOf(mDia.getType()).getCode());
        Logger.i("url = "+url);
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Snackbar.make(viewPager,R.string.edit_successfully,Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Snackbar.make(viewPager,R.string.failed_work,Snackbar.LENGTH_SHORT).show();
            }
        };
        RequestBody body = new FormBody.Builder()
                        .add("max", String.valueOf(max))
                        .add("min",String.valueOf(min))
                .build();
        HttpUtil.sendRequestWithCallback(url,body,listener);
    }
    @Override
    public void onClick(View view) {

    }
}
