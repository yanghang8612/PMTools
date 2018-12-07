package com.casc.pmtools;

import java.util.HashMap;
import java.util.Map;

public class MyParams {

    private MyParams() {
    }

    /**
     * AppKey for speech
     */
    public static final String AppId = "10703509";
    public static final String AppKey = "208B855UXS46N1WBR5sNyGCb";
    public static final String AppSecret = "c50303647164111b552b168a8c6fa2c7";

    /**
     * Setting Parameters
     */
    public static final String API_VERSION = "1.4";
    public static final int BODY_CODE_LENGTH = 8;
    public static final int NET_CONNECT_TIMEOUT = 3; // s
    public static final int NET_RW_TIMEOUT = 3; // s
    public static final int CONFIG_UPDATE_INTERVAL = 60; // s
//    public static final String S_MAIN_PLATFORM_ADDR = "http://59.252.100.114"; // 主平台软件地址
    public static final String S_MAIN_PLATFORM_ADDR = "http://192.168.1.18:8089"; // 主平台软件地址
    public static final int TASK_DETAIL_REFRESH_INTERVAL = 3000; // ms

    /**
     * Parameters for testing
     */
    public static final boolean PRINT_JSON = true;
}
