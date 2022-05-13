package cn.enjoyedu.dlx;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 死信交换器的用法
 * 若是设置requeue=false，或消息过期，或超出队列范围（最先进入的消息会丢失）
 * 则消息会丢失，可以使用死信交换器获取丢失的数据，然后重新投递
 */
public class DlxProducer {
    public static final String EXCHANGE_NAME = "dlx_make";

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接工厂到rabbitMq
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        //创建一个连接
        Connection connection = factory.newConnection();
        //创建一个信道
        Channel channel = connection.createChannel();
        //指定转发(使用直接交换器)
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //定义路由键数组
        String[] routeKeys = {"justin", "willon", "john"};

        for(int i = 0;i < 3; i++){
            String routeKey = routeKeys[i%3];
            //发送消息
            String message = "Hello RabbitMQ " + (i+1);
            //指定交换器下的routeKey，发送消息
            channel.basicPublish(EXCHANGE_NAME, routeKey, null, message.getBytes());
            System.out.println("Send '"+routeKey+"': " + message);
        }
        //关闭频道和连接
        channel.close();
        connection.close();
    }
}
