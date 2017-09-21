package administrator.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.qrcodescan.R;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.logging.Handler;

import administrator.base.ViewFindUtils;
import administrator.base.mqtt.MqttManager;
import administrator.base.mqtt.MqttMsgBean;
import administrator.entity.TabEntity;


public class MainActivity extends AppCompatActivity implements ResourceFragment.OnFragmentInteractionListener{
    private Context mContext = this;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private Handler mHandler;
    //初始化底部标题
    private String[] mTitles = {"资源","消息","我的"};
    //初始化底部显示图标
    private int[] mIconUnselectIds = {R.mipmap.resource_un,
            R.mipmap.message_un,R.mipmap.mine_un};
    private int[] mIconSelectIds = {R.mipmap.resource,
            R.mipmap.message,R.mipmap.mine};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private CommonTabLayout tabLayout;
    private View mDecorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //装填fragment
        mFragments.add(ResourceFragment.newInstance(mTitles[0]));
        mFragments.add(MessageFragment.newInstance(mTitles[1]));
        mFragments.add(MineFragment.newInstance(mTitles[2]));

        //装填底部tab
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }

        //获取当前最顶层view
        mDecorView = getWindow().getDecorView();
        //初始化tablayout
        tabLayout = ViewFindUtils.find(mDecorView,R.id.bottom);
        tabLayout.setTabData(mTabEntities,this,R.id.fl_change,mFragments);
        tabLayout.setIconGravity(Gravity.TOP);
        //设置图标大小与字体大小
        tabLayout.setTextsize(12.0F);
        tabLayout.setIconHeight(24.0F);
        tabLayout.setIconWidth(24.0F);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //开启mqtt连接
                MqttManager.getInstance().creatConnect();
                //订阅主题
                MqttManager.getInstance().subscribe();
                //循环队列
                MqttManager.getInstance().startQueue();
            }
        }).start();

    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == ResourceFragment.RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("qr_scan_result");
            //显示扫描到的信息
//            Snackbar.make(tabLayout,"获取到二维码信息："+scanResult,Snackbar.LENGTH_SHORT).show();
            //根据系统版本的不同选择getSupportFragmentManager或者getFragmentManager
           // ResourceFragment fragment = (ResourceFragment) getSupportFragmentManager()
            //                            .findFragmentById(R.id.fl_change);
           // fragment.changeText(scanResult);
            ResourceFragment fragment = (ResourceFragment)mFragments.get(0);
            fragment.changeText("二维码内容："+scanResult);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MqttManager.getInstance().disConnect();
                }catch (MqttException e){

                }
            }
        }).start();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }

    protected int dp2px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}


