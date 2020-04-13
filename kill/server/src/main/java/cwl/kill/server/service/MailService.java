package cwl.kill.server.service;

import cwl.kill.server.dto.MailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * 邮件服务
 * @Author long
 * @Date 2020/3/19 10:37
 */
@Service
@EnableAsync
public class MailService {

    private final static Logger log = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Environment env;

    /**
     * 发送简单文本
     * @param dto
     */
    @Async
    public void sendSimpleEmail(final MailDto dto){
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(env.getProperty("mail.send.from"));
            mailMessage.setTo(dto.getTos());
            mailMessage.setSubject(dto.getSubject());
            mailMessage.setText(dto.getContent());
            mailSender.send(mailMessage);

            log.info("发送简单文本文件-发送成功！");
        }catch (Exception e){
            log.error("发送简单文本文件-发送异常：",e.fillInStackTrace());
        }
    }

    /**
     * 发送花哨邮件
     * @param dto
     */
    @Async
    public void sendHTMLMail(final MailDto dto){
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message,true,"utf-8");
            messageHelper.setFrom(env.getProperty("mail.send.from"));
            messageHelper.setTo(dto.getTos());
            messageHelper.setSubject(dto.getSubject());
            messageHelper.setText(dto.getContent(),true);

            mailSender.send(message);
            log.info("发送花哨邮件-发送成功");
        }catch (Exception e){
            log.error("发送花哨邮件-发送异常：",e.fillInStackTrace());
        }
    }

}
