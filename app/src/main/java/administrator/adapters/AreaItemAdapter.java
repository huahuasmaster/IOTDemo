package administrator.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qrcodescan.R;

import java.util.List;

import administrator.adapters.listener.SwipeItemCallbackListener;
import administrator.entity.AreaDto;

/**
 * Created by zhuang_ge on 2017/8/8.
 * 房间管理页面，房间列表适配器
 */

public class AreaItemAdapter extends RecyclerView.Adapter {

    private List<AreaDto> roomList;

    private SwipeItemCallbackListener listener;

    public List<AreaDto> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<AreaDto> roomList) {
        this.roomList = roomList;
    }

    public SwipeItemCallbackListener getListener() {
        return listener;
    }

    public void setListener(SwipeItemCallbackListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView roomName;
        private ImageView editName;
        private TextView deleteRoom;

        public ViewHolder(View itemView) {
            super(itemView);

            roomName = (TextView)itemView.findViewById(R.id.room_name);
            editName = (ImageView)itemView.findViewById(R.id.edit_name);
            deleteRoom = (TextView)itemView.findViewById(R.id.right_menu);

            editName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onEdit(getAdapterPosition());
                }
            });

            deleteRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDelete(getAdapterPosition());
                }
            });
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.room_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.roomName.setText(
                roomList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }
}
