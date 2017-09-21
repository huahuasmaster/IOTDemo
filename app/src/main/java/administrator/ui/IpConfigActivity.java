package administrator.ui;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qrcodescan.R;

import administrator.application.ContextApplication;
import administrator.base.http.UrlHandler;

public class IpConfigActivity extends AppCompatActivity {

    private EditText newIpEdit;
    private Button checkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_config);



        newIpEdit = (EditText)findViewById(R.id.new_ip);
        newIpEdit.setText(UrlHandler.getIp());
        checkBtn = (Button)findViewById(R.id.check);

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UrlHandler.setIp(newIpEdit.getText().toString());

                Toast.makeText(IpConfigActivity.this,"新ip:"+UrlHandler.getIp(),Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        });

        checkBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                UrlHandler.setIp("121.40.140.223");
                Toast.makeText(IpConfigActivity.this,"新ip:"+UrlHandler.getIp(),Toast.LENGTH_SHORT)
                        .show();
                finish();
                return false;
            }
        });
    }
}
