# rabbitMQ
消息中间件 - rabbitMQ学习

涉及的要素：生产者、消费者、消息、交换器、队列、绑定、路由键
![rabbitmq](https://user-images.githubusercontent.com/23186519/164752902-41ce1989-9424-4c38-b45e-c611f4d2f71e.png)

消息的确认：消费者收到的每条消息都碧玺进行确认（自动确认和自行确认）

交换器类型：direct交换器、fanout交换器、topic交换器 header交换器

1、direct交换器，4中算法

生产者和消费者的而一般用法：发送到绑定的路由键

队列交换器的多重绑定：每个路由键均收到消息

一个连接多个信道：每个信道重复发送数据

一个队列多个消费者：轮询的方式发送数据，不会出现重复数据

2、fanout交换器：

fanout交换器和路由键没有关系，即使使用一个不存在的路由键也能收到消息
