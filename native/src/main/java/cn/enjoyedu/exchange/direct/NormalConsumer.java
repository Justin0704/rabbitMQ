package cn.enjoyedu.exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 普通消费者
 */
public class NormalConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {

        //创建连接，连接到rabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        //设置连接工程的默认地址
        factory.setHost("localhost");
        //创建连接
        Connection connection = factory.newConnection();
        //创建信道
        Channel channel = connection.createChannel();
        //在信道中设置交换器
        channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //声明队列
        String queueName = "queue-justin";
        channel.queueDeclare(queueName, false, false, false, null);

        //绑定，将队列（queue-justin）与交换器通过路由键绑定
        String routeKey = "justin";
        channel.queueBind(queueName, DirectProducer.EXCHANGE_NAME, routeKey);
        System.out.println("Waiting message...");

        //声明一个消费者
        final Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received: [" + envelope.getRoutingKey() + "]" + message);
            }
        };
        //消费者开始指定队列上的消息
        channel.basicConsume(queueName, true, consumer);
    }
}
