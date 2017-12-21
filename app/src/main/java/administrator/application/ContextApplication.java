package administrator.application;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import org.litepal.LitePalApplication;

/**
 * Created by zhuang_ge on 2017/8/23.
 */

public class ContextApplication extends Application{

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePalApplication.initialize(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    public static Context getContext(){
        return context;
    }
}
