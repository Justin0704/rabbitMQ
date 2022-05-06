package cn.enjoyedu.producer_balance.mandatory;

import cn.enjoyedu.exchange.direct.DirectProducer;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消费者 - (失败确认模式)
 */
public class ConsumerProducerMandatory {

    public static void main(String[] args) throws IOException, TimeoutException {

//创建连接，连接到rabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        //设置连接工程的默认地址
        factory.setHost("127.0.0.1");
        //创建连接
        Connection connection = factory.newConnection();
        //创建信道
        Channel channel = connection.createChannel();
        //在信道中设置交换器
        channel.exchangeDeclare(ProducerMandatory.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //声明随机队列
        String queueName = channel.queueDeclare().getQueue();
        //绑定，将队列（queue-justin）与交换器通过路由键绑定
        String routeKey = "justin";
        channel.queueBind(queueName, ProducerMandatory.EXCHANGE_NAME, routeKey);
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
