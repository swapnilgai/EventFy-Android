package com.java.eventfy.EventBus;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by swapnil on 6/4/16.
 */
public class EventBusService extends EventBus {

    private static final EventBusService eventBusObj = new EventBusService();

    private EventBusService(){};

    public static EventBus getInstance() {
        return  eventBusObj.getDefault();
    }

}
