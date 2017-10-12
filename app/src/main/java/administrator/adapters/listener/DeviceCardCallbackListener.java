package administrator.adapters.listener;

import administrator.entity.DeviceInArea;

/**
 * Created by zhuang_ge on 2017/8/8.
 * 设备卡片 点击事件回调函数接口
 */

public interface DeviceCardCallbackListener {
    /**
     * 点击了返回按钮
     */
    void onBack();

    /**
     * 点击了阈值设置按钮
     */
    void onThreshold(DeviceInArea dia);

    /**
     * 点击了查看详情按钮
     * @param position
     */
    void onCheck(int position);
}
