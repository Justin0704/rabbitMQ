package cn.enjoyedu.exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MultiBindConsumer {



    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接，连接到rabbitMq
        ConnectionFactory factory = new ConnectionFactory();
        //设置连接的默认地址
        factory.setHost("localhost");
        //创建连接
        Connection connection = factory.newConnection();
        //创建信道
        final Channel channel = connection.createChannel();
        //信道设置交换器类型
        channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //声明一个随机队列
        String queueName = channel.queueDeclare().getQueue();
        //队列绑定到交换器上时，是允许绑定多个路由键的，也就是多重绑定
        String[] routeKeys = {"justin", "willon", "john"};
        for(String routeKey : routeKeys){
            channel.queueBind(queueName, DirectProducer.EXCHANGE_NAME, routeKey);
        }
        System.out.println("Waiting for the message ...");
        //创建消费者队列
        final Consumer consumerA = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" Received : " + envelope.getRoutingKey() + ":" + message);
            }
        };
        //消费者开始指定队列上的消息
        channel.basicConsume(queueName, true, consumerA);

    }
}
