package cn.enjoyedu.exchange.topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 订阅所有justin的消息
 */
public class Justin_AllConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(TopicProducer.EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //声明一个随机队列
        String queueName = channel.queueDeclare().getQueue();
        //TODO 订阅所有justin的消息
        channel.queueBind(queueName, TopicProducer.EXCHANGE_NAME, "justin.#");
        System.out.println("[*] waiting for messages ...");
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
