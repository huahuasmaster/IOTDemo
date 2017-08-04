package administrator.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuang_ge on 2017/8/4.
 * 侧滑菜单中空间卡片的房间标签适配器
 */

public class RoomLabelsAdapter extends RecyclerView.Adapter{

    List<String> roomList = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView roomName;

        public ViewHolder(View itemView) {
            super(itemView);
            roomName = (TextView) itemView.findViewById(R.id.room_name);
        }
    }
    public void setRoomList(List<String> list) {
        this.roomList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder mHolder = (ViewHolder)holder;
        mHolder.roomName.setText(roomList.get(position));
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }
}
