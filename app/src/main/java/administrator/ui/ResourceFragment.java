package administrator.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.activity.CaptureActivity;
import com.lichfaker.log.Logger;
import com.qrcodescan.R;


import org.eclipse.paho.client.mqttv3.MqttException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import administrator.adapters.AreaCardAdapter;
import administrator.adapters.DevicePreviewAdapter;
import administrator.adapters.SpaceCardAdapter;
import administrator.adapters.listener.DeviceCardCallbackListener;
import administrator.base.CommonUtil;
import administrator.base.DeviceCodeUtil;
import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.adapters.listener.SpaceCardCallbackListener;
import administrator.base.mqtt.MqttManager;
import administrator.base.mqtt.MqttMsgBean;
import administrator.entity.AreaCurValue;
import administrator.entity.DeviceCurValue;
import administrator.entity.DeviceInArea;
import administrator.entity.SpaceWithAreas;
import administrator.enums.DataTypeEnum;
import administrator.view.MyRecyclerView;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * “资源”页面
 */
public class ResourceFragment extends Fragment {

    private SharedPreferences spaceDataSp;
    private SharedPreferences.Editor spaceDataSpEditor;
    private SharedPreferences loginDataSp;
    private SharedPreferences.Editor loginDataSpEditor;

    private SlidingTabLayout tabLayout;
    private ViewPager viewPager;
    private LayoutInflater inflater;
    private View previewPage, devicePage;
    private ImageView gotoScan;
    private ImageView spaceSelector;
    private DrawerLayout drawerLayout;
    private String result = "蛇皮";
    private LinearLayoutManager manager;


    private String[] titles = {"预览", "设备"};
    private List<String> titleList = new ArrayList<>();
    private List<View> viewList = new ArrayList<>();

    private final int INTERVAL_SECOND = 2;

    //打开扫描界面请求码
    public static int REQUEST_CODE = 0x01;
    //扫描成功返回码
    public static int RESULT_OK = 0xA1;

    //以下是对预览页面的初始化定义
    private TextView previewTitle;
    private ImageView roomBg;
    private MyRecyclerView areaCardsRV;
    private List<AreaCurValue> acvList = new ArrayList<>();
    private List<DeviceInArea> diaList = new ArrayList<>();
    private AreaCardAdapter areaCardAdapter;
    private MaterialDialog waitDialog;
    private DeviceCardCallbackListener deviceCardListener;
    private SwipeRefreshLayout previewSwiper;

    //以下是对设备页面的初始化定义
    private TextView deviceTitle;

    //以下是对侧滑菜单的初始化定义
    private ImageView head;//头像
    private FloatingActionButton addSpace;//新增空间按钮
    private RecyclerView spaceCardsRV;//空间列表
    private TextView userName;//用户名称
    private SpaceCardAdapter spaceCardAdapter;
    private List<SpaceWithAreas> swaList = new ArrayList<>();
    private SpaceCardCallbackListener spaceOnClickListener;
    private SwipeRefreshLayout sideSwiper;


    private OnFragmentInteractionListener mListener;

    public ResourceFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ResourceFragment newInstance(String param1) {
        ResourceFragment fragment = new ResourceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_resource, null);

        EventBus.getDefault().register(this);

        manager = new LinearLayoutManager(getContext());

        spaceDataSp = getActivity().getSharedPreferences("space_data", Context.MODE_PRIVATE);
        spaceDataSpEditor = spaceDataSp.edit();

        loginDataSp = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        loginDataSpEditor = loginDataSp.edit();

        tabLayout = (SlidingTabLayout) v.findViewById(R.id.tabs);
        viewPager = (ViewPager) v.findViewById(R.id.vp);
        previewPage = inflater.inflate(R.layout.page_preview, null);
        devicePage = inflater.inflate(R.layout.page_device, null);

        //向容器中填装view
        viewList.add(previewPage);
        viewList.add(devicePage);

        //初始化标题
        titleList = Arrays.asList(titles);

        //设置适配器
        MyPagerAdapter adapter = new MyPagerAdapter(viewList);
        viewPager.setAdapter(adapter);

        //将tabLayout与viewPager关联起来
        tabLayout.setViewPager(viewPager);

