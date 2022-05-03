package cn.enjoyedu.exchange.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 一个队列多个消费者，则会表现出消息在消费者之间的轮询发送
 */
public class MultiConsumerOneQueue {

    public static void main(String[] args) throws IOException, TimeoutException {
        //连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //连接rabbitMQ地址
        factory.setHost("localhost");
        //打开连接
        Connection connection = factory.newConnection();
        //创建3个线程，线程之间共享队列，一个队列多个消费者
        String queueName = "focusAll";
        for(int i = 0;i < 3; i++){
            //将队列名称作为参数，传递给每个线程
            Thread worker = new Thread(new ConsumerWorker(connection, queueName));
            worker.start();
        }
    }

    public static class ConsumerWorker implements Runnable{

        final Connection connection;
        final String queueName;

        public ConsumerWorker(Connection connection, String queueName){
            this.connection = connection;
            this.queueName = queueName;
        }
        public void run() {
            try {
                //创建一个信道，每个线程单独一个信道
                final Channel channel = connection.createChannel();
                //信道设置交换器类型
                channel.exchangeDeclare(DirectProducer.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
                //声明一个队列，若是队列已存在，不会重复创建
                channel.queueDeclare(queueName, false, false, false, null);
                //打印输入消费者的名称
                final String consumerName = Thread.currentThread().getName();
                //队列绑定到交换器上时，是允许绑定多个路由键的，也就是多重绑定
                String[] routeKeys = {"justin", "willon", "john"};
                for(String routeKey : routeKeys){
                    channel.queueBind(queueName, DirectProducer.EXCHANGE_NAME, routeKey);
                }
                System.out.println("[" + consumerName + "] waiting messages ...");
                //创建队列消费者
                final Consumer consumer = new DefaultConsumer(channel){
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        String message = new String(body, "UTF-8");
                        System.out.println(consumerName + " Received " + envelope.getRoutingKey() +":" + message);
                    }
                };
                channel.basicConsume(queueName, true, consumer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
