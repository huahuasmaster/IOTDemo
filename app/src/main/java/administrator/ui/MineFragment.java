package administrator.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.qrcodescan.R;

import java.io.File;
import java.io.FileOutputStream;

import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import administrator.entity.PictureDto;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/7/14.
 */
public class MineFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    @BindView(R.id.space_manage_linear)
    LinearLayout spaceManageLayout;

    LinearLayout deviceManageLayout;

    @BindView(R.id.room_manage_linear)
    LinearLayout roomManageLayout;

    LinearLayout thresholdSetLayout;
    LinearLayout msgSetLayout;
    LinearLayout deviceLinkSetLayout;

    @BindView(R.id.head_img)
    ImageView headIcon;

    ImageView edit;
    ImageView exit;

    @BindView(R.id.user_name)
    TextView name;

    SharedPreferences sp;

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
        ButterKnife.bind(this, v);
        sp = getActivity().getSharedPreferences("login_data", Context.MODE_PRIVATE);
        initViews(v);

        spaceManageLayout.setOnClickListener(this);
        roomManageLayout.setOnClickListener(this);
        return v;
    }

    private void initViews(View view) {
        name.setText(sp.getString("name", "神秘用户"));
        HttpUtil.sendRequestWithCallback(UrlHandler.getIcon(),
                new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        final PictureDto pictureDto = new Gson().fromJson(response, PictureDto.class);
                        if (pictureDto == null) {
                            Log.i("icon", "没有数据");
                        } else if (pictureDto.getContent() != null) {
                            try {
                                byte[] bytes = pictureDto.getContent().getBytes();
                                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        headIcon.setImageBitmap(bitmap);
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

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
                Intent intent = new Intent(getActivity(), SpaceManageActivity.class);
                startActivity(intent);
                break;
            case R.id.room_manage_linear:
                startActivity(new Intent(getActivity(), AreaManageActivity.class));
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

