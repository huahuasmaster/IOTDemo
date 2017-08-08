package administrator.base;

/**
 * 滑动删除功能的回调函数接口
 * Created by zhuang_ge on 2017/8/7.
 */

public interface SwipeItemCallbackListener {
    void onDelete(int position);
    void onEdit(int position);
}
