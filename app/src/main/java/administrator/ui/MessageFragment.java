package administrator.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lichfaker.log.Logger;
import com.qrcodescan.R;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import administrator.adapters.MsgAdapter;
import administrator.adapters.listener.SwipeItemCallbackListener;
import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.base.mqtt.MqttMsgBean;
import administrator.entity.AlertDto;


/**
 * Created by Administrator on 2017/7/14.
 * "消息"页面
 */

public class MessageFragment extends Fragment {

    private RecyclerView rv;
    private SwipeRefreshLayout srl;
    private MsgAdapter adapter = new MsgAdapter();
    private SwipeItemCallbackListener listener;
    private List<AlertDto> alertDtos = new ArrayList<>();

    private OnFragmentInteractionListener mListener;
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public MessageFragment() {
        // Required empty public constructor
    }

    public static MessageFragment newInstance(String param1) {
        MessageFragment fragment = new MessageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message, null);
        rv = (RecyclerView) v.findViewById(R.id.smr);
        srl = (SwipeRefreshLayout) v.findViewById(R.id.srl);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        DividerItemDecoration decoration = new DividerItemDecoration(
                getContext(), DividerItemDecoration.VERTICAL);

        listener = new SwipeItemCallbackListener() {
            @Override
            public void onDelete(final int position) {
                final AlertDto alertDto = adapter.getAlertDtos().get(position);
                String url = UrlHandler.processAlert(alertDto.getId(), new Date().getTime());
                HttpCallbackListener listener = new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        Logger.i("标注确认成功");
                    }

                    @Override
                    public void onError(Exception e) {
                        Logger.i("标注确认失败");
                    }
                };
                HttpUtil.sendRequestWithCallback(url, listener);
                //在ui中删除
                adapter.getAlertDtos().remove(position);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyItemRangeRemoved(position, 1);
                    }
                });
                //刷新未读计数
                if (alertDto.getReadTime() == null) {
                    setUnreadDot(adapter.getAlertDtos());
                }
            }

            @Override
            public void onEdit(int position) {

            }

            //点击某个消息后执行的事件
            @Override
            public void onMain(final int position) {
                final AlertDto alertDto = adapter.getAlertDtos().get(position);
                Intent intent = new Intent(
                        getContext(), DeviceDetailActivity.class);
                intent.putExtra("device_id", alertDto.getDeviceId());
                intent.putExtra("data_type", alertDto.getDataType());
                intent.putExtra("from_alert", true);

                Logger.i(alertDto.getAlertTime());
                if (alertDto.getReadTime() == null) {
                    //点击之后为刷新未读计数
                    final Date date = new Date();
                    adapter.getAlertDtos().get(position).setReadTime(format.format(date));
                    setUnreadDot(adapter.getAlertDtos());

                    //上传至服务器
                    String url = UrlHandler.readAlert(alertDto.getId(), date.getTime());
                    HttpCallbackListener listener = new HttpCallbackListener() {
                        @Override
                        public void onFinish(String response) {
                            Logger.i("标注已读成功");
                        }

                        @Override
                        public void onError(Exception e) {
                            Logger.i("标注已读失败 ");
                        }
                    };
                    HttpUtil.sendRequestWithCallback(url, listener);
                }
                startActivity(intent);
            }
        };
        adapter.setAlertDtos(alertDtos);
        adapter.setContext(getContext());
        adapter.setListener(listener);
        rv.setAdapter(adapter);
        rv.setLayoutManager(manager);
        rv.addItemDecoration(decoration);

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAlertOnline(-1L);
            }
        });
        getAlertOnline(-1L);

        return v;
    }

    public void getAlertOnline(long spaceId) {
        Logger.i("尝试请求alert");
        if(!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        String url = "";
        if (spaceId != -1L) {
            url = UrlHandler.getAlert(spaceId);
        } else {
            url = UrlHandler.getAlertByDefault();
        }
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                alertDtos = new Gson().fromJson(response,
                        new TypeToken<List<AlertDto>>() {
                        }.getType());
                adapter.setAlertDtos(alertDtos);
                setUnreadDot(alertDtos);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        if (srl.isRefreshing()) {
                            srl.setRefreshing(false);
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Snackbar.make(rv, "请求告警信息失败", Snackbar.LENGTH_SHORT).show();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (srl.isRefreshing()) {
                            srl.setRefreshing(false);
                        }
                    }
                });
            }
        };
        HttpUtil.sendRequestWithCallback(url, listener);
    }

    //刷新未读计数
    private void setUnreadDot(List<AlertDto> alertDtos) {
        int count = 0;
        for (AlertDto alertDto : alertDtos) {
            if (alertDto.getReadTime() == null) {
                count++;
            }
        }
//        Logger.i("count = " + count);
        ((MainActivity) getActivity()).setMsgUnreadDot(count);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
