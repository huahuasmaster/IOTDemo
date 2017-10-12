package administrator.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

import administrator.adapters.MsgAdapter;
import administrator.adapters.listener.SwipeItemCallbackListener;
import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.entity.AlertDto;


/**
 * Created by Administrator on 2017/7/14.
 * "消息"页面
 */

public class MessageFragment extends Fragment {

    private RecyclerView rv;
    private MsgAdapter adapter = new MsgAdapter();
    private SwipeItemCallbackListener listener;
    private List<AlertDto> alertDtos = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public MessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MineFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        rv = (RecyclerView)v.findViewById(R.id.smr);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        DividerItemDecoration decoration = new DividerItemDecoration(
                getContext(),DividerItemDecoration.VERTICAL);
        adapter.setAlertDtos(alertDtos);
        adapter.setListener(listener);
        rv.setAdapter(adapter);
        rv.setLayoutManager(manager);
        rv.addItemDecoration(decoration);

        listener = new SwipeItemCallbackListener() {
            @Override
            public void onDelete(int position) {
                Snackbar.make(rv,"点击了删除",Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onEdit(int position) {

            }
        };
        getAlertOnline(-1L);

        return v;
    }

    public void getAlertOnline(long spaceId) {
        Logger.i("尝试请求alert");
        String url = "";
        if(spaceId != -1L) {
            url = UrlHandler.getAlert(spaceId);
        } else {
            url = UrlHandler.getAlertByDefault();
        }
        HttpCallbackListener listener = new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                alertDtos = new Gson().fromJson(response,
                        new TypeToken<List<AlertDto>>(){}.getType());
                Logger.i(""+alertDtos.size());
                adapter.setAlertDtos(alertDtos);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Snackbar.make(rv,"请求告警信息失败",Snackbar.LENGTH_SHORT).show();
            }
        };
        HttpUtil.sendRequestWithCallback(url,listener);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
