package administrator.view;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.google.gson.Gson;
import com.jaygoo.widget.RangeSeekBar;
import com.lichfaker.log.Logger;
import com.qrcodescan.R;

import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.entity.AlertConfigDto;
import administrator.entity.DeviceInArea;
import administrator.enums.DataTypeEnum;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by zhuang_ge on 2017/10/12.
 * 用于生成dialog弹窗的工具类
 */

public class DialogUtil {
    public static void showThresholdSetDialog(final DeviceInArea dia, final Context context) {

        DataTypeEnum type = DataTypeEnum
                .indexOf(dia.getType());


        String title = "自定义适宜";
        String temp = "";
        switch (type) {
            case HUMIDITY:
                title += "湿度";
                temp = "%";
                break;
            case TMP_CELSIUS:
                title += "温度";
                temp = "℃";
                break;
            case TMP_K:
                title += "温度";
                temp = "K";
                break;
        }
        final String unit = temp;
        final MaterialDialog thresholdSetDialog = new MaterialDialog.Builder(context)
                .title(title)
                .customView(R.layout.threshold_set_single, false)
                .positiveText("确定")
                .negativeText("取消")
                .build();

        final RangeSeekBar seekBar = (RangeSeekBar) thresholdSetDialog
                .getCustomView().findViewById(R.id.seekbar);
        final TextView range = (TextView) thresholdSetDialog.getCustomView()
                .findViewById(R.id.range);

        // TODO: 2017/8/8 初始化范围值 后续应该用网络请求代替
        seekBar.setValue(19f, 27f);
        //请求用户预先设置的阈值
        String url = UrlHandler.getAlertConfig(dia.getId(), type.getCode());
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                final AlertConfigDto acd = new Gson().fromJson(response, AlertConfigDto.class);
                if (acd != null) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setValue((float) acd.getMinValue(), (float) acd.getMaxValue());
                            range.setText((int) seekBar
                                    .getCurrentRange()[0] + unit + "-" + (int) seekBar.getCurrentRange()[1] + unit);
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {

            }
        };
        HttpUtil.sendRequestWithCallback(url, listener);
        range.setText((int) seekBar
                .getCurrentRange()[0] + unit + "-" + (int) seekBar.getCurrentRange()[1] + unit);
        seekBar.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser) {
                if (isFromUser) {
                    range.setText((int) min + unit + "-" + (int) max + unit);
                }
            }
        });

        MDButton positiveBtn = thresholdSetDialog.getActionButton(DialogAction.POSITIVE);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetThreshold(dia, (int) seekBar.getCurrentRange()[0], (int) seekBar.getCurrentRange()[1]);
                thresholdSetDialog.dismiss();
            }
        });
        thresholdSetDialog.show();

    }

    /**
     * 实际进行网络请求调整阈值的方法
     */
    public static void resetThreshold(DeviceInArea mDia, int min, int max) {
//        Snackbar.make(viewPager,"max："+max+" min:"+min,Snackbar.LENGTH_SHORT).show();
        String url = UrlHandler.addAlertConfig(mDia.getId(),
                DataTypeEnum.indexOf(mDia.getType()).getCode());
        Logger.i("url = " + url);
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Snackbar.make(null, R.string.edit_successfully, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Snackbar.make(null, R.string.failed_work, Snackbar.LENGTH_SHORT).show();
            }
        };
        RequestBody body = new FormBody.Builder()
                .add("max", String.valueOf(max))
                .add("min", String.valueOf(min))
                .build();
        HttpUtil.sendRequestWithCallback(url, body, listener);
    }

}
