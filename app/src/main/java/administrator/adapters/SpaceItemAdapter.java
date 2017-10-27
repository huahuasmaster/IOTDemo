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
import administrator.entity.SpaceDto;

/**
 * Created by zhuang_ge on 2017/8/7.
 */

public class SpaceItemAdapter extends RecyclerView.Adapter{

    private List<SpaceDto> spaceList;
    private SwipeItemCallbackListener listener;

    public SwipeItemCallbackListener getListener() {
        return listener;
    }

    public void setListener(SwipeItemCallbackListener listener) {
        this.listener = listener;
    }

    public List<SpaceDto> getSpaceList() {
        return spaceList;
    }

    public void setSpaceList(List<SpaceDto> spaceList) {
        this.spaceList = spaceList;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView spaceName;
        private ImageView editName;
        private TextView deleteSpace;
        public ViewHolder(View itemView) {
            super(itemView);
            spaceName = (TextView)itemView.findViewById(R.id.space_name);
            editName = (ImageView)itemView.findViewById(R.id.edit_name);
            deleteSpace = (TextView)itemView.findViewById(R.id.right_menu);
            if(listener != null) {
                deleteSpace.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onDelete(getAdapterPosition());
                    }
                });
                editName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onEdit(getAdapterPosition());
                    }
                });
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.space_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.spaceName.setText(
                spaceList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return spaceList.size();
    }
}
