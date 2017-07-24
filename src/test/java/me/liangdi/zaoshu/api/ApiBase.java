package me.liangdi.zaoshu.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by liangdi on 6/27/17.
 */
public class ApiBase {
    protected String secret = "90bdfe7a832c54c62175364e527f6bf43a663cdab3b6a9b545273f2208b73d69";
    protected String apiKey = "76a509404e0247359f03f12d5933211d";
    protected Gson gson = new GsonBuilder().setPrettyPrinting().create();

}
