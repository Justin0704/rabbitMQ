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

3、topic交换器

'#' 表示接收所有消息

'justin.#' 表示接收justin的所有消息

'justin.#.A' 表示接收justin下的A消息

'#.A' 表示接收所有关于A的消息

4、生产者 - 消息发布时的权衡

无保障

失败通知（发送消息时设置mandatory标志）

发布者确认（一般确认、批量确认、异步监听确认）

备用交换器(如果主交换器无法路由消息，那么消息将被路由到这个新的备用交换器)

高可用队列、

事务、

事务+高可用队列、

消息的持久化

5、消息消费的拒绝

Reject(单条消息处理) Nack (批量)

消息的重新投递：requeue=true

6、死信交换器：

若是设置requeue=false，或消息过期，或超出队列范围（最先进入的消息会丢失）

则消息会丢失，可以使用死信交换器获取丢失的数据，然后重新投递

备用交换器：生产者生产的消息还没有进入队列

死信交换器：针对消费者消费的消息，消费者拒绝消息后进入死信队列

临时队列：数据内存中

持久化队列：数据在磁盘

7、rabbitMQ的实现

发送方的确认，失败通知（确保消息能够到达MQ）

消息持久化（交换器、队列、消息都要进行持久化）

确保消息一定能够被消费，手动应答

8、RabbitMQ安装 

安装过程

在Linux(以CentOS7为例)下安装RabbitMQ

1、wget https://packages.erlang-solutions.com/erlang-solutions-1.0-1.noarch.rpm

2、rpm -Uvh erlang-solutions-1.0-1.noarch.rpm

3、yum install epel-release

4、yum install erlang

5、wget http://www.rabbitmq.com/releases/rabbitmq-server/v3.6.6/rabbitmq-server-3.6.6-1.el7.noarch.rpm

6、yum install rabbitmq-server-3.6.6-1.el7.noarch.rpm

RabbitMQ常用端口

client端通信端口： 5672      

管理端口 ： 15672   

server间内部通信端口： 25672

可能的问题

如端口出现不能访问，考虑是否防火墙问题，可以使用形如以下命令开启或直接关闭防火墙：

firewall-cmd --add-port=15672/tcp --permanent

运行rabbitmqctl status出现Error: unable to connect to node rabbit@controller: nodedown之类问题考虑如下几种解决办法： 

1、重启服务 

service rabbitmq-server stop

service rabbitmq-server start

2、检查/var/lib/rabbitmq中是否存在.erlang.cookie，没有则新建一个，里面随便输入一段字符串 

3、重新安装服务 

4、百度或者Google一下 

管理RabbitMQ 

管理

日志一般存放位置 

/var/log/rabbitmq/rabbit@centosvm.log 

/var/log/rabbitmq/rabbit@centosvm-sasl.log 

管理虚拟主机 

rabbitmqctl add_vhost [vhost_name] 

rabbitmqctl list_vhosts 

启动和关闭rabbitmq

以服务方式

service rabbitmq-server stop

service rabbitmq-server start

service rabbitmq-server status

以应用程序方式

rabbitmq-server会启动Erlang节点和Rabbitmq应用 

rabbitmqctl stop会关闭Erlang节点和Rabbitmq应用 

rabbitmqctl status 可以检查消息节点是否正常

Rabbitmq配置文件放在 /etc/rabbitmq 下，名为rabbitmq.config，没有且需要使用则可以自己新建一个

单独关闭RabbitMQ应用

rabbitmqctl stop_app关闭Rabbitmq应用 

rabbitmqctl start_app启动Rabbitmq应用

用户管理 

rabbitmqctl add_user [username] [pwd]

rabbitmqctl delete_user [username]

rabbitmqctl  change_password  Username  Newpassword

rabbitmqctl  list_users

用户权限控制 

guest用户

guest是默认用户，具有默认virtual host "/"上的全部权限，仅能通过localhost访问RabbitMQ包括Plugin，建议删除或更改密码。可通过将配置文件中loopback_users来取消其本地访问的限制：[{rabbit, [{loopback_users, []}]}]

用户权限

用户仅能对其所能访问的virtual hosts中的资源进行操作。这里的资源指的是virtual hosts中的exchanges、queues等，操作包括对资源进行配置、写、读。配置权限可创建、删除、资源并修改资源的行为，写权限可向资源发送消息，读权限从资源获取消息。比如：

