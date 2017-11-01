package administrator.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.List;

import administrator.base.DeviceCodeUtil;
import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.entity.UserDto;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button loginBtn;
    private MaterialDialog waitForLoginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("login_data", MODE_PRIVATE);
        editor = sp.edit();

        initViews();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        loginBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(LoginActivity.this,IpConfigActivity.class);
                startActivity(intent);
                return false;
            }
        });

        accountEdit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(LoginActivity.this,GateOnlineActivity.class);
                startActivity(intent);
                return false;
            }
        });


        checkPermission();
    }

    private void checkPermission() {
        //检查权限，动态申请未给予的权限
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(LoginActivity.this, permissions, 1);
        }
    }
    private void initViews() {
        accountEdit = (EditText) findViewById(R.id.account_edit);
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        loginBtn = (Button) findViewById(R.id.login_btn);
        accountEdit.requestFocus();

        //填充上次登录的账号密码
        String preAccount = sp.getString("account","");
        String prePassword = sp.getString("password","");
        accountEdit.setText(preAccount);
        passwordEdit.setText(prePassword);


        waitForLoginDialog = new MaterialDialog.Builder(this)
                .title(getResources()
                        .getString(R.string.be_logging_in))
                .content(getResources()
                        .getString(R.string.plz_wait))
                .progress(true,0)
                .progressIndeterminateStyle(false)
                .build();
    }

    private void login() {
        final String account = accountEdit.getText().toString();
        final String password = passwordEdit.getText().toString();

        waitForLoginDialog.show();

        RequestBody body = new FormBody.Builder()
                            .add("account",account)
                            .add("password",password)
                            .build();
        String url = UrlHandler.getLoginUrl();
        HttpUtil.sendRequestWithCallback(url, body, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {

                UserDto userDto = new UserDto();
                if(response.equals("") || response == null) {
                    Snackbar.make(loginBtn,"登录失败，请检查账号密码",Snackbar.LENGTH_SHORT).show();
                } else {
                    userDto = new Gson().fromJson(response,UserDto.class);
                    //记住账号密码
                    editor.putString("account",userDto.getLoginName())
                            .putString("name",userDto.getName())
                            .putLong("user_id",userDto.getId())
                            .putString("password",password);
                    editor.apply();
                    DeviceCodeUtil.getMapOnline();//获取code-sn映射
                    UrlHandler.setUserId(userDto.getId());
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onError(Exception e) {
                Snackbar.make(loginBtn,"网络请求失败！",Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
}
