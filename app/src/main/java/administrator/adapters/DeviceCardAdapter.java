package administrator.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuang_ge on 2017/8/5.
 */

public class DeviceCardAdapter extends PagerAdapter{
    private Context context;
    private List<View> views;
    private final String TAG = "devicecard";

    //    private List<>
    //// TODO: 2017/8/5 此为测试方法，后续应删除
    public DeviceCardAdapter(Context context) {
        this.context = context;

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setList(List<View> views) {
        this.views = views;
    }

    @Override public int getCount() {
        return views.size();
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    //在此处进行view的赋值
    @Override public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }
}
