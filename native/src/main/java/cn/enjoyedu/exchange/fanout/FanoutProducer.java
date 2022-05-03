package cn.enjoyedu.exchange.fanout;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * fanout交换器
 */
public class FanoutProducer {

    public static final String EXCHANGE_NAME = "fanout_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接到rabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        //设置rabbitMQ地址
        factory.setHost("127.0.0.1");
        //创建一个连接
        Connection connection = factory.newConnection();
        //创建一个信道
        Channel channel = connection.createChannel();
        //指定转发
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        //声明路由键
        String[] routeKeys = {"justin", "willon", "john"};
        for(int i = 0;i < 3; i++){
            String routeKey = routeKeys[i % 3];//每一次发送一条消息
            //发送消息
            String message = "Hello world_" + (i + 1);
            channel.basicPublish(EXCHANGE_NAME, routeKey, null, message.getBytes());
            System.out.println("Send " + routeKey + " [" + message + "]");
        }
        //关闭信道和连接
        channel.close();
        connection.close();
    }
}
