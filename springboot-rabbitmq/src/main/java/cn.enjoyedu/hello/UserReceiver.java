package cn.enjoyedu.hello;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Component
public class UserReceiver implements ChannelAwareMessageListener{

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try{
            String msg = new String(message.getBody(), "UTF-8");
            System.out.println("UserReceiver接收到的消息：" + msg);
            try{
                //消费者手动应答（原因，手动应答若是失败，则可以通过nack拒绝，重新处理消息，使消息不会丢失）
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                System.out.println("UserReceiver消息已消费");
            }catch (Exception ex){
                System.out.println(ex.getMessage());
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);//requeue=true，则消息重发
                System.out.println("UserReceiver拒绝消息，要求MQ重新发送");
                throw ex;
            }

        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
}
