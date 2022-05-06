package cn.enjoyedu.producer_balance.producerConfirm;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 生产者确认 - 发送方确认模式 - 异步监听确认
 */
public class ProducerAsyncConfirm {
    public static final String EXCHANGE_NAME = "producer_async_confirm";

    public static void main(String[] args) throws IOException, TimeoutException {
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
        //TODO 启用发送者确认模式
        channel.confirmSelect();
        //TODO 添加发送者确认监听器
        channel.addConfirmListener(new ConfirmListener() {
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("send_ACK: " + deliveryTag + " ,multiple: " + multiple);
            }
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {

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
                System.out.println("RabbitMQ路由失败，返回的 routeKey ：" + routeKey + " ,message : " + message);
            }
        });
        //声明路由键/消息体
        String[] routeKeys = {"justin", "willon"};
        for(int i = 0;i < 6;i++){
            String routeKey = routeKeys[i % 2];
            //发送消息
            String msg = "Hello RabbitMQ" + (i+1) + ("_" + System.currentTimeMillis());
            //发消息,true 表示加上了失败的通知
            channel.basicPublish(EXCHANGE_NAME, routeKey, true,MessageProperties.PERSISTENT_BASIC, msg.getBytes());
            System.out.println("---------------------------------------------------");
            System.out.println("Send Messages：" + routeKey + ":" + msg);
        }
        //关闭信道
        //关闭连接
        //channel.close();
        //connection.close();
    }
}
