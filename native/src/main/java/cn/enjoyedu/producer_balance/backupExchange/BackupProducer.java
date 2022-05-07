package cn.enjoyedu.producer_balance.backupExchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 备用交换器
 */
public class BackupProducer {

    public static final String EXCHANGE_NAME = "main_exchange";
    public static final String BAK_EXCHANGE_NAME = "bak_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接，连接到rabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        //设置连接工厂的连接地址（默认端口号为5672）
        factory.setHost("127.0.0.1");
        //创建连接
        Connection connection = factory.newConnection();
        //创建信道
        Channel channel = connection.createChannel();
        //TODO 声明备用交换器
        Map<String, Object> argsMap = new HashMap<String, Object>();
        argsMap.put("alternate-exchange", BAK_EXCHANGE_NAME);
        //在信道中设置交换器(主交换器)
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false, false, argsMap);
        //备用交换器
        channel.exchangeDeclare(BAK_EXCHANGE_NAME, BuiltinExchangeType.FANOUT, true, false, null);

        //声明路由键/消息体
        String[] routeKeys = {"justin", "willon", "john"};
        for(int i = 0;i < 3;i++){
            String routeKey = routeKeys[i%3];
            //发送消息
            String msg = "Hello RabbitMQ" + (i+1) + ("_" + System.currentTimeMillis());
            //发消息,true 表示加上了失败的通知
            channel.basicPublish(EXCHANGE_NAME, routeKey,null, msg.getBytes());
            System.out.println("---------------------------------------------------");
            System.out.println("Send Message：" + routeKey + ":" + msg);
        }
        //关闭信道
        channel.close();
        //关闭连接
        connection.close();
    }
}
