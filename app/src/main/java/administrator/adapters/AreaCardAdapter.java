package administrator.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.List;

import administrator.entity.AreaCurValue;
import administrator.ui.AreaDetailActivity;

/**
 * Created by zhuang_ge on 2017/8/18.
 * 资源-预览页面 房间卡片的适配器
 */

public class AreaCardAdapter extends RecyclerView.Adapter{

    private List<AreaCurValue> areaList;

    private Context context;

    //用于储存卡片内预览值列表的适配器 方便调用、刷新预览数据
    //对特定布局刷新 而不是整个列表刷新 可以有效减少程序开销
    private List<DevicePreviewAdapter> previewAdapterList;

    public AreaCardAdapter() {
        super();
        previewAdapterList = new ArrayList<>();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<AreaCurValue> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<AreaCurValue> areaList) {
        this.areaList = areaList;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        //房间背景图片
        ImageView areaBack;
        //房间名称
        TextView areaName;
        //设备预览数据列表
        RecyclerView curValueRV;

        public ViewHolder(View itemView) {
            super(itemView);

            areaBack = (ImageView) itemView.findViewById(R.id.room_bg);
            areaName = (TextView) itemView.findViewById(R.id.area_card_area_name);
            curValueRV = (RecyclerView) itemView.findViewById(R.id.area_card_inner_device_rv);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.area_card_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        DevicePreviewAdapter adapter = new DevicePreviewAdapter();
        previewAdapterList.add(adapter);

        final AreaCurValue mAcv = areaList.get(position);
        adapter.setContext(context);
        adapter.setDcvList(mAcv.getDeviceCurValueList());
        adapter.setAreaId(mAcv.getAreaId());
        //填充房间名
        viewHolder.areaName.setText(mAcv.getName());
        //添加点击事件
        viewHolder.areaName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNextActivity(mAcv.getAreaId());
            }
        });
        viewHolder.areaBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goNextActivity(mAcv.getAreaId());
            }
        });
        //展示设备预览数据
        viewHolder.curValueRV.setAdapter(adapter);
        viewHolder.curValueRV.setLayoutManager(
                new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
    }

    private void goNextActivity(long areaId) {
        Intent intent = new Intent(context, AreaDetailActivity.class);
        intent.putExtra("area_id",areaId);
        context.startActivity(intent);
    }
    @Override
    public int getItemCount() {
        return areaList.size();
    }
}
