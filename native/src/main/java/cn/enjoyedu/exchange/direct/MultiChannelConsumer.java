package cn.enjoyedu.exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 一个连接多个信道
 */
public class MultiChannelConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {

        //连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //连接rabbitMQ地址
        factory.setHost("127.0.0.1");
        //打开连接
        Connection connection = factory.newConnection();
        //一个连接多个信道
        for(int i=0;i<2; i++){
            //将连接作为参数，传递给每个线程
            Thread worker = new Thread(new ConsumerWorder(connection));
            worker.start();
        }
    }


    public static class ConsumerWorder implements Runnable{
        final Connection connection;
        public ConsumerWorder(Connection connection){
            this.connection = connection;
        }
        public void run() {
            try {
                //创建信道，意味着每个线程单独一个信道
                Channel channel = connection.createChannel();
                //信道设置交换器类型（direct）
                channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
                //声明一个随机队列
                String queueName = channel.queueDeclare().getQueue();
                //消费者名字， 打印输出用
                final String consumerName = Thread.currentThread().getName() + "-ALL";
                //队列绑定到交换器上时，是允许绑定多个路由键，也就是多重绑定
                String[] routeKeys = {"justin", "willon", "john"};
                for(String routeKey : routeKeys){
                    channel.queueBind(queueName, DirectProducer.EXCHANGE_NAME, routeKey);
                }
                System.out.println("[" + consumerName + "] waiting for messages ...");
                //创建队列的消费者
                final Consumer consumer = new DefaultConsumer(channel){
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        String message = new String(body, "UTF-8");
                        System.out.println(consumerName + " Received " + envelope.getRoutingKey() + " : " + message);
                    }
                };
                channel.basicConsume(queueName, true, consumer);

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
