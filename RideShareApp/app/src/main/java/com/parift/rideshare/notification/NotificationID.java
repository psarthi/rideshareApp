package com.parift.rideshare.notification;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by psarthi on 2/16/18.
 */

public class NotificationID {
    private final static AtomicInteger c = new AtomicInteger(0);
    public static int getID() {
        return c.incrementAndGet();
    }
}
