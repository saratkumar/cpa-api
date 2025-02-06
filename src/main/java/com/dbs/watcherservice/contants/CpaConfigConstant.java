package com.dbs.watcherservice.contants;

import okhttp3.MediaType;

public class CpaConfigConstant {

    public enum JOB_STATUS {
        completed,
        pending
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

}


