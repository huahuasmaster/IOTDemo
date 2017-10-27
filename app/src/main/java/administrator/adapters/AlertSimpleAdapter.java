package administrator.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qrcodescan.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import administrator.adapters.listener.SwipeItemCallbackListener;
import administrator.base.AlertToMsgUtil;
import administrator.entity.AlertDto;
import administrator.enums.AlertTypeEnum;
import administrator.enums.DataTypeEnum;

/**
 * Created by zhuang_ge on 2017/10/27.
 */

public class AlertSimpleAdapter extends RecyclerView.Adapter{
    private final boolean removeProcessedAlert = true;
    private List<AlertDto> alertDtos;
    private SwipeItemCallbackListener listener;
    private Context context;

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
        if(removeProcessedAlert) {
            alertDtos.removeAll(alertForRemove);
        }
        this.alertDtos = alertDtos;
    }

    public SwipeItemCallbackListener getListener() {
        return listener;
    }

    public void setListener(SwipeItemCallbackListener listener) {
        this.listener = listener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alert_simple_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        AlertDto alertDto = alertDtos.get(position);
        final ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.title.setText(AlertTypeEnum.indexOf(alertDto.getAlertType()).getTitle()
                +"("+alertDto.getAlertValue()
                + AlertToMsgUtil.getUnit(DataTypeEnum.indexOf(alertDto.getDataType()))+")");
        viewHolder.date.setText(alertDto.getAlertTime());
        viewHolder.processBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDelete(position);
                viewHolder.processBtn.setEnabled(false);//按钮设为不可按
            }
        });
    }

    @Override
    public int getItemCount() {
        return alertDtos.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;
        Button processBtn;
        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.alert_simple_title);
            date = (TextView)itemView.findViewById(R.id.alert_simple_date);
            processBtn = (Button)itemView.findViewById(R.id.alert_simple_confirm);
        }
    }
}
