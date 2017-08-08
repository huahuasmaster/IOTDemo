package administrator.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qrcodescan.R;


/**
 * Created by Administrator on 2017/7/14.
 */
public class MineFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;

    private LinearLayout spaceManageLayout;
    private LinearLayout deviceManageLayout;
    private LinearLayout roomManageLayout;
    private LinearLayout thresholdSetLayout;
    private LinearLayout msgSetLayout;
    private LinearLayout deviceLinkSetLayout;
    private ImageView headIcon;
    private ImageView edit;
    private ImageView exit;

    public MineFragment() {
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
    public static MineFragment newInstance(String param1) {
        MineFragment fragment = new MineFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mine, null);

        initViews(v);

        spaceManageLayout.setOnClickListener(this);
        roomManageLayout.setOnClickListener(this);
        return v;
    }

    private void initViews(View view) {
        spaceManageLayout = (LinearLayout)view.findViewById(R.id.space_manage_linear);
        roomManageLayout = (LinearLayout)view.findViewById(R.id.room_manage_linear);
    }

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.space_manage_linear:
                Intent intent = new Intent(getActivity(),SpaceManageActivity.class);
                startActivity(intent);
                break;
            case R.id.room_manage_linear:
                startActivity(new Intent(getActivity(),RoomManageActivity.class));
                break;
        }
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

