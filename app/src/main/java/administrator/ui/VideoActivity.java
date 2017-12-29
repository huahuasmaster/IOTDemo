package administrator.ui;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezvizuikit.open.EZUIError;
import com.ezvizuikit.open.EZUIKit;
import com.ezvizuikit.open.EZUIPlayer;
import com.qrcodescan.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Calendar;

import administrator.base.http.HttpCallbackListener;
import administrator.base.http.HttpUtil;
import administrator.base.http.UrlHandler;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class VideoActivity extends AppCompatActivity {
    @BindView(R.id.player_ui)
    EZUIPlayer mPlayer;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.tool_bar)
    CardView cardView;

    @BindView(R.id.more)
    ImageView moreImg;

    String appkey = "6297f134ef1c4ff082a3f48eeef75759";

    String appSecret = "c6c973226c34ebd9d5d8b13ec0bc0f61";

    String accessToken = "at.1ajc5sos6hpwkhq15ep7emlj2i43lop6-1scae8jrjt-1oi4m0d-0p9nfuljz";

    String roomName;

    @SuppressLint("AuthLeak")
    String url = "ezopen://AES:5XUKkqbfnrhFSxkHb5U_NA@open.ys7.com/664242854/1.hd.live";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        ButterKnife.bind(this);

        roomName = getIntent().getStringExtra("room_name");

        findViewById(R.id.go_back_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ((TextView) findViewById(R.id.room_name)).setText(roomName);
        //初始化EZUIKit
        EZUIKit.initWithAppKey(getApplication(), appkey);


        //设置播放回调callback
        mPlayer.setCallBack(new EZUIPlayer.EZUIPlayerCallBack() {
            @Override
            public void onPlaySuccess() {

            }

            @Override
            public void onPlayFail(EZUIError ezuiError) {

            }

            @Override
            public void onVideoSizeChange(int i, int i1) {

            }

            @Override
            public void onPrepared() {

            }

            @Override
            public void onPlayTime(Calendar calendar) {

            }

            @Override
            public void onPlayFinish() {

            }
        });

        //设置播放参数
        mPlayer.setUrl(url);

        //开始播放
        mPlayer.startPlay();

        RequestBody body = new FormBody.Builder()
                .add("appKey", appkey)
                .add("appSecret", appSecret)
                .build();
        HttpUtil.sendRequestWithCallback(UrlHandler.getVideoAccessToken(),
                body, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String data = jsonObject.getString("data");
                            String token = new JSONObject(data).getString("accessToken");
                            EZUIKit.setAccessToken(token);
                            Log.i("video", "使用了动态token");
                            mPlayer.startPlay();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(VideoActivity.this, "获取token出错", Toast.LENGTH_SHORT).show();
                                EZUIKit.setAccessToken(accessToken);
                                mPlayer.startPlay();
                            }
                        });
                    }
                }, true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断当前屏幕方向
                if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    //切换到竖屏
                    Log.d("fab","切换到竖屏");
                    fab.show();
                    cardView.setVisibility(View.VISIBLE);
                    VideoActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    constraintSet.clear(mPlayer.getId());
//                    constraintSet.connect(mPlayer.getId(),ConstraintSet.LEFT,ConstraintSet.PARENT_ID,ConstraintSet.LEFT);
//                    constraintSet.connect(mPlayer.getId(),ConstraintSet.RIGHT,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT);
//                    constraintSet.connect(mPlayer.getId(),ConstraintSet.TOP,ConstraintSet.PARENT_ID,ConstraintSet.TOP);
//                    constraintSet.applyTo(constraintLayout);

                }else{
                    //切换到横屏
                    VideoActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    fab.hide();
                    cardView.setVisibility(View.GONE);
                    Log.d("fab","切换到横屏");
//                    constraintSet.connect(mPlayer.getId(),ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM);
//                    constraintSet.applyTo(constraintLayout);
                }

            }
        });

//        moreImg.setOnClickListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlayer.stopPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.releasePlayer();
    }

    @Override
    public void onBackPressed() {
        if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            Log.d("fab","切换到竖屏");
            fab.show();
            cardView.setVisibility(View.VISIBLE);
            VideoActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPlayer.startPlay();
    }
}
