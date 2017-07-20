package administrator.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.List;

import administrator.smartconfig.esptouch.EsptouchTask;
import administrator.smartconfig.esptouch.IEsptouchResult;


public class SmartConfigActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener {
    private static final String SSID_PASSWORD = "ssid_password";

    private SharedPreferences mShared;

    //以下是对toolbar的初始化
    private ImageView back;
    private TextView title;
    //展示当前所选WIFI
//    private TextView mCurrentSsidTV;
    //用于选择wifi 的下拉列表
    private Spinner mConfigureSP;
    //显示wifi ssid
//    private EditText mSsidET;
    //显示密码
    private EditText mPasswordET;
    private CheckBox mShowPasswordCB;
    private CheckBox mIsSsidHiddenCB;
    private Button mDeletePasswordBtn;
    private Button mConfirmBtn;
    private WifiManager wifimanager;

    private BaseAdapter mWifiAdapter;
    private volatile List<ScanResult> mScanResultList;
    private volatile List<String> mScanResultSsidList;

    private String mCurrentSSID;

    private String ssidForSend;
    private String passwordForSend;


    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
            {
                updateCurrentConnectionInfo();
            }
        }

    };
    //（选择WiFi后）更新视图
    private void updateCurrentConnectionInfo()
    {
        wifimanager=(WifiManager)getSystemService(WIFI_SERVICE);
        WifiInfo wifiinfo=wifimanager.getConnectionInfo();

        mCurrentSSID = wifiinfo.getSSID();
        if (mCurrentSSID == null)
        {
            mCurrentSSID = "";
        }
//        mCurrentSsidTV.setText(getString(R.string.esp_esptouch_current_ssid, mCurrentSSID));

        if (!TextUtils.isEmpty(mCurrentSSID))
        {
            scanWifi();
            mWifiAdapter.notifyDataSetChanged();
            for (int i = 0; i < mScanResultList.size(); i++)
            {
                String ssid = mScanResultList.get(i).SSID;
                if (ssid.equals(mCurrentSSID))
                {
                    mConfigureSP.setSelection(i);
                    break;
                }
            }
        }
        else
        {
            mPasswordET.setText("");
        }
    }
    //扫描wifi 将结果添加至下拉框
    private void scanWifi() {
        wifimanager.startScan();
        mScanResultList=wifimanager.getScanResults();
        mScanResultSsidList.clear();
        for(ScanResult scanResult : mScanResultList)
        {
            mScanResultSsidList.add(scanResult.SSID);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_config_activity);
        mShared = getSharedPreferences(SSID_PASSWORD, Context.MODE_PRIVATE);

        mConfigureSP = (Spinner)findViewById(R.id.esptouch_configure_wifi);
        mPasswordET = (EditText)findViewById(R.id.esptouch_pwd);
        mShowPasswordCB = (CheckBox)findViewById(R.id.esptouch_show_pwd);
        mIsSsidHiddenCB = (CheckBox)findViewById(R.id.esptouch_isSsidHidden);
        mDeletePasswordBtn = (Button)findViewById(R.id.esptouch_delete_pwd);
        mConfirmBtn = (Button)findViewById(R.id.esptouch_confirm);

        mShowPasswordCB.setOnCheckedChangeListener(this);
        mDeletePasswordBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);

        mScanResultList = new ArrayList<ScanResult>();
        mScanResultSsidList = new ArrayList<String>();
        mWifiAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, mScanResultSsidList);
        mConfigureSP.setAdapter(mWifiAdapter);
        mConfigureSP.setOnItemSelectedListener(this);

        back = (ImageView)findViewById(R.id.scanner_toolbar_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title = (TextView)findViewById(R.id.scanner_toolbar_title);
        title.setText(R.string.esp_esptouch_title);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, filter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //“显示密码”的checkbox监听器
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == mShowPasswordCB)
        {
            if (b)
            {
                mPasswordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
            else
            {
                mPasswordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        }

    }

    private class ConfigureTask extends AsyncTask<String, Void, IEsptouchResult> implements DialogInterface.OnCancelListener
    {
        private Activity mActivity;

        private ProgressDialog mDialog;

        private EsptouchTask mTask;

        private final String mSsid;

        public ConfigureTask(Activity activity, String apSsid, String apBssid, String password, boolean isSsidHidden)
        {
            mActivity = activity;
            mSsid = apSsid;
            mTask = new EsptouchTask(apSsid, apBssid, password, isSsidHidden, SmartConfigActivity.this);
        }

        //在正式执行请求之前
        @Override
        protected void onPreExecute()
        {
            mDialog = new ProgressDialog(mActivity);
            mDialog.setMessage(getString(R.string.esp_esptouch_configure_message, mSsid));
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setOnCancelListener(this);
            mDialog.show();
        }

        @Override
        protected IEsptouchResult doInBackground(String... params)
        {
            return mTask.executeForResult();
        }

        @Override
        protected void onPostExecute(IEsptouchResult result)
        {
            mDialog.dismiss();

            int toastMsg;
            if (result.isSuc())
            {
                toastMsg = R.string.esp_esptouch_result_suc;
            }
            else if (result.isCancelled())
            {
                toastMsg = R.string.esp_esptouch_result_cancel;
            }
            else if (mCurrentSSID.equals(mSsid))
            {
                toastMsg = R.string.esp_esptouch_result_failed;
            }
            else
            {
                toastMsg = R.string.esp_esptouch_result_over;
            }

            Toast.makeText(mActivity, toastMsg, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel(DialogInterface dialog)
        {
            if (mTask != null)
            {
                mTask.interrupt();
                Toast.makeText(mActivity, R.string.esp_esptouch_result_cancel, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        //如果按下了确认按钮
        if (view == mConfirmBtn)
        {
            if (!TextUtils.isEmpty(mCurrentSSID))
            {
                String ssid = ssidForSend;
                String password = mPasswordET.getText().toString();
                mShared.edit().putString(ssid, password).apply();
                boolean isSsidHidden = mIsSsidHiddenCB.isChecked();
                // find the bssid is scanList
                String bssid = scanApBssidBySsid(ssid);
                if (bssid == null)
                {
                    Toast.makeText(this, getString(R.string.esp_esptouch_cannot_find_ap_hing, ssid), Toast.LENGTH_LONG)
                            .show();
                }
                else
                {
                    //若成功找到路由，则进行访问
                    new ConfigureTask(this, ssid, bssid, password, isSsidHidden).execute();
                }
            }
            else
            {
                Toast.makeText(this, R.string.esp_esptouch_connection_hint, Toast.LENGTH_LONG).show();
            }
        }

        //如果按下了删除密码按钮
        else if (view == mDeletePasswordBtn)
        {
            String selectionSSID = mConfigureSP.getSelectedItem().toString();
            if (!TextUtils.isEmpty(selectionSSID))
            {
                mShared.edit().remove(selectionSSID).apply();
                mPasswordET.setText("");
            }
        }

    }

    //根据ssid扫描附近路由
    private String scanApBssidBySsid(String ssid) {
        if (TextUtils.isEmpty(ssid))
        {
            return null;
        }
        String bssid = null;
        for (int retry = 0; bssid == null && retry < 3; retry++)
        {
            scanWifi();
            for (ScanResult scanResult : mScanResultList)
            {
                if (scanResult.SSID.equals(ssid))
                {
                    bssid = scanResult.BSSID;
                    return bssid;
                }
            }
        }
        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String ssid = mScanResultList.get(i).SSID;
        String password = mShared.getString(ssid, "");
        mPasswordET.setText(password);
//        mSsidET.setText(ssid);
        passwordForSend = password;
        ssidForSend = ssid;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
