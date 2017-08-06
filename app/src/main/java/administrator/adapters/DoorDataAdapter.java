package administrator.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qrcodescan.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import administrator.entity.UniversalData;

/**
 * Created by zhuang_ge on 2017/8/6.
 */

public class DoorDataAdapter extends RecyclerView.Adapter{

    List<UniversalData<Boolean>> doorData = new ArrayList<>();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public List<UniversalData<Boolean>> getDoorData() {
        return doorData;
    }

    public void setDoorData(List<UniversalData<Boolean>> doorData) {
        this.doorData = doorData;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView doorImg;
        TextView dateText;
        public ViewHolder(View itemView) {
            super(itemView);
            doorImg = (ImageView)itemView.findViewById(R.id.data_icon);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_data_icon_item,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        boolean isOpen = doorData.get(position).getData();
        String time = sdf.format(doorData.get(position).getDate());
        viewHolder.doorImg
                .setImageResource(isOpen ?
                        R.drawable.ic_door_open : R.drawable.ic_door_close);
    }

    @Override
    public int getItemCount() {
        return doorData.size();
    }
}
