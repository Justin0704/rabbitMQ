package cn.enjoyedu.rejectmsg;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息消费的拒绝
 * Reject(单条消息处理) Nack (批量)
 */
public class RejectRequeueConsumer {
    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        //创建连接
        Connection connection = factory.newConnection();
        //创建信道
        final Channel channel = connection.createChannel();
        //添加一个交换器, 标记为直接交换器
        channel.exchangeDeclare(RejectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //声明一个队列
        String queueName = "focuserror";
        channel.queueDeclare(queueName, false, false, false, null);
        //绑定，将队列和交换器通过路由键进行绑定
        String routeKey = "error";
        channel.queueBind(queueName, RejectProducer.EXCHANGE_NAME, routeKey);

        System.out.println("[*] waiting for the messages ... ");

        //声明一个拒绝的消费者
        final Consumer rejectConsumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                try{
                    String message = new String(body, "UTF-8");
                    System.out.println("Received [" + envelope.getRoutingKey() + "]" + message);
                    throw new RuntimeException("处理异常：" + message);
                }catch (Exception e){
                    /**
                     * TODO Reject方式拒绝, （第二个参数决定是否重新投递）
                     * long deliveryTag, boolean requeue
                     */
                    channel.basicReject(envelope.getDeliveryTag(), true);
                    /**
                     * TODO Nack方式拒绝（第二个参数决定是否批量）
                     * long deliveryTag, boolean multiple, boolean requeue
                     */
                    //channel.basicNack(envelope.getDeliveryTag(), false, true);
                }
            }
        };
        /**
         * 消费者正式在指定队列上消费消息
         * autoAck = false, 消费者拒绝
         */
        channel.basicConsume(queueName, false, rejectConsumer);
    }
}
