package cwl.kill.server.config;

import cwl.kill.server.service.CustomRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Shiro的通用化配置
 * @Author long
 * @Date 2020/3/19 13:48
 */
@Configuration
public class ShiroConfig {
    @Bean
    public CustomRealm customRealm(){
        return new CustomRealm();
    }

    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(customRealm());
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager());
        bean.setLoginUrl("/to/login");
        bean.setUnauthorizedUrl("/unauth");

        Map<String,String> filterChainDefinitionMap = new HashMap<>();
        filterChainDefinitionMap.put("/to/login","anon");
        filterChainDefinitionMap.put("/**","anon");
//        filterChainDefinitionMap.put("/kill/execute/*","authc");// 使用Jmeter压力测试时，不进行shiro权限认证，注释此行
        filterChainDefinitionMap.put("/item/detail/*","authc");

        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return bean ;
    }
}
