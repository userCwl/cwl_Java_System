package cwl.kill.server.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 基础控制器
 * @Author long
 * @Date 2020/3/8 9:54
 */
@Controller
@RequestMapping("base")
public class BaseController {

    /**
     * 跳转到错误页面
     * @return
     */
    @RequestMapping(value = "/error",method = RequestMethod.GET)
    public String error(){
        return "error";
    }

    /**
     * 跳转到欢迎页面
     * @param name
     * @param modelMap
     * @return
     */
    @GetMapping("/welcome")
    public String welcome(String name, ModelMap modelMap){
        if(StringUtils.isBlank(name)){
            name = "welcome!";
        }
        modelMap.put("name",name);
        return "welcome";
    }



}
