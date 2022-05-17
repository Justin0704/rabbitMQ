package cn.enjoyedu.hello;

import cn.enjoyedu.constant.RabbitConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 类说明：消息的生产者
 */
@Component
public class DefaultSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(String msg){
        String sendMsg = msg + "-----" + System.currentTimeMillis();
        System.out.println("发送的消息：" + sendMsg);
        //普通消息发送
        //rabbitTemplate.convertAndSend(RabbitConstant.QUEUE_HELLO, sendMsg);
        //消息的处理 -- 加了发送方的确认
        rabbitTemplate.convertAndSend(RabbitConstant.QUEUE_USER, sendMsg);
    }
}
