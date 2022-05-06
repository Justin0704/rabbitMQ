package cn.enjoyedu.producer_balance.mandatory;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 生产者 - 消息发布时的权衡
 * 失败通知（发送消息时设置mandatory标志）
 */
public class ProducerMandatory {

    public static final String EXCHANGE_NAME = "mandatory_test";

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
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        //TODO 失败通知


        //声明路由键
        String[] teachers = {"justin", "willon", "john"};
        for(int i = 0; i < 3 ; i ++){
            String[] modules = {"kafka", "jvm", "redis"};
            for(int j = 0;j<3;j++){
                String[] servers = {"A", "B", "C"};
                for(int k = 0;k<3;k++){
                    //发送消息
                    String message = "Hello Topic_["+i+","+j+","+k+"]";
                    String routeKey = teachers[i%3] + "." + modules[j%3] + "." + servers[k%3];
                    channel.basicPublish(EXCHANGE_NAME, routeKey, null, message.getBytes());
                    System.out.println(" [*] Send " + routeKey + " : " + message);
                }
            }
        }
        //关闭信道和连接
        channel.close();
        connection.close();
    }
}
