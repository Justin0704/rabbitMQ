package cn.enjoyedu.exchange.direct;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * direct类型交换器的生产者
 */
public class DirectProducer {

    public static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {

        //创建连接，连接到rabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        //设置连接工厂的连接地址（默认端口号为5672）
        factory.setHost("localhost");
        //创建连接
        Connection connection = factory.newConnection();
        //创建信道
        Channel channel = connection.createChannel();
        //在信道中设置交换器
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //声明路由键/消息体
        String[] routeKeys = {"justin", "willon", "john"};
        for(int i = 0;i<3;i++){
            String routeKey = routeKeys[i % 3];
            String msg = "Hello RabbitMQ" + (i+1);
            //发消息
            channel.basicPublish(EXCHANGE_NAME, routeKey, null, msg.getBytes());
            System.out.println("Send：" + routeKey + ":" + msg);
        }
        //关闭信道
        if(channel != null){
            channel.close();
        }
        //关闭连接
        if(connection != null){
            connection.close();
        }
    }
}
