package administrator.ui;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.activity.CaptureActivity;
import com.qrcodescan.R;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import administrator.adapters.AreaCardAdapter;
import administrator.adapters.SpaceCardAdapter;
import administrator.base.CommonUtil;
import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.entity.AreaCurValue;
import administrator.entity.SpaceWithAreas;

/**
 * “资源”页面
 */
public class ResourceFragment extends Fragment {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private SlidingTabLayout tabLayout;
    private ViewPager viewPager;
    private LayoutInflater inflater;
    private View previewPage, devicePage;
    private ImageView gotoScan;
    private Button fadingTextView;
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
    private RecyclerView areaCardsRV;
    private List<AreaCurValue> acvList = new ArrayList<>();
    private AreaCardAdapter areaCardAdapter;

    //以下是对设备页面的初始化定义
    private TextView deviceTitle;

    //以下是对侧滑菜单的初始化定义
    private ImageView head;//头像
    private FloatingActionButton addSpace;//新增空间按钮
    private RecyclerView spaceCardsRV;//空间列表
    private TextView userName;//用户名称
    private SpaceCardAdapter spaceCardAdapter;
    private List<SpaceWithAreas> swaList = new ArrayList<>();



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

        manager = new LinearLayoutManager(getContext());

        sp = getActivity().getSharedPreferences("space_data",Context.MODE_PRIVATE);
        editor = sp.edit();

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
        fadingTextView = (Button)v.findViewById(R.id.ex_space_text);
        fadingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


         //主页面初始化完毕，接下来进行侧滑菜单的初始化


        return v;
    }

    /**
     * 预览页面，所有内部组件的初始化都在此方法内完成
     */
    private void initPreview(View v) {
        areaCardsRV = (RecyclerView) v.findViewById(R.id.area_cards);
        areaCardAdapter = new AreaCardAdapter();
        areaCardAdapter.setContext(getContext());

        //获取默认空间id
        int defaultSpaceId = sp.getInt("default_space_id",-1);
        //如果本地没有保存默认空间id则进行网络请求获取
        if(defaultSpaceId == -1) {
            // TODO: 2017/8/18 替换为网络请求
            defaultSpaceId = 1;
            editor.putInt("default_space_id",defaultSpaceId);
            editor.apply();
        }

        initAreaCardsBySpaceId(defaultSpaceId);
    }

    private void initAreaCardsBySpaceId(int spaceId) {
        final String url = UrlHandler.getAreaWithInnerDevicePreviewUrl(spaceId);
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                acvList = new Gson().fromJson(response,
                        new TypeToken<List<AreaCurValue>>(){}.getType());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        areaCardAdapter.setAreaList(acvList);
                        areaCardsRV.setAdapter(areaCardAdapter);
                        areaCardsRV.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Snackbar.make(viewPager,"请求房间列表出错",Snackbar.LENGTH_SHORT).show();
            }
        };

        HttpUtil.sendRequestWithCallback(url,listener);
    }

    private void initDevice(View v) {
    }

    private void initSideMenu(View v) {
        head = (ImageView) v.findViewById(R.id.head_img);
        spaceCardsRV = (RecyclerView)v.findViewById(R.id.space_card_recycler);
        addSpace = (FloatingActionButton)v.findViewById(R.id.add_space);
        userName = (TextView)v.findViewById(R.id.user_name);

        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //将json转换为对象
                try {
                    swaList = new Gson().fromJson(response,
                            new TypeToken<List<SpaceWithAreas>>() {
                            }.getType());
                } catch (Exception e) {
                    Snackbar.make(viewPager,"服务器出错",Snackbar.LENGTH_SHORT).show();
                }

                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        initSpaceCards();
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                Looper.prepare();
                Toast.makeText(getContext(),"请求空间数据失败",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        };

        HttpUtil.sendRequestWithCallback(UrlHandler.getSpaceWithAreasListUrl(),listener);
    }

    private void initSpaceCards() {
        spaceCardAdapter = new SpaceCardAdapter();
        spaceCardAdapter.setSwaList(swaList);
        spaceCardAdapter.setContext(getContext());
        spaceCardsRV.setAdapter(spaceCardAdapter);
        spaceCardsRV.setLayoutManager(manager);

    }

    public void changeText(String text) {
        Toast.makeText(getContext(),"扫描到的信息为"+text,Toast.LENGTH_SHORT)
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
