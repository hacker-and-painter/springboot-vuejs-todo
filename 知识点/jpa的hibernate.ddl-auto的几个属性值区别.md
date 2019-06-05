jpa是一种规范，但是对jpa支持最好的就是hibernate，所以一般来说jpa指的就是hibernate实现的jpa。 

在使用springboot时，使用jpa，加上jpa的起步依赖spring-boot-starter-data-jpa即可。 

在配置文件中除了配置数据源以外，还可以配置一些方便开发使用的选项，如spring.jpa.show-sql=true,这个属性可以在操作数据库时显示sql语句。 

还有一个属性值比较常用：spring.jpa.hibernate.ddl-auto，其属性值作用区别介绍如下：

create： 

每次加载hibernate时都会删除上一次的生成的表，然后根据你的model类再重新来生成新表，哪怕两次没有任何改变也要这样执行，这就是导致数据库表数据丢失的一个重要原因。 

create-drop ： 

每次加载hibernate时根据model类生成表，但是sessionFactory一关闭,表就自动删除。

update： 

最常用的属性，第一次加载hibernate时根据model类会自动建立起表的结构（前提是先建立好数据库），以后加载hibernate时根据 model类自动更新表结构，即使表结构改变了但表中的行仍然存在不会删除以前的行。要注意的是当部署到服务器后，表结构是不会被马上建立起来的，是要等 应用第一次运行起来后才会。 

validate ： 

每次加载hibernate时，验证创建数据库表结构，只会和数据库中的表进行比较，不会创建新表，但是会插入新值。


