package cn.enjoyedu.fanout;

import cn.enjoyedu.constant.RabbitConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FanoutSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(String msg){
        String sendMsg = msg + "----" + System.currentTimeMillis();
        System.out.println("FanoutSender = " + sendMsg);
        this.rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_FANOUT, "", sendMsg);
    }

}
