package administrator.view;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.google.gson.Gson;
import com.jaygoo.widget.RangeSeekBar;
import com.lichfaker.log.Logger;
import com.qrcodescan.R;

import administrator.application.ContextApplication;
import administrator.base.AlertToMsgUtil;
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


        assert type != null;
        String title = "自定义适宜" + type.getType();
        final String unit = AlertToMsgUtil.getUnit(type);
        final MaterialDialog thresholdSetDialog = new MaterialDialog.Builder(context)
                .title(title)
                .customView(R.layout.threshold_set_range, false)
                .positiveText(context.getResources().getString(R.string.confirm))
                .negativeText(context.getResources().getString(R.string.cancle))
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

    public static void showSingleThreshold(final DeviceInArea dia, final Context context) {
        DataTypeEnum type = DataTypeEnum
                .indexOf(dia.getType());
        String title = "触发警报的最短移动时间:";
        final String unit = AlertToMsgUtil.getUnit(DataTypeEnum.SEC);
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(title)
                .customView(R.layout.threshold_set_single,false)
                .positiveText(context.getResources().getString(R.string.confirm))
                .negativeText(context.getResources().getString(R.string.cancle))
                .build();
        final RangeSeekBar seekBar = (RangeSeekBar)dialog.findViewById(R.id.seekbar);
        final TextView range = (TextView) dialog.getCustomView()
                .findViewById(R.id.range);
        seekBar.setValue(0,30f);
        String url = UrlHandler.getAlertConfig(dia.getId(),DataTypeEnum.SEC.getCode());
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                final AlertConfigDto acd = new Gson().fromJson(response,AlertConfigDto.class);
                if (acd != null) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setValue((float) acd.getMaxValue());
                            range.setText(""+(int) seekBar.getCurrentRange()[0] + unit);

                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {

            }
        };
        HttpUtil.sendRequestWithCallback(url, listener);
        range.setText(""+(int) seekBar.getCurrentRange()[0] + unit);
        seekBar.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser) {
                if (isFromUser) {
                    Log.i("",min+","+max);
                    range.setText(""+(int) min + unit);
                }
            }
        });
        MDButton positiveBtn = dialog.getActionButton(DialogAction.POSITIVE);
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetThreshold(dia, 0, (int) seekBar.getCurrentRange()[0]);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 实际进行网络请求调整阈值的方法
     */
    public static void resetThreshold(DeviceInArea mDia, int min, int max) {
//        Snackbar.make(viewPager,"max："+max+" min:"+min,Snackbar.LENGTH_SHORT).show();
        String code;
        if(mDia.getType() == DataTypeEnum.POS_GPS.getIndex()
                || mDia.getType() == DataTypeEnum.POS_BEIDOU.getIndex())  {
            code = DataTypeEnum.SEC.getCode();
        } else {
            code = DataTypeEnum.indexOf(mDia.getType()).getCode();
        }
        String url = UrlHandler.addAlertConfig(mDia.getId(),code);
        Logger.i("url = " + url);
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Looper.prepare();
                Toast.makeText(ContextApplication.getContext(),
                        R.string.edit_successfully, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onError(Exception e) {
//                Looper.prepare();
//                Toast.makeText(ContextApplication.getContext(),
//                        R.string.failed_work, Toast.LENGTH_SHORT).show();
//                Looper.loop();
            }
        };
        RequestBody body = new FormBody.Builder()
                .add("max", String.valueOf(max))
                .add("min", String.valueOf(min))
                .build();
        HttpUtil.sendRequestWithCallback(url, body, listener);
    }

    public static void showCameraListDialog(DeviceInArea deviceInArea, final Context context) {
        DataTypeEnum dataTypeEnum = DataTypeEnum.indexOf(deviceInArea.getType());
        assert dataTypeEnum != null;
        String url = UrlHandler.addAlertConfig(deviceInArea.getId(), dataTypeEnum.getCode());
        RequestBody body = new FormBody.Builder()
                .add("max", "0")
                .add("min", "0")
                .build();
        HttpUtil.sendRequestWithCallback(url, body, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "设置成功。", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "设置失败。", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
