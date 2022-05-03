package cn.enjoyedu.exchange.topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * # 表示收到所有的消息
 */
public class AllConsumer {
    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接到rabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        //设置rabbitMQ地址
        factory.setHost("127.0.0.1");
        //创建一个连接
        Connection connection = factory.newConnection();
        //创建一个信道
        final Channel channel = connection.createChannel();
        //指定转发
        channel.exchangeDeclare(TopicProducer.EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //声明一个随机队列
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, TopicProducer.EXCHANGE_NAME, "#");
        System.out.println("[*] waiting for the messages ...");
        //创建队列消费者
        final Consumer consumerA = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("AllConsumer Received " + envelope.getRoutingKey() + " : " + message);
            }
        };
        channel.basicConsume(queueName, true, consumerA);
    }
}
