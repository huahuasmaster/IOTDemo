package administrator.ui;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;

import com.ezvizuikit.open.EZUIError;
import com.ezvizuikit.open.EZUIKit;
import com.ezvizuikit.open.EZUIPlayer;
import com.qrcodescan.R;

import java.util.Calendar;

public class VideoActivity extends AppCompatActivity {

    private EZUIPlayer mPlayer;
    private FloatingActionButton fab;

    String appkey = "6297f134ef1c4ff082a3f48eeef75759";

    String accessToken = "at.1ajc5sos6hpwkhq15ep7emlj2i43lop6-1scae8jrjt-1oi4m0d-0p9nfuljz";

    @SuppressLint("AuthLeak")
    String url = "ezopen://AES:5XUKkqbfnrhFSxkHb5U_NA@open.ys7.com/664242854/1.hd.live";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        //获取EZUIPlayer实例
        mPlayer = (EZUIPlayer) findViewById(R.id.player_ui);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        //初始化EZUIKit
        EZUIKit.initWithAppKey(getApplication(), appkey);

        //设置授权token
        EZUIKit.setAccessToken(accessToken);


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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断当前屏幕方向
                if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    //切换到竖屏
                    Log.d("fab","切换到竖屏");
                    fab.setVisibility(View.VISIBLE);

                    VideoActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    constraintSet.clear(mPlayer.getId());
//                    constraintSet.connect(mPlayer.getId(),ConstraintSet.LEFT,ConstraintSet.PARENT_ID,ConstraintSet.LEFT);
//                    constraintSet.connect(mPlayer.getId(),ConstraintSet.RIGHT,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT);
//                    constraintSet.connect(mPlayer.getId(),ConstraintSet.TOP,ConstraintSet.PARENT_ID,ConstraintSet.TOP);
//                    constraintSet.applyTo(constraintLayout);

                }else{
                    //切换到横屏
                    VideoActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    fab.setVisibility(View.INVISIBLE);

                    Log.d("fab","切换到横屏");
//                    constraintSet.connect(mPlayer.getId(),ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM);
//                    constraintSet.applyTo(constraintLayout);
                }

            }
        });
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
            fab.setVisibility(View.VISIBLE);
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
