package administrator.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lichfaker.log.Logger;
import com.qrcodescan.R;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import administrator.adapters.SpaceCardAdapter;
import administrator.adapters.listener.SpaceCardCallbackListener;
import administrator.base.ViewFindUtils;
import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.base.mqtt.MqttManager;
import administrator.base.mqtt.MqttMsgBean;
import administrator.entity.SpaceWithAreas;
import administrator.entity.TabEntity;
import okhttp3.FormBody;
import okhttp3.RequestBody;


public class MainActivity extends AppCompatActivity implements ResourceFragment.OnFragmentInteractionListener {
    private Context mContext = this;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    //初始化底部标题
    private String[] mTitles = {"资源", "消息", "我的"};
    //初始化底部显示图标
    private int[] mIconUnselectIds = {R.mipmap.resource_un,
            R.mipmap.message_un, R.mipmap.mine_un};
    private int[] mIconSelectIds = {R.mipmap.resource,
            R.mipmap.message, R.mipmap.mine};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private CommonTabLayout tabLayout;
    private View mDecorView;

    private SharedPreferences loginDataSp;
    private SharedPreferences.Editor loginDataSpEditor;

    //以下是对侧滑菜单的初始化定义
    private ImageView head;//头像
    private FloatingActionButton addSpace;//新增空间按钮
    private RecyclerView spaceCardsRV;//空间列表
    private SpaceCardAdapter spaceCardAdapter;
    private List<SpaceWithAreas> swaList = new ArrayList<>();
    private SpaceCardCallbackListener spaceOnClickListener;
    private SwipeRefreshLayout sideSwiper;
    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginDataSp = getSharedPreferences("login_data", Context.MODE_PRIVATE);
        loginDataSpEditor = loginDataSp.edit();

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
        tabLayout = ViewFindUtils.find(mDecorView, R.id.bottom);
        tabLayout.setTabData(mTabEntities, this, R.id.fl_change, mFragments);
        tabLayout.setIconGravity(Gravity.TOP);
        //设置图标大小与字体大小
        tabLayout.setTextsize(12.0F);
        tabLayout.setIconHeight(25.0F);
        tabLayout.setIconWidth(25.0F);

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
        initSideMenu();
    }

    /**
     * 通过网络请求 以及本地已有信息 填充侧边菜单卡片信息
     */
    private void initSideMenu() {
        head = (ImageView) findViewById(R.id.side_menu_head_img);
        spaceCardsRV = (RecyclerView) findViewById(R.id.space_card_recycler);
        addSpace = (FloatingActionButton) findViewById(R.id.add_space);
        TextView userName = (TextView) findViewById(R.id.user_name);
        sideSwiper = (SwipeRefreshLayout) findViewById(R.id.swipe_side_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);


        userName.setText(loginDataSp.getString("name", "神秘用户"));

        sideSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initSpaceCards();
            }
        });

        spaceOnClickListener = new SpaceCardCallbackListener() {
            @Override
            public void onChooseSpace(long spaceId) {
                //在选择了某个空间后 关闭侧滑菜单 主页显示对应的空间信息
                drawerLayout.closeDrawer(Gravity.START);
                ((ResourceFragment) mFragments.get(0)).initAreaCardsBySpaceId(spaceId);
                ((MessageFragment) mFragments.get(1)).getAlertOnline(spaceId);
            }

            @Override
            public void onChooseArea(long areaId) {

            }

            @Override
            public void onClickSwitch(long spaceId, final Switch mSwicth, boolean checked) {
                //离家->1 归家->0
                short type = checked ? (short) 1 : (short) 0;

                RequestBody body = new FormBody.Builder()
                        .add("type", String.valueOf(type))
                        .build();
                String url = UrlHandler.postChangeModelTypeBySpaceId(spaceId);
                HttpCallbackListener listener = new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        //修改成功
                        if (response.equals("1")) {
                            Snackbar.make(mSwicth, "修改成功", Snackbar.LENGTH_SHORT).show();
                        } else {
                            mSwicth.setChecked(!mSwicth.isChecked());
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        //如果出错 switch恢复原来的状态
                        mSwicth.setChecked(!mSwicth.isChecked());
                    }
                };
                HttpUtil.sendRequestWithCallback(url, body, listener);
            }
        };
        initSpaceCards();
    }

    /**
     * 实际进行侧边栏卡片刷新的方法
     */
    private void initSpaceCards() {

        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //将json转换为对象
                try {
                    swaList = new Gson().fromJson(response,
                            new TypeToken<List<SpaceWithAreas>>() {
                            }.getType());
                } catch (Exception e) {
                    Snackbar.make(drawerLayout, "服务器出错", Snackbar.LENGTH_SHORT).show();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spaceCardAdapter = new SpaceCardAdapter();
                        spaceCardAdapter.setListener(spaceOnClickListener);
                        spaceCardAdapter.setSwaList(swaList);
                        spaceCardAdapter.setContext(MainActivity.this);
                        spaceCardsRV.setAdapter(spaceCardAdapter);
                        spaceCardsRV.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        if (sideSwiper.isRefreshing()) {
                            sideSwiper.setRefreshing(false);
                        }
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                Looper.prepare();
                Toast.makeText(MainActivity.this, "请求空间数据失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        };

        HttpUtil.sendRequestWithCallback(UrlHandler.getSpaceWithAreasListUrl(), listener);


    }

    public void setMsgUnreadDot(final int count) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (count > 1) {
                    tabLayout.showMsg(1, count);
                } else if (count == 1) {
                    tabLayout.showDot(1);
                } else if(count < 1) {
                    tabLayout.hideMsg(1);
                }
                tabLayout.setMsgMargin(1,0,dp2px(1.85f));
            }
        });

    }

    public void openDraw() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ResourceFragment.RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("qr_scan_result");
            //显示扫描到的信息
//            Snackbar.make(tabLayout,"获取到二维码信息："+scanResult,Snackbar.LENGTH_SHORT).show();
            //根据系统版本的不同选择getSupportFragmentManager或者getFragmentManager
            // ResourceFragment fragment = (ResourceFragment) getSupportFragmentManager()
            //                            .findFragmentById(R.id.fl_change);
            // fragment.changeText(scanResult);
            ResourceFragment fragment = (ResourceFragment) mFragments.get(0);
            fragment.changeText("二维码内容：" + scanResult);
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
                } catch (MqttException e) {

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


