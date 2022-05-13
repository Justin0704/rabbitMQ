package cn.enjoyedu.dlx;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 正常消费
 */
public class DlxProcessConsumer {

    public static final String DLX_EXCHANGE_NAME = "dlx_accept";

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        //创建连接
        Connection connection = factory.newConnection();
        //创建信道
        final Channel channel = connection.createChannel();
        //添加一个交换器, 标记为直接交换器
        channel.exchangeDeclare(DLX_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //TODO 声明一个队列
        String queueName = "dlx_accept";
        channel.queueDeclare(queueName, false, false, false, null);
        //绑定，将队列和交换器通过路由键绑定
        channel.queueBind(queueName, DLX_EXCHANGE_NAME, "#");
        System.out.println("Waiting for the messages ......");
        //声明一个死信消费者
        final Consumer consumerDead = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Received [" + envelope.getRoutingKey() + "]" + message);
            }
        };
        //消费者正式在指定队列上消费消息
        channel.basicConsume(queueName, false, consumerDead);
    }
}
