package me.liangdi.zaoshu.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by liangdi on 6/27/17.
 */
public abstract class AbstractApi implements Api{
    protected KeyPair keyPair;
    protected Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Override
    public void init(KeyPair keyPair) {
        this.keyPair = keyPair;
    }
}
