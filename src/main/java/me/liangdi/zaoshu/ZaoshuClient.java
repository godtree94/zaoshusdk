package me.liangdi.zaoshu;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.liangdi.zaoshu.api.InstanceApi;
import me.liangdi.zaoshu.api.KeyPair;
import me.liangdi.zaoshu.api.SystemApi;
import me.liangdi.zaoshu.api.UserApi;

/**
 * Zaoshu Client 是各 API 接口的组合封装
 * Created by liangdi on 6/27/17.
 */
public class ZaoshuClient {
    private KeyPair keyPair;
    private UserApi user = null;
    private InstanceApi instance = null;
    private SystemApi system = null;
    
    // jvm保证在任何线程访问uniqueInstance静态变量之前一定先创建了此实例  
    private static ZaoshuClient uniqueInstance = new ZaoshuClient();  

    // 私有的默认构造子，保证外界无法直接实例化  
    private ZaoshuClient() {
        InputStream ins = getClass().getResourceAsStream("/zaoshuclient.properties"); 
        if(ins==null){
            throw new RuntimeException("找不到zaoshuclient.properties配置文件");
        }
        Properties properties=new Properties();
        try {
            properties.load(ins);
        } catch (IOException ex) {
            throw new RuntimeException("读取配置文件发生io异常");
        }
        if(properties.get("apiKey")==null||properties.get("secret")==null){
            throw new RuntimeException("配置文件没有包含apiKey或者secret的配置值");
        }
        this.keyPair = new KeyPair(properties.get("apiKey").toString(), properties.get("secret").toString());
    }  

    // 提供全局访问点获取唯一的实例  
    public static ZaoshuClient getInstance() {  
        return uniqueInstance;  
    }

    /**
     * 获取用户操作 API 接口
     * @return
     */
    public synchronized UserApi user(){
        if(user == null) {
            user = new UserApi();
            user.init(keyPair);
        }

        return user;
    }

    /**
     * 实例相关 API 接口
     * @return
     */
    public synchronized InstanceApi instance(){
        if(instance == null){
            instance = new InstanceApi();
            instance.init(keyPair);
        }

        return instance;
    }

    /**
     * 系统相关 API 接口
     * @return
     */
    public synchronized SystemApi system(){
        if(system == null){
            system = new SystemApi();
            system.init(keyPair);
        }

        return system;
    }
}