        //对各个视图的内部进行初始化
        initPreview(previewPage);
        initDevice(devicePage);
        initSideMenu(v);
        //对其它view执行初始化
        gotoScan = (ImageView) v.findViewById(R.id.gotoScan);
        gotoScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtil.isCameraCanUse()) {
//                    Intent intent = new Intent(getActivity(), ScannerActivity.class);
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    Toast.makeText(getContext(), "请打开此应用的摄像头权限！"
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
        gotoScan.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getActivity(), SmartConfigActivity.class);
                startActivity(intent);
                return false;
            }
        });

        drawerLayout = (DrawerLayout) v.findViewById(R.id.drawer);
        spaceSelector = (ImageView) v.findViewById(R.id.ex_space_text);
        spaceSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        spaceSelector.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getContext(),
                        "mqtt连接状态:"+MqttManager.getInstance().isConnecting(),
                        Toast.LENGTH_SHORT).show();
                MqttManager.getInstance().publish(MqttManager.getDeviceDataTopic(),0,
                        new String("Hello world").getBytes());
                return false;
            }
        });

        return v;
    }

    /**
     * 预览页面，所有内部组件的初始化都在此方法内完成
     */
    private void initPreview(View v) {
        areaCardsRV = (MyRecyclerView) v.findViewById(R.id.area_cards);
        previewSwiper = (SwipeRefreshLayout)v.findViewById(R.id.swipe_preview);

        previewSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initAreaCardsBySpaceId(-1L);
            }
        });
        areaCardAdapter = new AreaCardAdapter();
        areaCardAdapter.setContext(getContext());

        waitDialog = new MaterialDialog.Builder(getContext())
                .title(getResources()
                        .getString(R.string.getting_data))
                .content(getResources()
                        .getString(R.string.plz_wait))
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .build();

        deviceCardListener = new DeviceCardCallbackListener() {
            @Override
            public void onBack() {

            }

            @Override
            public void onThreshold(int position) {

            }

            @Override
            public void onCheck(int position) {
                Intent intent = new Intent(
                        getContext(), DeviceDetailActivity.class);
                intent.putExtra("device_id", diaList.get(position).getId());
                intent.putExtra("data_type", diaList.get(position).getType());
                startActivity(intent);
            }
        };
        //获取默认空间信息
        initAreaCardsBySpaceId(-1L);

    }

    /**
     * 通过空间id请求房间以及内里设备的预览信息
     *
     * @param spaceId
     */
    private void initAreaCardsBySpaceId(long spaceId) {
        //如果spaceid为-1 则请求默认空间
        final String url = spaceId != -1L ?
                UrlHandler.getAreaWithInnerDevicePreviewUrl(spaceId)
                : UrlHandler.getAreaWithDeviceOfDefaultSpace(loginDataSp.getLong("user_id", -1L));
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //获取完整json 手动解析
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getBoolean("success")) {
                        acvList = new Gson().fromJson(json.getString("mainData"),
                                new TypeToken<List<AreaCurValue>>() {
                                }.getType());

                        diaList = new Gson().fromJson(json.getString("minorData"),
                                new TypeToken<List<DeviceInArea>>() {
                                }.getType());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                areaCardAdapter.setAreaList(acvList);
                                areaCardAdapter.setDiaList(diaList);
                                areaCardAdapter.setListener(deviceCardListener);
                                areaCardsRV.setAdapter(areaCardAdapter);
                                areaCardsRV.setLayoutManager(new LinearLayoutManager(getContext()));
                                if (waitDialog.isShowing()) {
                                    waitDialog.dismiss();
                                }
                                if(previewSwiper.isRefreshing()) {
                                    previewSwiper.setRefreshing(false);
                                }
                            }
                        });
                    }

                } catch (JSONException e) {

                }
            }

            @Override
            public void onError(Exception e) {
                Snackbar.make(viewPager, "请求房间列表出错", Snackbar.LENGTH_SHORT).show();
            }
        };
        waitDialog.show();
        HttpUtil.sendRequestWithCallback(url, listener, true);
    }

    /**
     * 初始化设备page页面
     *
     * @param v
     */
    private void initDevice(View v) {
    }

    /**
     * 通过网络请求 以及本地已有信息 填充侧边菜单卡片信息
     *
     * @param v
     */
    private void initSideMenu(View v) {
        head = (ImageView) v.findViewById(R.id.side_menu_head_img);
        spaceCardsRV = (RecyclerView) v.findViewById(R.id.space_card_recycler);
        addSpace = (FloatingActionButton) v.findViewById(R.id.add_space);
        userName = (TextView) v.findViewById(R.id.user_name);
        sideSwiper = (SwipeRefreshLayout)v.findViewById(R.id.swipe_side_menu);

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
                initAreaCardsBySpaceId(spaceId);
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
                    Snackbar.make(viewPager, "服务器出错", Snackbar.LENGTH_SHORT).show();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spaceCardAdapter = new SpaceCardAdapter();
                        spaceCardAdapter.setListener(spaceOnClickListener);
                        spaceCardAdapter.setSwaList(swaList);
                        spaceCardAdapter.setContext(getContext());
                        spaceCardsRV.setAdapter(spaceCardAdapter);
                        spaceCardsRV.setLayoutManager(manager);
                        if(sideSwiper.isRefreshing()) {
                            sideSwiper.setRefreshing(false);
                        }
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                Looper.prepare();
                Toast.makeText(getContext(), "请求空间数据失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        };

        HttpUtil.sendRequestWithCallback(UrlHandler.getSpaceWithAreasListUrl(), listener);


    }

    @Override
    public void onStart() {
        super.onStart();
        //动态注册广播接收器
//
//        IntentFilter intentFilter =
//                new IntentFilter("administrator.service.WsnDataService");//过滤器,保证只收到想要的信息
//        WsnDataBroadcastRecevier recevier = new WsnDataBroadcastRecevier();
//        getActivity().registerReceiver(recevier, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 通过eventbus接受mqtt消息 进行处理
     *
     * @param msgBean
     */
    @Subscribe(priority = 99)
    public void onEvent(MqttMsgBean msgBean) {
        Looper.prepare();
//        Toast.makeText(getContext(),
//                "资源页面->mqtt主题:" + msgBean.getMainTopic()
//                        + " mqtt信息:" + msgBean.getMqttMessage().toString()
//                , Toast.LENGTH_SHORT)
//                .show();
        Logger.e("资源页面->mqtt主题:" + msgBean.getMainTopic()
                        + " mqtt信息:" + msgBean.getMqttMessage().toString());
        String mainTopic = msgBean.getMainTopic();
        String msg = msgBean.getMqttMessage().toString();
        //如果mqtt消息主题为data,则动态修改页面内的信息
        if (mainTopic.equals(MqttMsgBean.DATA)) {
            Map<String, String> dataMap = msgBean.getDataMap();
            Set<String> keySet = dataMap.keySet();
            for(String key : keySet) {
                String code = DeviceCodeUtil.getCode(key);
                updatePreviewByMqtt(key,code,dataMap.get(key));
            }
        }

        Looper.loop();
    }

    /**
     * 对譬如“sn1$23#60%”这样一条数据
     * 1)找到对应的preview适配器
     * 2)根据code拆分原始数据 修改原适配器里的数据
     * 3)刷新原适配器
     *
     * @param sn
     * @param code
     * @param value
     */
    private void updatePreviewByMqtt(String sn, String code, String value) {
        final DevicePreviewAdapter adapter = areaCardAdapter.findPreviewAdapterBySn(sn);
        String[] codes = code.split("#");
        String[] values = value.split("#");
        for (int i = 0; i < codes.length && i < values.length; i++) {
            for(DeviceCurValue dcv : adapter.getDcvList()) {
                //匹配数据，匹配成功则修改
                try {
                    if (dcv.getSn().equals(sn)
                            && dcv.getType() == DataTypeEnum.findByCode(codes[i]).getIndex()) {
                        dcv.setCurValue(values[i]);
                    }
                } catch (NullPointerException e) {
                }
            }
        }
        //在修改完所有数据后 刷新适配器
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void changeText(String text) {
        Toast.makeText(getContext(), "扫描到的信息为" + text, Toast.LENGTH_SHORT)
                .show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class WsnDataBroadcastRecevier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "收到了消息", Toast.LENGTH_SHORT).show();
        }
    }

    //ViewPager适配器
    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();//页卡数
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//官方推荐写法
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));//添加页卡
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));//删除页卡
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);//页卡标题
        }

    }

}
