package com.zhiyu.fire.model;

/**
 * Created by Administrator on 2017/6/12 0012.
 * 模型父类
 */

public class ViewModel {

    private EventInterface mEventInterface;

    public ViewModel(EventInterface eventInterface) {
        mEventInterface = eventInterface;
    }

    public ViewModel() {}

    public void mark() {}

    public void setmEventInterface(EventInterface mEventInterface) {
        this.mEventInterface = mEventInterface;
    }

    public final void update(UIMessage message) {
        if (mEventInterface != null) {
            mEventInterface.update(message);
        }
    }

    public interface EventInterface {
        void update(UIMessage message);
    }

}