exchange和queue的declare与delete分别需要：exchange和queue上的配置权限

queue的bind与unbind需要：queue写权限，exchange的读权限

发消息(publish)需exchange的写权限

获取或清除(get、consume、purge)消息需queue的读权限

对何种资源具有配置、写、读的权限通过正则表达式来匹配，具体命令如下：

rabbitmqctl set_permissions [-p <vhostpath>] <user> <conf> <write> <read>

如用户Mark在虚拟主机logHost上的所有权限： 

rabbitmqctl set_permissions –p logHost Mark  '.*'  '.*'  '.*' 

设置用户角色：

rabbitmqctl  set_user_tags  User  Tag

User为用户名， Tag为角色名(对应于下面的administrator，monitoring，policymaker，management)

RabbitMQ的用户角色分类

none、management、policymaker、monitoring、administrator

none

不能访问 management plugin，通常就是普通的生产者和消费者

management

普通的生产者和消费者加：

列出自己可以通过AMQP登入的virtual hosts  

查看自己的virtual hosts中的queues, exchanges 和 bindings

查看和关闭自己的channels 和 connections

查看有关自己的virtual hosts的“全局”的统计信息，包含其他用户在这些virtual hosts中的活动。

policymaker 

management可以做的任何事加：

查看、创建和删除自己的virtual hosts所属的policies和parameters

monitoring  

management可以做的任何事加：

列出所有virtual hosts，包括他们不能登录的virtual hosts

查看其他用户的connections和channels

查看节点级别的数据如clustering和memory使用情况

查看真正的关于所有virtual hosts的全局的统计信息

administrator   

policymaker和monitoring可以做的任何事加:

创建和删除virtual hosts

查看、创建和删除users

查看创建和删除permissions

关闭其他用户的connections

查看

查看队列 

rabbitmqctl list_queues

查看交换器 

rabbitmqctl list_exchanges

查看绑定 

rabbitmqctl list_bindings 

RabbitMQ集群 

RabbitMQ內建集群

內建集群的设计目标

1、允许消费者和生产者在节点崩溃的情况下继续运行；2、通过添加节点线性扩展消息通信的吞吐量。

可以保证消息的万无一失吗？

不行，当一个节点崩溃时，该节点上队列的消息也会消失，rabbitmq默认不会将队列的消息复制到整个集群上。

集群中的队列和交换器

队列

集群中队列信息只在队列的所有者节点保存队列的所有信息，其他节点只知道队列的元数据和指向所有者节点的指针，节点崩溃时，该节点的队列和其上的绑定信息都消失了。

为什么集群不复制队列内容和状态到所有节点：1）存储空间；2）性能，如果消息需要复制到集群中每个节点，网络开销不可避免，持久化消息还需要写磁盘。

所以其他节点接收到不属于该节点的队列的消息时会将该消息传递给该队列的所有者节点上。

交换器

本质上是个这个交换器的名称和队列的绑定列表，可以看成一个类似于hashmap的映射表，所以交换器会在整个集群上复制。

元数据

队列元数据：队列名称和属性（是否可持久化，是否自动删除）

交换器元数据：交换器名称、类型和属性

绑定元数据：交换器和队列的绑定列表

vhost元数据：vhost内的相关属性，如安全属性等等

集群中的节点

要么是内存节点，要么是磁盘节点。怎么区分？就是节点将队列、交换器、用户等等信息保存在哪里？单节点肯定是磁盘类型。集群中可以有内存节点，为了性能的考虑，全部是磁盘节点，当声明队列、交换器等等时，rabbitmq必须将数据保存在所有节点后才能表示操作完成。

Rabbitmq只要求集群中至少有一个磁盘节点，从高可用的角度讲每个集群应该至少配备两个磁盘节点。因为只有一个磁盘节点的情况下，当这个磁盘节点崩溃时，集群可以保持运行，但任何修改操作，比如创建队列、交换器、添加和删除集群节点都无法进行。

构建我们自己的集群

集群常用命令

rabbitmqctl join_cluster [rabbit@node1]将节点加入集群

rabbitmqctl cluster_status 查询集群状态

rabbitmqctl reset 严格来说，不属于集群命令，reset的作用是将node节点恢复为空白状态，包括但不限于，比如，用户信息，虚拟主机信息，所有持久化的消息。在集群下，通过这个命令，可以让节点离开集群。

集群下的注意事项

