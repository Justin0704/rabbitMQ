package cn.enjoyedu.consumer_balance.getmessage;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class GetMessageProducer {

    public static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {

        //创建连接工厂到rabbitMq
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        //创建一个连接
        Connection connection = factory.newConnection();
        //创建一个信道
        Channel channel = connection.createChannel();
        //指定转发(使用直接交换器)
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        for(int i = 0;i < 3; i++){
            String message = "Helloworld_" + (i+1);
            //指定交换器下的routeKey，发送消息
            channel.basicPublish(EXCHANGE_NAME, "error", null, message.getBytes());
            System.out.println("Send error: " + message);
        }
        channel.close();
        connection.close();
    }
}
