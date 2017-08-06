package administrator.ui;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.List;

import administrator.adapters.DeviceCardAdapter;

public class RoomDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewPager viewPager;
    private LayoutInflater inflater;
    private Button goCheckBtn;
    public static final int DEAULT_OFFSCEEN_LIMIT = 3;
    private List<View> viewList = new ArrayList<>();
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        viewPager = (ViewPager) findViewById(R.id.device_pager);
        initViews();
        DeviceCardAdapter adapter = new DeviceCardAdapter(this);
        adapter.setList(viewList);
        viewPager.setAdapter(adapter);
        viewPager.setPageMargin(50);
        viewPager.setOffscreenPageLimit(DEAULT_OFFSCEEN_LIMIT);
    }

    private void initViews() {
        inflater = LayoutInflater.from(this);
        for(int i = 0;i <= 3;i++){
            viewList.add(inflater.inflate(R.layout.device_card_item,null));
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_detail_btn:
                int position = viewPager.getCurrentItem();
                Intent intent = new Intent(this,DeviceDetailActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            case R.id.go_back:
                finish();
        }
    }
}
