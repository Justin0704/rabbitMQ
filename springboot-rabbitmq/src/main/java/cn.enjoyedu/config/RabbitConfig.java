package cn.enjoyedu.config;

import cn.enjoyedu.constant.RabbitConstant;
import cn.enjoyedu.hello.UserReceiver;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${spring.rabbitmq.host}")
    private String address;

    @Value("${spring.rabbitmq.port}")
    private String port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Value("${spring.rabbitmq.publisher-confirms}")
    private Boolean publisherConfirms;

    @Autowired
    private UserReceiver userReceiver;

    /**
     * 连接工厂
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(address+ ":" +port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirms(publisherConfirms);
        return connectionFactory;
    }
    /**
     * rabbitAdmin类封装对rabbitMQ的管理操作
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }
    /**
     * 使用rabbitTemplate
     * @return
     */
    @Bean
    public RabbitTemplate newRabbitTemplate(){
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        //失败通知
        template.setMandatory(true);
        //发送确认
        template.setConfirmCallback(confirmCallback());
        //失败回调
        template.setReturnCallback(returnCallback());
        return template;
    }
    /**
     * 使用了rabbitMQ系统缺省的交换器（direct交换器）
     * 声明队列
     */
    @Bean
    public Queue helloQueue(){ return new Queue(RabbitConstant.QUEUE_HELLO); }
    @Bean
    public Queue userQueue(){ return new Queue(RabbitConstant.QUEUE_USER); }

    //TODO -------------------------验证-Topic exchange demo
    @Bean
    public Queue queueEmailMessage(){ return new Queue(RabbitConstant.QUEUE_TOPIC_EMAIL); }
    @Bean
    public Queue queueUserMessage(){ return new Queue(RabbitConstant.QUEUE_TOPIC_USER); }
    //声明topic交换器
    @Bean
    public TopicExchange exchange(){ return new TopicExchange(RabbitConstant.EXCHANGE_TOPIC); }
    //绑定关系
    @Bean
    public Binding bindingEmailExchangeMessage(){
        return BindingBuilder.bind(queueEmailMessage()).to(exchange()).with(RabbitConstant.ROUTE_KEY_EMAIL);
    }
    @Bean
    public Binding bindingUserExchangeEmail(){
        return BindingBuilder.bind(queueUserMessage()).to(exchange()).with(RabbitConstant.ROUTE_KEY_USER);
    }

    //TODO ----------------------验证Fanout交换器
    @Bean
    public Queue AMessage(){ return new Queue(RabbitConstant.QUEUE_FANOUT); }
    @Bean
    public FanoutExchange fanoutExchange(){ return new FanoutExchange(RabbitConstant.EXCHANGE_FANOUT); }

    //TODO 消费者确认
    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        //绑定sb.user队列
        container.setQueues(userQueue());
        //手动提交
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //发送确认
        container.setMessageListener(userReceiver);
        return container;
    }

    /**
     * 生产者发送确认
     * @return
     */
    @Bean
    public RabbitTemplate.ConfirmCallback confirmCallback() {
        return new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if(ack){
                    System.out.println("发送者确认发送给mq成功");
                }else{
                    //处理失败的消息
                    System.out.println("发送者发送给mq失败,考虑重发:"+cause);
                }
            }
        };
    }
    /**
     * 失败回调
     * @return
     */
    @Bean
    public RabbitTemplate.ReturnCallback returnCallback() {
        return new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("无法路由的消息，需要考虑另外处理。");
                System.out.println("Returned replyText："+replyText);
                System.out.println("Returned exchange："+exchange);
                System.out.println("Returned routingKey："+ routingKey);
                String msgJson  = new String(message.getBody());
                System.out.println("Returned Message："+msgJson);
            }
        };
    }
}
