package cwl.kill.server.controller;

import cwl.kill.api.enums.StatusCode;
import cwl.kill.api.response.BaseResponse;
import cwl.kill.server.dto.KillDto;
import cwl.kill.server.service.KillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 秒杀商品控制器
 * @Author long
 * @Date 2020/3/8 11:57
 */
@Controller
@RequestMapping("kill")
public class KillController {


    private static final Logger log = LoggerFactory.getLogger(KillController.class);

    @Autowired
    private KillService killService;

    /**
     * 商品秒杀核心业务逻辑
     * @param dto
     * @param result
     * @param session
     * @return
     */
    @RequestMapping(value = "/execute",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    //BindingResult用在实体类校验信息返回结果绑定，与@Validated配套使用
    public BaseResponse execute(@RequestBody @Validated KillDto dto, BindingResult result, HttpSession session){
        if(result.hasErrors() || dto.getKillId()<=0){
            //参数有问题
            return new BaseResponse(StatusCode.InvalidParams);
        }
        //判断用户是否登录
        Object uId = session.getAttribute("uid");
        if(uId == null){
            return new BaseResponse(StatusCode.UserNotLogin);
        }

        Integer userId = (Integer)uId;
        BaseResponse response = new BaseResponse(StatusCode.Success);

        //进行秒杀
        try{
            Boolean res = killService.killItem(dto.getKillId(),userId);
            if(!res){
                return new BaseResponse(StatusCode.Fail.getCode(),"商品已抢购完毕或者不在抢购时间段哦！");
            }
        }catch (Exception e){
            response = new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;

    }

    /**
     * 商品秒杀核心业务逻辑-用于压力测试（不作用户登录认证）
     * @param dto
     * @param result
     * @return
     */
    @RequestMapping(value = "/execute/lock",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BaseResponse executeLock(@RequestBody @Validated KillDto dto,BindingResult result){
        if(result.hasErrors() || dto.getKillId() <= 0){
            return new BaseResponse(StatusCode.InvalidParams);
        }
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {

            //不加分布式锁的前提
            Boolean res=killService.killItem(dto.getKillId(),dto.getUserId());
            if (!res){
                return new BaseResponse(StatusCode.Fail.getCode(),"不加分布式锁-哈哈~商品已抢购完毕或者不在抢购时间段哦!");
            }

            //不加分布式锁的前提-MySQL层的优化
            /*Boolean res=killService.killItemV2(dto.getKillId(),dto.getUserId());
            if (!res){
                return new BaseResponse(StatusCode.Fail.getCode(),"不加分布式锁-哈哈~商品已抢购完毕或者不在抢购时间段哦!");
            }*/

            //基于Redis的分布式锁进行控制
            /*Boolean res=killService.killItemV3(dto.getKillId(),dto.getUserId());
            if (!res){
                return new BaseResponse(StatusCode.Fail.getCode(),"基于Redis的分布式锁进行控制-哈哈~商品已抢购完毕或者不在抢购时间段哦!");
            }*/

            //基于Redisson的分布式锁进行控制
            /*Boolean res=killService.killItemV4(dto.getKillId(),dto.getUserId());
            if (!res){
                return new BaseResponse(StatusCode.Fail.getCode(),"基于Redisson的分布式锁进行控制-哈哈~商品已抢购完毕或者不在抢购时间段哦!");
            }*/

            //基于ZooKeeper的分布式锁进行控制
//            Boolean res=killService.killItemV5(dto.getKillId(),dto.getUserId());
//            if (!res){
//                return new BaseResponse(StatusCode.Fail.getCode(),"基于ZooKeeper的分布式锁进行控制-哈哈~商品已抢购完毕或者不在抢购时间段哦!");
//            }

        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }


    /**
     * 抢购成功页面
     * @return
     */
    @RequestMapping(value = "/execute/success",method = RequestMethod.GET)
    public String executeSuccess(){
        return "executeSuccess";
    }

    /**
     * 抢购失败页面
     * @return
     */
    @RequestMapping(value = "/execute/fail",method = RequestMethod.GET)
    public String executeFail(){
        return "executeFail";
    }

}
