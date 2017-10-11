package administrator.ui;

import android.content.Intent;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.activity.CaptureActivity;
import com.qrcodescan.R;

import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.entity.DeviceDto;
import administrator.enums.DeviceTypeEnum;

public class TypeSnActivity extends AppCompatActivity {
    private EditText editText;
    private Button nextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_num);

        editText = (EditText) findViewById(R.id.number);
        nextBtn = (Button)findViewById(R.id.button);

        nextBtn.setEnabled(false);
        editText.addTextChangedListener(new numberChangeedListener());
    }

    private void handleText(){
        String text = editText.getText().toString();
//        Snackbar.make(nextBtn,text,Snackbar.LENGTH_SHORT).show();
        if(text.length() >= 6) {
//            Log.d("lalala", "handleText: "+text);
            nextBtn.setEnabled(true);
        }
    }

    private void checkSn(final String sn) {
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if (response != null && !response.equals("")) {
                    DeviceDto deviceDto = new Gson().fromJson(response, DeviceDto.class);
                    Looper.prepare();
//                    Toast.makeText(CaptureActivity.this,""+deviceDto.getDeviceModelDto().getType(),Toast.LENGTH_SHORT)
//                            .show();
                    if (deviceDto.getSn().equals(sn)) {
                        //判断设备类型 跳转至不同页面
                        DeviceTypeEnum deviceTypeEnum = DeviceTypeEnum.stateOf(
                                deviceDto.getDeviceModelDto().getType());
                        if (deviceTypeEnum != null) {
                            Intent intent = new Intent();
                            switch (deviceTypeEnum) {
                                case GATEWAY:
                                    intent.setClass(TypeSnActivity.this,
                                            GateOnlineActivity.class);
                                    break;
                                case NODE:
                                    intent.setClass(TypeSnActivity.this,
                                            DeviceOnlineActivity.class);
                                    break;
                            }
                            intent.putExtra("device_sn", sn);
                            intent.putExtra("device_dto", deviceDto);
                            startActivity(intent);
                        } else {
                            Toast.makeText(TypeSnActivity.this, R.string.unknown_device,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    Looper.loop();
                }
            }

            @Override
            public void onError(Exception e) {
                Looper.prepare();
                Toast.makeText(TypeSnActivity.this, "出错，请检查网络", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        };
        String url = UrlHandler.getDeviceDetailDescBySn(sn);

        HttpUtil.sendRequestWithCallback(url, listener);
    }

    private class numberChangeedListener implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            handleText();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            handleText();
        }

        @Override
        public void afterTextChanged(Editable s) {
            handleText();
        }
    }
}
