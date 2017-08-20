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
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.jaygoo.widget.RangeSeekBar;
import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.List;

import administrator.adapters.DeviceCardAdapter;
import administrator.base.DeviceCardCallbackListener;
import administrator.base.http.HttpCallbackListener;

public class AreaDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewPager viewPager;
    private LayoutInflater inflater;
    private Button goCheckBtn;
    private MaterialDialog thresholdSetDialog;
    private MaterialDialog waitDialog;

    public static final int DEAULT_OFFSCEEN_LIMIT = 3;
    private List<View> viewList = new ArrayList<>();
    private boolean isFirst = true;

    private DeviceCardCallbackListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_detail);

        viewPager = (ViewPager) findViewById(R.id.device_pager);
        initViews();
        DeviceCardAdapter adapter = new DeviceCardAdapter(this);
        adapter.setList(viewList);
        adapter.setListener(listener);
        viewPager.setAdapter(adapter);
        viewPager.setPageMargin(50);
        viewPager.setOffscreenPageLimit(DEAULT_OFFSCEEN_LIMIT);
    }

    private void initViews() {
        inflater = LayoutInflater.from(this);
        for(int i = 0;i <= 3;i++){
            viewList.add(inflater.inflate(R.layout.device_card_item,null));
        }

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
                intent.putExtra("position",position);
                startActivity(intent);
            }
        };
    }
    private void initData(){
        waitDialog = new MaterialDialog.Builder(this)
                            .title(getResources()
                                    .getString(R.string.getting_data))
                            .content(getResources()
                                    .getString(R.string.plz_wait))
                            .progress(true,0)
                            .progressIndeterminateStyle(false)
                            .build();
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Toast.makeText(AreaDetailActivity.this,response,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AreaDetailActivity.this,"获取数据失败",Toast.LENGTH_SHORT).show();
            }
        };
        waitDialog.show();


    }
    /**
     * 弹出阈值设置弹窗
     * @param postion
     */
    private void showThresholdSetDialog(final int postion) {

        thresholdSetDialog = new MaterialDialog.Builder(this)
                .title("自定义适宜温度")
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

        range.setText((int)seekBar.getCurrentRange()[0]+ "℃-"+(int)seekBar.getCurrentRange()[1]+"℃");
        seekBar.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser) {
                if (isFromUser) {
                    range.setText((int)min+ "℃-"+(int)max+"℃");
                }
            }
        });

        MDButton positiveBtn = thresholdSetDialog.getActionButton(DialogAction.POSITIVE);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetThreshold(postion,(int)seekBar.getCurrentRange()[0],(int)seekBar.getCurrentRange()[1]);
                thresholdSetDialog.dismiss();
            }
        });
        thresholdSetDialog.show();

    }

    private void resetThreshold(int position,int max,int min) {
        Snackbar.make(viewPager,"max："+max+" min:"+min,Snackbar.LENGTH_SHORT).show();
    }
    @Override
    public void onClick(View view) {

    }
}
