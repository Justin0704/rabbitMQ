package cn.enjoyedu.topic;

import cn.enjoyedu.constant.RabbitConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TopicSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(){
        String emailMsg = "this is email message ...";
        System.out.println("TopicSender send email message: " + emailMsg);
        this.rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_TOPIC, RabbitConstant.ROUTE_KEY_EMAIL, emailMsg);

        String userMsg = "this is user message ...";
        System.out.println("TopicSender send user message: " + userMsg);
        this.rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_TOPIC, RabbitConstant.ROUTE_KEY_USER, userMsg);

        String errorMsg = "this is an error message";
        System.out.println("TopicSender send an error message");
        this.rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_TOPIC, "errorKey", errorMsg);

    }
}
