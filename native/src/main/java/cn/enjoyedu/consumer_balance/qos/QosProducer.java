package cn.enjoyedu.consumer_balance.qos;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class QosProducer {

    public static String EXCHANGE_NAME = "qos_logs";

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
        //生产者发送批量消息
        for(int i = 0;i<210;i++){
            String message = "Hello World " + (i + 1);
            if(i == 209){
                message = "stop";
            }
            //发送消息，exchangeName，routeKey
            channel.basicPublish(EXCHANGE_NAME, "error", null, message.getBytes());
            System.out.println("Send message: " + message);
        }
        //关闭信道和连接
        channel.close();
        connection.close();
    }
}
