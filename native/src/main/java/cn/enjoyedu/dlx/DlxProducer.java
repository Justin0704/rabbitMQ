package cn.enjoyedu.dlx;

/**
 * 死信交换器的用法
 * 若是设置requeue=false，或消息过期，或超出队列范围（最先进入的消息会丢失）
 * 则消息会丢失，可以使用死信交换器获取丢失的数据，然后重新投递
 */
public class DlxProducer {
    public static void main(String[] args) {

    }
}
