package cn.enjoyedu.consumer_balance.qos;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * QoS（Quality of Service，服务质量）指一个网络能够利用各种基础技术，为
 * 指定的网络通信提供更好的服务能力，是网络的一种安全机制， 是用来解决网络延迟和阻塞等问题的一种技术。
 * QoS的保证对于容量有限的网络来说是十分重要的，特别是对于流多媒体应用，例如VoIP和IPTV等，
 * 因为这些应用常常需要固定的传输率，对延时也比较敏感。
 *
 * Qos预取模式，效率比手动拉取效率高
 */
public class QosConsumer {
    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接工厂到rabbitMq
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        //创建一个连接
        Connection connection = factory.newConnection();
        //创建一个信道
        final Channel channel = connection.createChannel();
        //指定转发(使用直接交换器)
        channel.exchangeDeclare(QosProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //声明一个队列
        String queueName = "focuserror";
        channel.queueDeclare(queueName, false, false, false, null);
        //绑定，将队列和交换器通过路由键绑定
        String routeKey = "error";
        channel.queueBind(queueName, QosProducer.EXCHANGE_NAME, routeKey);
        System.out.println("[*] Waiting messages ...");

        //声明一个消费者
        final Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received [" + envelope.getRoutingKey() + "]" + message);
                //TODO 单条确认
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        //TODO 150条预取
        //channel.basicQos(150, true);
        //消费者在指定的队列消费
        //channel.basicConsume(queueName, false, consumer);
        //自定义消费者批量确认
        BatchAckConsumer batchAckConsumer = new BatchAckConsumer(channel);
        channel.basicConsume(queueName, false, batchAckConsumer);
    }
}
