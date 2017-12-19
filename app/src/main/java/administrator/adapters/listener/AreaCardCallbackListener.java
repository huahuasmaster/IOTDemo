package administrator.adapters.listener;

import administrator.entity.AreaCurValue;

public interface AreaCardCallbackListener {
    void onAreaBack(AreaCurValue areaCurValue);

    void onAreaName(AreaCurValue areaCurValue);

    void onLongAreaBack(AreaCurValue areaCurValue);
}
