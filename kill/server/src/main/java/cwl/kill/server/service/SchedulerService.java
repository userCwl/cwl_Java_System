package cwl.kill.server.service;

import cwl.kill.model.entity.ItemKillSuccess;
import cwl.kill.model.mapper.ItemKillSuccessMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务服务
 * @Author long
 * @Date 2020/3/19 11:34
 */
@Service
public class SchedulerService {
    private final static Logger log = LoggerFactory.getLogger(SchedulerService.class);

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;
    @Autowired
    private Environment env;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void schedulerExpireOrders(){
        try{
            List<ItemKillSuccess> list = itemKillSuccessMapper.selectExpireOrders();
            if(list!=null && !list.isEmpty()){
                // java8的写法
                list.stream().forEach(i ->{
                    if(i!=null && i.getDiffTime() > env.getProperty("scheduler.expire.orders.time",Integer.class)){
                        itemKillSuccessMapper.expireOrder(i.getCode());
                    }
                });
            }
        }catch (Exception e){
            log.error("定时获取status=0的订单并判断是否超过TTL，然后进行失效-发生异常：",e.fillInStackTrace());
        }
    }
}
