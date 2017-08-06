package administrator.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.qrcodescan.R;

import java.util.List;

import administrator.ui.DeviceDetailActivity;

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

    public List<View> getViews() {
        return views;
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

    //在此处进行view的赋值,点击事件等
    @Override public Object instantiateItem(ViewGroup container, final int position) {
        View view = views.get(position);
        //为两个按钮添加点击事件
        Button checkDetailBtn = (Button)view.findViewById(R.id.check_detail_btn);
        checkDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,DeviceDetailActivity.class);
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });

        Button goBackBtn = (Button)view.findViewById(R.id.go_back);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity)context).finish();
            }
        });
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }
}
