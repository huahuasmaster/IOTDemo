package administrator.adapters.listener;

import android.widget.Switch;

/**
 * Created by zhuang_ge on 2017/8/22.
 */

public interface SpaceCardCallbackListener {
    /**
     * 当选择了某个卡片的背景后
     * @param spaceId
     */
    void onChooseSpace(long spaceId);

    /**
     * 当选择了某个安装位置后
     * @param areaId
     */
    void onChooseArea(long areaId);

    /**
     * 当点击了switch后
     */
    void onClickSwitch(long spaceId, Switch mSwitch,boolean checked);
}
