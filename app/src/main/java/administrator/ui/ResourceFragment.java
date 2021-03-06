package administrator.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.activity.CaptureActivity;
import com.lichfaker.log.Logger;
import com.qrcodescan.R;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import administrator.adapters.AreaCardAdapter;
import administrator.adapters.DevicePreviewAdapter;
import administrator.adapters.listener.AreaCardCallbackListener;
import administrator.adapters.listener.DeviceCardCallbackListener;
import administrator.base.CommonUtil;
import administrator.base.DeviceCodeUtil;
import administrator.base.PictureUtil;
import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.base.mqtt.MqttManager;
import administrator.base.mqtt.MqttMsgBean;
import administrator.entity.AreaCurValue;
import administrator.entity.DeviceCurValue;
import administrator.entity.DeviceInArea;
import administrator.enums.DataTypeEnum;
import administrator.view.MyRecyclerView;

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
    private AreaCardCallbackListener areaCardCallbackListener;
    private SwipeRefreshLayout previewSwiper;

    //以下是对设备页面的初始化定义
    private TextView deviceTitle;

    public static int REQUEST_LOAD_IMAGE = 0x99;
    public static final int ACTION_CROP = 0x98;


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

        spaceSelector = (ImageView) v.findViewById(R.id.ex_space_text);
        spaceSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).openDraw();
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
            public void onThreshold(DeviceInArea dia) {

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

        areaCardCallbackListener = new AreaCardCallbackListener() {
            @Override
            public void onAreaBack(AreaCurValue areaCurValue) {
                Intent intent = new Intent(getContext(), AreaDetailActivity.class);
                intent.putExtra("area_id", areaCurValue.getAreaId());
                getActivity().startActivity(intent);
            }

            @Override
            public void onAreaName(AreaCurValue areaCurValue) {
                onAreaBack(areaCurValue);
            }

            @Override
            public void onLongAreaBack(AreaCurValue areaCurValue) {
                Intent i = new Intent(Intent.ACTION_PICK, android.
                        provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getActivity().startActivityForResult(i, REQUEST_LOAD_IMAGE);
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
    public void initAreaCardsBySpaceId(long spaceId) {
//        if (!previewSwiper.isRefreshing()) {
//            previewSwiper.setRefreshing(true);
//        }
        //如果spaceid为-1 则请求默认空间
        final String url = spaceId != -1L ?
                UrlHandler.getAreaWithInnerDevicePreviewUrl(spaceId)
                : UrlHandler.getAreaWithDeviceOfDefaultSpace(loginDataSp.getLong("user_id", -1L));
        Logger.i("url = "+url);
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //获取完整json 手动解析
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getBoolean("success")) {
                        JSONObject data = new JSONObject(json.get("data").toString());
                        acvList = new Gson().fromJson(data.getString("mainData"),
                                new TypeToken<List<AreaCurValue>>() {
                                }.getType());

                        diaList = new Gson().fromJson(data.getString("minorData"),
                                new TypeToken<List<DeviceInArea>>() {
                                }.getType());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                areaCardAdapter.setAreaList(acvList);
                                areaCardAdapter.setDiaList(diaList);
                                areaCardAdapter.setDeviceCardCallbackListener(deviceCardListener);
                                areaCardAdapter.setAreaCardCallbackListener(areaCardCallbackListener);
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
//        waitDialog.show();
        HttpUtil.sendRequestWithCallback(url, listener, true);
    }

    /**
     * 初始化设备page页面
     *
     * @param v
     */
    private void initDevice(View v) {
    }


    @Override
    public void onStart() {
        super.onStart();
        //动态注册广播接收器

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
                Logger.i("开始更新key为"+key+"的数据");
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
        if(adapter == null) {
            return;
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void setPic(Bitmap bitmap) {
        Log.i("bitmap", "接收到bitmap" + bitmap.toString());
        File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/image");//设置保存路径
        if (!PHOTO_DIR.exists()) {
            Log.i("file", "没有该文件夹");
        }
        File avaterFile = new File(PHOTO_DIR, "avater.jpg");//设置文件名称

        if (avaterFile.exists()) {
            avaterFile.delete();
        }
        try {
            avaterFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(avaterFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Log.i("", "文件写入成功");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap bitmap1 = null;
        try {
            File avaterFile1 = new File(PHOTO_DIR, "avater.jpg");
            if (avaterFile1.exists()) {
                bitmap1 = BitmapFactory.decodeFile(PHOTO_DIR + "/avater.jpg");
            }
        } catch (Exception e) {
        }
        getActivity().getSharedPreferences("image", 0).edit()
                .putBoolean("change", true).apply();
        initAreaCardsBySpaceId(-1L);
    }





}
