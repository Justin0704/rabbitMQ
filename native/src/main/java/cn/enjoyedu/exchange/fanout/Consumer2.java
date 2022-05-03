package cn.enjoyedu.exchange.fanout;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 类说明：fanout交换器和路由键没有关系，即使使用一个不存在的路由键也能收到消息
 */
public class Consumer2 {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        //打开连接和创建信道
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(FanoutProducer.EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        //声明一个随机队列
        String queueName = channel.queueDeclare().getQueue();
        //设置一个不存在的路由键
        String routeKey = "test";
        channel.queueBind(queueName, FanoutProducer.EXCHANGE_NAME, routeKey);
        System.out.println("[*] waiting for the messages ...");
        //创建队列消费者
        final Consumer consumerB = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received " + envelope.getRoutingKey() + " : " + message);
            }
        };
        channel.basicConsume(queueName, true, consumerB);
    }
}