元数据的变更，我们知道，这些消息都要记录在磁盘节点上。当有节点离开集群时，所有的磁盘节点上都要记录这个信息。如果磁盘节点在离开集群时不用reset命令，会导致集群认为该节点发生了故障，并会一直等待该节点恢复才允许新节点加入，所以，当磁盘节点是被暴力从集群中脱离时，有可能导致集群永久性的无法变更。

本机集群(建议不要随意尝试)：

RABBITMQ_NODE_PORT=5672 RABBITMQ_NODENAME=rabbit rabbitmq-server -detached 

RABBITMQ_NODE_PORT=5673 RABBITMQ_NODENAME=rabbit_1 rabbitmq-server -detached 

RABBITMQ_NODE_PORT=5674 RABBITMQ_NODENAME=rabbit_2 rabbitmq-server -detached 

rabbitmqctl -n rabbit_1@centosvm stop_app

rabbitmqctl -n rabbit_1@centosvm reset

rabbitmqctl -n rabbit_1@centosvm join_cluster rabbit@centosvm

rabbitmqctl -n rabbit_1@centosvm start_app

rabbitmqctl cluster_status

rabbitmqctl -n rabbit_2@centosvm stop_app

rabbitmqctl -n rabbit_2@centosvm reset

rabbitmqctl -n rabbit_2@centosvm join_cluster rabbit@centosvm --ram 

rabbitmqctl -n rabbit_2@centosvm start_app

rabbitmqctl cluster_status

从外部要访问虚拟机中的mq记得在防火墙中打开端口

firewall-cmd --add-port=5673/tcp --permanent  

firewall-cmd --add-port=5674/tcp --permanent  



rabbitmqctl add_user mq mq

rabbitmqctl set_permissions mq ".*" ".*" ".*"

rabbitmqctl set_user_tags mq administrator

rabbitmq-plugins -n rabbit_1@centosvm enable rabbitmq_management

多机下的集群

Rabbitmq集群对延迟非常敏感，只能在本地局域网内使用。

1、	修改 /etc/hosts

192.168.1.1 node1

192.168.1.2 node2

192.168.1.3 node3

2、Erlang Cookie 文件：/var/lib/rabbitmq/.erlang.cookie。将 node1 的该文件复制到 node2、node3，由于这个文件权限是 400，所以需要先修改 node2、node3 中的该文件权限为 777，然后将 node1 中的该文件拷贝到 node2、node3，最后将权限和所属用户/组修改回来。

3、运行各节点

4、在node2、node3上分别运行

[root@node2 ~]# rabbitmqctl stop_app

[root@node2 ~]./rabbitmqctl reset

[root@node2 ~]# rabbitmqctl join_cluster rabbit@node1

[root@node2 ~]# rabbitmqctl start_app

rabbitmqctl cluster_status

内存节点则是rabbitmqctl join_cluster rabbit@node1 --ram

移除集群中的节点

[root@node2 ~]# rabbitmqctl stop_app

[root@node2 ~]./rabbitmqctl reset

[root@node2 ~]# rabbitmqctl start_app

RabbitMQ集群高可用

镜像队列

什么是镜像队列

如果RabbitMQ集群是由多个broker节点构成的，那么从服务的整体可用性上来讲，该集群对于单点失效是有弹性的，但是同时也需要注意：尽管exchange和binding能够在单点失效问题上幸免于难，但是queue和其上持有的message却不行，这是因为queue及其内容仅仅存储于单个节点之上，所以一个节点的失效表现为其对应的queue不可用。

引入RabbitMQ的镜像队列机制，将queue镜像到cluster中其他的节点之上。在该实现下，如果集群中的一个节点失效了，queue能自动地切换到镜像中的另一个节点以保证服务的可用性。在通常的用法中，针对每一个镜像队列都包含一个master和多个slave，分别对应于不同的节点。slave会准确地按照master执行命令的顺序进行命令执行，故slave与master上维护的状态应该是相同的。除了publish外所有动作都只会向master发送，然后由master将命令执行的结果广播给slave们，故看似从镜像队列中的消费操作实际上是在master上执行的。

RabbitMQ的镜像队列同时支持publisher confirm和事务两种机制。在事务机制中，只有当前事务在全部镜像queue中执行之后，客户端才会收到Tx.CommitOk的消息。同样的，在publisher confirm机制中，向publisher进行当前message确认的前提是该message被全部镜像所接受了。

