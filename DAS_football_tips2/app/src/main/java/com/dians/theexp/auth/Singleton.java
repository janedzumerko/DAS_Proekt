package com.dians.theexp.auth;

/**
 * Created by k1ko on 12/4/15.
 */

/*
 * Singleton pattern implementation
 */
public class Singleton {

    // variables saved across the application
    // needed for accessing the server
    public String username = null;
    public Integer userId = -1;

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
