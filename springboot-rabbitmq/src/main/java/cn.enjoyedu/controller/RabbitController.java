package cn.enjoyedu.controller;

import cn.enjoyedu.fanout.FanoutSender;
import cn.enjoyedu.hello.DefaultSender;
import cn.enjoyedu.topic.TopicSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rabbit")
public class RabbitController {

    @Autowired
    private DefaultSender defaultSender;
    @Autowired
    private FanoutSender fanoutSender;
    @Autowired
    private TopicSender topicSender;

    @RequestMapping("/hello")
    public void hello(){
        defaultSender.send("hello");
    }
    /**
     * topic exchange类型rabbitmq测试
     */
    @GetMapping("/topicTest")
    public void topicTest() {
        topicSender.send();
    }

    /**
     * fanout exchange类型rabbitmq测试
     */
    @GetMapping("/fanoutTest")
    public void fanoutTest() {
        fanoutSender.send("hellomsg:OK");
    }
}
