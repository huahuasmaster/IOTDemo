package administrator.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.qrcodescan.R;

import java.util.List;

import administrator.entity.AreaDto;
import administrator.entity.Room;

/**
 * Created by zhuang_ge on 2017/8/4.
 * 侧滑菜单中空间卡片的房间标签适配器
 */

public class AreaLabelAdapter {

    private List<AreaDto> areaDtoList;

    private Context mContext;

    private FlowLayout mFlowLayout;

    /**
     * 将子view添加至流动布局以内
     */
    public void addLabels() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for(AreaDto area : areaDtoList) {
            View v = inflater.inflate(R.layout.room_label_item,null);

            CardView labelBack = (CardView)v.findViewById(R.id.label_back);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                                  ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(4,4,4,4);
            labelBack.setLayoutParams(lp);
            TextView roomName = (TextView) (v.findViewById(R.id.room_name));
            roomName.setText(area.getName());
            mFlowLayout.addView(v);
        }
    }

    public AreaLabelAdapter(List<AreaDto> roomList, FlowLayout mFlowLayout,Context mContext) {
        this.areaDtoList = roomList;
        this.mFlowLayout = mFlowLayout;
        this.mContext = mContext;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public AreaLabelAdapter() {
    }

    public List<AreaDto> getAreaDtoList() {
        return areaDtoList;
    }

    public void setAreaDtoList(List<AreaDto> areaDtoList) {
        this.areaDtoList = areaDtoList;
    }
}


