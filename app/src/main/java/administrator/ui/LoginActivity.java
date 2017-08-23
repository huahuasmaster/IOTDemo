package administrator.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.qrcodescan.R;

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
    }

    private void login() {
        final String account = accountEdit.getText().toString();
        final String password = passwordEdit.getText().toString();

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

}