镜像队列的配置

代码

Map<String, Object> args = new HashMap<String, Object>();

args.put("x-ha-policy", "all");

在声明队列时传入channel.queueDeclare(queueName,false,false, false, args);

控制台

镜像队列的配置通过添加policy完成，policy添加的命令为：

rabbitmqctl set_policy [-p Vhost] Name Pattern Definition [Priority]

-p Vhost： 可选参数，针对指定vhost下的queue进行设置

Name: policy的名称

Pattern: queue的匹配模式(正则表达式)

Definition：镜像定义，包括三个部分ha-mode, ha-params, ha-sync-mode

    ha-mode:指明镜像队列的模式，有效值为 all/exactly/nodes

        all：表示在集群中所有的节点上进行镜像

        exactly：表示在指定个数的节点上进行镜像，节点的个数由ha-params指定

        nodes：表示在指定的节点上进行镜像，节点名称通过ha-params指定

    ha-params：ha-mode模式需要用到的参数

    ha-sync-mode：进行队列中消息的同步方式，有效值为automatic和manual

priority：可选参数，policy的优先级

例如，对队列名称以“queue_”开头的所有队列进行镜像，并在集群的两个节点上完成进行，policy的设置命令为：

rabbitmqctl set_policy ha-queue-two "^queue_" '{"ha-mode":"exactly","ha-params":2,"ha-sync-mode":"automatic"}'

windows下将单引号改为双引号(rabbitmqctl set_policy ha-all “^ha.” “{“”ha-mode”“:”“all”“}”)

补充：

可通过如下命令确认哪些salve在同步：

rabbitmqctl list_queues name slave_pids synchronised_slave_pids

手动同步queue：

rabbitmqctl sync_queue name

取消queue同步：

rabbitmqctl cancel_sync_queue name

使用HAProxy 

处理节点选择，故障服务器检测和负载分布可以使用HAProxy，具体如何安装，参考文档《安装HAProxy》 

RabbitMQ的Web控制台

运行rabbitmq-plugins enable rabbitmq_management  

重启RabbitMQ

在浏览中打开http://localhost:15672

9、VirtualBox中的虚拟机构建RabbitMQ集群

======已安装好RabbitMQ的虚拟机镜像，命名为node1，假设ip地址为192.168.56.103

======修改 /etc/hosts

vi /etc/hosts

======加入

192.168.56.103 node1

192.168.56.101 node2

======将01的hostname修改为node01

vi /etc/hostname

======将node1虚拟机镜像拷贝一份，命名为node2，假设ip地址为192.168.56.101

======确保启动后两者可以通过ssh命令互联

======修改node2 /etc/hosts

vi /etc/hosts

======加入

192.168.56.103 node1

192.168.56.101 node2

======将node2的hostname修改为node2

vi /etc/hostname



======将node1上/var/lib/rabbitmq/.erlang.cookie内容拷贝至node2的.erlang.cookie中，由于这个文件权限是400，所以======需要先修改node2中的该文件权限为 777，拷贝完成后，最后将权限和所属用户/组修改回来。

chown 777 .erlang.cookie

复制内容

chown 400 .erlang.cookie



======node1和node2均要执行

firewall-cmd --permanent --add-port=15672/tcp

firewall-cmd --permanent --add-port=5672/tcp

firewall-cmd --permanent --add-port=25672/tcp

firewall-cmd --permanent --add-port=4369/tcp

firewall-cmd --reload

======如果怀疑有端口未打开，使用以下命令检查，返回yes表示成功

firewall-cmd --query-port=4369/tcp

======启动node1和node2的RabbitMQ

service rabbitmq-server start

======方便操作使用 service rabbitmq-server stop

======方便操作使用 service rabbitmq-server status

======如果node2的RabbitMQ启动失败，需要

vi /etc/rabbitmq/rabbitmq-env.conf

======加入

NODENAME=rabbit2@node2

======再次启动node2的RabbitMQ

======在node2上执行

rabbitmqctl stop_app

rabbitmqctl reset

rabbitmqctl join_cluster rabbit@node1

======内存节点则是 rabbitmqctl join_cluster rabbit@node1 --ram

rabbitmqctl start_app

======检查集群状态（node1和node2均可）

rabbitmqctl cluster_status

======移除集群中的node2

rabbitmqctl stop_app

rabbitmqctl reset

rabbitmqctl start_app
