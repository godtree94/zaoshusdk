package me.liangdi.zaoshu.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.liangdi.zaoshu.ApiException;
import me.liangdi.zaoshu.ZaoshuClient;
import me.liangdi.zaoshu.model.ApiResult;
import me.liangdi.zaoshu.model.InstanceList;
import me.liangdi.zaoshu.model.RunConfig;
import me.liangdi.zaoshu.model.RunParamConfig;
import sun.misc.Launcher;

/**
 * Created by liangdi on 6/27/17.
 */
@Slf4j
public class AuthorizeTest {

    Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Test
    public void testSignJsonRequest() throws IOException {
        URL[] urls = Launcher.getBootstrapClassPath().getURLs();
        System.out.println(Launcher.getLauncher().getClassLoader().getSystemClassLoader());
        System.out.println(getClass().getClassLoader().getResourceAsStream(""));
        for (int i = 0; i < urls.length; i++) {
            System.out.println(urls[i].toExternalForm());
        }
        System.out.println(System.getProperty("sun.boot.class.path"));
//        String method = "POST";
//        Date now = new Date();
//
//        String gmtDate = Authorize.getDate(now);
//        gmtDate = "Wed, 18 Mar 2016 08:04:06 GMT";
//        log.info("gmtDate:{}",gmtDate);
//        Map<String,String> query  = new HashMap<>();
//        
        RunParamConfig runParamConfig = new RunParamConfig();
        List<RunConfig> runConfigs = new LinkedList<>();
        RunConfig runConfig = new RunConfig();
        List<String> urlList = new LinkedList<>();
        urlList.add("https://www.baidu.com/s?wd=测试&pn={{0-100;10}}");
        urlList.add("https://www.baidu.com/s?wd=eee&pn={{0-100;10}}");
        runConfig.setUrls(urlList);
        runConfig.setMethod("GET");
        Map<String, String> headers = new HashMap<>();
        runConfig.setHeaders(headers);
        runConfigs.add(runConfig);
        runParamConfig.setTarget_sources(runConfigs);

        ZaoshuClient zaoshuClient = ZaoshuClient.getInstance();
        try {
            ApiResult apiResult = zaoshuClient.instance().run("b0135193867b4af9b2df0ebd6740cb82", runParamConfig, "notify?");
            System.out.println(apiResult.getMsg());
        } catch (ApiException ex) {
            Logger.getLogger(AuthorizeTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            System.out.println(zaoshuClient.user().wallet());
        } catch (ApiException ex) {
            Logger.getLogger(AuthorizeTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            System.out.println(zaoshuClient.instance().get("b0135193867b4af9b2df0ebd6740cb82").getData().getStatus());
        } catch (ApiException ex) {
            Logger.getLogger(AuthorizeTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            System.out.println(gson.toJson(zaoshuClient.instance().taskList("b0135193867b4af9b2df0ebd6740cb82")));
        } catch (ApiException ex) {
            Logger.getLogger(AuthorizeTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            InstanceList list = zaoshuClient.instance().list();
            System.out.println(gson.toJson(list));
        } catch (ApiException ex) {
            Logger.getLogger(AuthorizeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            zaoshuClient.instance().getTaskData("b0135193867b4af9b2df0ebd6740cb82", "fc43d19c6f4d4a158677bee59b80d8f8", "csv");
        } catch (ApiException ex) {
            Logger.getLogger(AuthorizeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//    @Test
    public void tesss() throws InterruptedException {
        String pattern = ":[^/]*&";
        String patternEnd = ":[^/]*$";
        String testUrl = "http://localhost:8080/api/test?par=:par&param=:param&aaa=:aaa";
        try {
            URL url=new URL(testUrl);
            String queryPart=url.getQuery();
            String[] queryList=queryPart.split("&");
            for (int i = 0; i < queryList.length; i++) {
                String query = queryList[i];
                System.out.println(query.split("=")[0]);
            }
            System.out.println(url.getQuery().split("&"));
        } catch (MalformedURLException ex) {
            Logger.getLogger(AuthorizeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
