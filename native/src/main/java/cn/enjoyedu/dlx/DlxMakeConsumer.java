package cn.enjoyedu.dlx;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 普通消费者，自己无法消费的消息，将投入到死信队列
 */
public class DlxMakeConsumer {
    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        //创建连接
        Connection connection = factory.newConnection();
        //创建信道
        final Channel channel = connection.createChannel();
        //添加一个交换器, 标记为直接交换器
        channel.exchangeDeclare(DlxProducer.EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //TODO 绑定死信交换器
        //TODO 声明一个队列，并绑定死信交换器
        String queueName = "dlx_make";
        Map<String, Object> argsMap = new HashMap<String, Object>();
        argsMap.put("x-dead-letter-exchange", DlxProcessConsumer.DLX_EXCHANGE_NAME);
        //死信路由键，会替换掉原来的路由键
        //argsMap.put("x-dead-letter-routing-key", "deal");
        channel.queueDeclare(queueName, false, false, false, argsMap);

        //绑定，将队列和交换器通过路由键进行绑定
        channel.queueBind(queueName, DlxProducer.EXCHANGE_NAME, "#");
        System.out.println("[*] waiting for the messages ...");

        //声明一个消费者
        final Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                //TODO 若是justin的消息则确认
                if(envelope.getRoutingKey().equals("justin")){
                    System.out.println("Received [" + envelope.getRoutingKey() + "]" + message);
                    //消费者确认
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }else{
                    // TODO 若是其它的消息则拒绝（requeue=false），成为死信消息
                    System.out.println("Rejected [" + envelope.getRoutingKey() + "]" + message);
                    //消费者拒绝
                    channel.basicReject(envelope.getDeliveryTag(), false);
                }
            }
        };
        //消费者正式在指定队列上消费消息
        channel.basicConsume(queueName, false, consumer);
    }
}
