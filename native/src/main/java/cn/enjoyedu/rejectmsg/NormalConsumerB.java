package cn.enjoyedu.rejectmsg;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class NormalConsumerB {
    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        //创建连接
        Connection connection = factory.newConnection();
        //创建信道
        final Channel channel = connection.createChannel();
        //添加一个交换器, 标记为直接交换器
        channel.exchangeDeclare(RejectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //声明一个队列
        String queueName = "focuserror";
        channel.queueDeclare(queueName, false, false, false, null);
        //绑定，将队列和交换器通过路由键进行绑定
        String routeKey = "error";
        channel.queueBind(queueName, RejectProducer.EXCHANGE_NAME, routeKey);

        System.out.println("[*] ConsumerB waiting for the messages ... ");

        final Consumer consumerB = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                try{
                    String message = new String(body, "UTF-8");
                    System.out.println("Received [" + envelope.getRoutingKey() + "]" + message);
                    //消费者确认
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }catch (Exception ex){
                    channel.basicReject(envelope.getDeliveryTag(), false);
                }
            }
        };
        //消费者正式在指定队列上消费消息
        channel.basicConsume(queueName, false, consumerB);
    }
}
