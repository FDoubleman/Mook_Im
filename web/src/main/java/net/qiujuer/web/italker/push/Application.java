package net.qiujuer.web.italker.push;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.qiujuer.web.italker.push.provider.GsonProvider;
import net.qiujuer.web.italker.push.service.AccountService;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;

/**
 * Created by fmm on 2017/7/8.
 */
public class Application extends ResourceConfig{

    public Application(){
        //注册逻辑处理的包名
        packages(AccountService.class.getPackage().getName());

        //注册json转换器
//        register(JacksonJsonProvider.class);
        //替换Gson 解析器
        register(GsonProvider.class);
        //注册日志打印输出
        register(Logger.class);
    }
}
