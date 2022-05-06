package cn.enjoyedu.producer_balance.producerConfirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 发布者确认 - 批量确认
 */
public class ProducerBatchConfirm {
    public static final String EXCHANGE_NAME = "producer_batch_confirm";
    //声明路由键/消息体
    public static final String ROUTE_KEY = "justin";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //创建连接，连接到rabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        //设置连接工厂的连接地址（默认端口号为5672）
        factory.setHost("127.0.0.1");
        //创建连接
        Connection connection = factory.newConnection();
        //创建信道
        Channel channel = connection.createChannel();
        //在信道中设置交换器
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //TODO 连接关闭回调
        connection.addShutdownListener(new ShutdownListener() {
            public void shutdownCompleted(ShutdownSignalException e) {
                System.out.println("连接关闭 ......");
            }
        });
        //TODO 信道关闭回调
        channel.addShutdownListener(new ShutdownListener() {
            public void shutdownCompleted(ShutdownSignalException e) {
                System.out.println("信道关闭 ......");
            }
        });
        /**
         * TODO 失败通知，回调, 有justin接收的路由键，则消息发送成功，其它没有路由键的则消息发送失败
         * ---------------------------------------------------
         Send Message：justin:Hello RabbitMQ1_1651829975179
         ---------------------------------------------------
         Send Message：willon:Hello RabbitMQ2_1651829975395
         返回的 replyCode：312
         返回的 replyText：NO_ROUTE
         返回的 exchange：mandatory_test
         返回的 routeKey：willon
         ---------------------------------------------------
         Send Message：john:Hello RabbitMQ3_1651829975596
         返回的 replyCode：312
         返回的 replyText：NO_ROUTE
         返回的 exchange：mandatory_test
         返回的 routeKey：john
         */
        channel.addReturnListener(new ReturnListener() {
            public void handleReturn(int replyCode, String replyText, String exchange, String routeKey, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
                String message = new String(bytes, "UTF-8");
                System.out.println(" 返回的 replyCode：" + replyCode);
                System.out.println(" 返回的 replyText：" + replyText);
                System.out.println(" 返回的 exchange：" + exchange);
                System.out.println(" 返回的 routeKey：" + routeKey);
            }
        });
        //TODO 启用发送者确认模式
        channel.confirmSelect();

        for(int i = 0;i < 2;i++){
            String msg = "Hello RabbitMQ" + (i+1) + ("_" + System.currentTimeMillis());
            //发消息,true 表示加上了失败的通知
            channel.basicPublish(EXCHANGE_NAME, ROUTE_KEY, true,null, msg.getBytes());
            System.out.println("---------------------------------------------------");
            System.out.println("Send Message：" + ROUTE_KEY + ":" + msg);
        }
        //TODO 启用发送者确认模式（批量确认模式）
        channel.waitForConfirmsOrDie();
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
