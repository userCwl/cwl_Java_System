package cwl.kill.server.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户控制器
 * @Author long
 * @Date 2020/3/19 9:45
 */
@Controller
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private Environment env;

    /**
     * 跳转到登录页
     * @return
     */
    @RequestMapping(value = {"/to/login","/unauth"})
    public String toLogin(){
        return "login";
    }

    /**
     * 登录认证
     * @param userName
     * @param password
     * @param modelMap
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(@RequestParam String userName, @RequestParam String password, ModelMap modelMap){
        String errorMsg = "";
        try {
            if(!SecurityUtils.getSubject().isAuthenticated()){
                String newPsd = new Md5Hash(password,env.getProperty("shiro.encrypt.password.salt")).toString();
                UsernamePasswordToken token = new UsernamePasswordToken(userName,newPsd);
                SecurityUtils.getSubject().login(token);
            }
        }catch (UnknownAccountException e){
            errorMsg = e.getMessage();
            modelMap.addAttribute("userName",userName);
        }catch (DisabledAccountException e){
            errorMsg = e.getMessage();
            modelMap.addAttribute("userName",userName);
        }catch (IncorrectCredentialsException e){
            errorMsg = e.getMessage();
            modelMap.addAttribute("userName",userName);
        }catch (Exception e){
            errorMsg = "用户登录异常，请联系管理员!";
            e.printStackTrace();
        }
        // 登录成功
        if(StringUtils.isBlank(errorMsg)){
            return "redirect:/index";// 直接跳转到商品列表
        }else {
            modelMap.addAttribute("errorMsg",errorMsg);
            return "login";
        }
    }

    /**
     * 退出登录
     * @return
     */
    @RequestMapping("/logout")
    public String logout(){
        SecurityUtils.getSubject().logout();
        return "login";
    }
}
