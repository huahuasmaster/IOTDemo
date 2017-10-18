package administrator.adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lichfaker.log.Logger;
import com.qrcodescan.R;

import java.util.ArrayList;
import java.util.List;

import administrator.adapters.listener.SwipeItemCallbackListener;
import administrator.base.AlertToMsgUtil;
import administrator.entity.AlertDto;
import administrator.enums.AlertTypeEnum;
import administrator.enums.DataTypeEnum;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zhuang_ge on 2017/10/12.
 */

public class MsgAdapter extends RecyclerView.Adapter{

    private List<AlertDto> alertDtos;

    private SwipeItemCallbackListener listener;

    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<AlertDto> getAlertDtos() {
        return alertDtos;
    }

    public void setAlertDtos(List<AlertDto> alertDtos) {
        List<AlertDto> alertForRemove = new ArrayList<>();

        for(int i = 0;i < alertDtos.size();i++) {
            if(alertDtos.get(i).getProcessTime() != null) {
                alertForRemove.add(alertDtos.get(i));
            }
        }
//        Logger.i("alertDtosForRemove.size"+alertForRemove.size());
        alertDtos.removeAll(alertForRemove);
//        Logger.i("alertDtos.size()"+alertDtos.size());
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
        final ViewHolder viewHolder = (ViewHolder)holder;
        final AlertDto alertDto = alertDtos.get(position);
        final Context mConText = context;
        String unit = AlertToMsgUtil.getUnit(DataTypeEnum.indexOf(alertDto.getDataType()));
        viewHolder.deleteTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDelete(position);
            }
        });
        viewHolder.redPoint
                .setVisibility(alertDto.getReadTime() == null ? View.VISIBLE : View.INVISIBLE);
        viewHolder.dateTxt.setText(alertDto.getAlertTime());
        AlertTypeEnum alertType = AlertTypeEnum.indexOf(alertDto.getAlertType());
        String content = AlertToMsgUtil.getContent(alertType,alertDto,unit);
        viewHolder.title.setText(alertType.getTitle());
        viewHolder.content.setText(content);
        viewHolder.icon.setImageResource(R.drawable.ic_warnning_circle);
        viewHolder.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.redPoint.setVisibility(View.INVISIBLE);
                listener.onMain(position);
            }
        });
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
        private ConstraintLayout back;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (CircleImageView)itemView.findViewById(R.id.icon_msg);
            title = (TextView)itemView.findViewById(R.id.title_msg);
            content = (TextView)itemView.findViewById(R.id.content_msg);
            dateTxt = (TextView)itemView.findViewById(R.id.date);
            redPoint = (ImageView)itemView.findViewById(R.id.unread_point);
            deleteTxt = (TextView)itemView.findViewById(R.id.right_menu);
            back = (ConstraintLayout) itemView.findViewById(R.id.background);
        }
    }


}
