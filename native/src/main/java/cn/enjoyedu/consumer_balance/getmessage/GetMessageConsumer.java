package cn.enjoyedu.consumer_balance.getmessage;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息消费要避免拉取
 * 消费者进行消息消费的时候，无论是拉取还是消费，都要进行消息消费
 */
public class GetMessageConsumer {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        //创建连接工厂到rabbitMq
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        //创建一个连接
        Connection connection = factory.newConnection();
        //创建一个信道
        final Channel channel = connection.createChannel();
        //指定转发(使用直接交换器)
        channel.exchangeDeclare(GetMessageProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //声明一个队列
        String queueName = "focuserror";
        channel.queueDeclare(queueName, false, false, false, null);
        String routeKey = "error";
        channel.queueBind(queueName, GetMessageProducer.EXCHANGE_NAME, routeKey);
        System.out.println("[*] Waiting messages ...");
        //无限循环拉取
        while (true){
            /**
             * true：自动确认（rabbit认为这条消息消费-从队列中删除），false非自动确认
             */
            GetResponse response = channel.basicGet(queueName, false);
            if(response != null){
                System.out.println("Received [" + response.getEnvelope().getRoutingKey() + "]" +
                        new String(response.getBody()));
            }
            //TODO 手动确认机制，确认后的数据将从队列里面删除掉
            channel.basicAck(0, true);
            Thread.sleep(100);
        }
    }
}
