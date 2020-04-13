package cwl.kill.server.service;

import cwl.kill.model.dto.KillSuccessUserInfo;
import cwl.kill.model.entity.ItemKillSuccess;
import cwl.kill.model.mapper.ItemKillSuccessMapper;
import cwl.kill.server.dto.MailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ接收消息服务
 * @Author long
 * @Date 2020/3/19 10:35
 */
@Service
public class RabbitReceiverService {

    private final static Logger log = LoggerFactory.getLogger(RabbitReceiverService.class);

    @Autowired
    private Environment env;
    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;
    @Autowired
    private MailService mailService;


    /**
     * 秒杀异步邮件通知-接收消息
     * @param info
     */
    @RabbitListener(queues = "${mq.kill.item.success.email.queue}",containerFactory = "singleListenerContainer")
    public void consumeEmailMsg(KillSuccessUserInfo info){
        try{
            log.info("秒杀异步邮件通知-接收消息：{}",info);

            //TODO:真正的发送邮件....
            //MailDto dto=new MailDto(env.getProperty("mail.kill.item.success.subject"),"这是测试内容",new String[]{info.getEmail()});
            //mailService.sendSimpleEmail(dto);

            final String content = String .format(env.getProperty("mail.kill.item.success.content"),info.getItemName(),info.getCode());
            MailDto dto = new MailDto(env.getProperty("mail.kill.item.success.subject"),content,new String[]{info.getEmail()});
            mailService.sendHTMLMail(dto);
        }catch (Exception e){
            log.error("秒杀异步邮件通知-接收消息-发送异常：",e.fillInStackTrace());
        }
    }

    /**
     * 用户秒杀成功后超时未支付-监听者
     * @param info
     */
    @RabbitListener(queues = "${mq.kill.item.success.kill.dead.real.queue}",containerFactory = "singleListenerContainer" )
    public void consumeExpireOrder(KillSuccessUserInfo info){
        try {
            log.info("用户秒杀成功后超时未支付-监听者-接收消息：{}",info);

            if(info!=null){
                ItemKillSuccess itemKillSuccess = itemKillSuccessMapper.selectByPrimaryKey(info.getCode());
                if(itemKillSuccess!=null && itemKillSuccess.getStatus().intValue() == 0){
                    itemKillSuccessMapper.expireOrder(info.getCode());
                }
            }
        }catch (Exception e){
            log.error("用户秒杀成功后超时未支付-监听者-发生异常：",e.fillInStackTrace());
        }
    }

}
