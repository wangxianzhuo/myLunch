package com.github.xianzhuo.myLunch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Gson helper.
 *
 * @author Yang XuePing
 */
public class GsonHelper {
    public static final Gson DEFAULT_GSON = new GsonBuilder()
            .serializeNulls()
            .excludeFieldsWithoutExposeAnnotation()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
}
