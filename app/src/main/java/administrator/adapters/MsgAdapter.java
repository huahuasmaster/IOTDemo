package administrator.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qrcodescan.R;

import org.w3c.dom.Text;

import java.util.List;

import administrator.adapters.listener.SwipeItemCallbackListener;
import administrator.entity.AlertDto;
import administrator.enums.AlertTypeEnum;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zhuang_ge on 2017/10/12.
 */

public class MsgAdapter extends RecyclerView.Adapter{

    private List<AlertDto> alertDtos;

    private SwipeItemCallbackListener listener;

    public List<AlertDto> getAlertDtos() {
        return alertDtos;
    }

    public void setAlertDtos(List<AlertDto> alertDtos) {
        this.alertDtos = alertDtos;
    }

    public SwipeItemCallbackListener getListener() {
        return listener;
    }

    public void setListener(SwipeItemCallbackListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msg_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        AlertDto alertDto = alertDtos.get(position);
        viewHolder.deleteTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDelete(position);
            }
        });
        viewHolder.redPoint
                .setVisibility(alertDto.getReadTime() == null ? View.VISIBLE : View.INVISIBLE);
        viewHolder.dateTxt.setText(alertDto.getAlertTime());
        String content = null;
        AlertTypeEnum alertType = AlertTypeEnum.indexOf(alertDto.getAlertType());
        switch (alertType) {
            case TMP_HIGH:content = "("+alertDto.getOtherName()+")温度过高，请通风。";break;
            case TMP_LOW:content = "("+alertDto.getOtherName()+")温度过低，请注意保暖。";break;
            case HUMIDITY_HIGH:content = "("+alertDto.getOtherName()+")湿度过低";break;
            case HUMIDITY_LOW:content = "("+alertDto.getOtherName()+")湿度过高";break;
        }
        viewHolder.title.setText((alertType.getTitle()));
        viewHolder.content.setText(content);
        viewHolder.icon.setImageResource(R.drawable.ic_warnning_circle);
    }

    @Override
    public int getItemCount() {
        return alertDtos.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView icon;
        private TextView title;
        private TextView content;
        private TextView dateTxt;
        private ImageView redPoint;
        private TextView deleteTxt;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (CircleImageView)itemView.findViewById(R.id.icon_msg);
            title = (TextView)itemView.findViewById(R.id.title_msg);
            content = (TextView)itemView.findViewById(R.id.content_msg);
            dateTxt = (TextView)itemView.findViewById(R.id.date);
            redPoint = (ImageView)itemView.findViewById(R.id.unread_point);
            deleteTxt = (TextView)itemView.findViewById(R.id.right_menu);
        }
    }


}
