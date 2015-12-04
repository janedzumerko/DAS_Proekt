package com.dians.theexp.auth;

/**
 * Created by k1ko on 12/4/15.
 */
public class Singleton {

    public String username = null;

    private static Singleton mInstance = null;

    protected Singleton() {
    }

    public static synchronized Singleton getInstance() {
        if (null == mInstance) {
            mInstance = new Singleton();
        }
        return mInstance;
    }
}
