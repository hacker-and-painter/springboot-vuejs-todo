# 理解JPA注解@GeneratedValue

### 一、JPA通用策略生成器

通过annotation来映射hibernate实体的,基于annotation的hibernate主键标识为@Id, 
其生成规则由@GeneratedValue设定的.这里的@id和@GeneratedValue都是JPA的标准用法, 
JPA提供四种标准用法,由@GeneratedValue的源代码可以明显看出.

```
Target({METHOD,FIELD})    
    @Retention(RUNTIME)    
    public @interface GeneratedValue{    
        GenerationType strategy() default AUTO;    
        String generator() default "";    
    }   
```

其中GenerationType:

```
public enum GenerationType{    
    TABLE,    
    SEQUENCE,    
    IDENTITY,    
    AUTO   
}  
```

JPA提供的四种标准用法为TABLE,SEQUENCE,IDENTITY,AUTO.

>  TABLE：使用一个特定的数据库表格来保存主键。
> SEQUENCE：根据底层数据库的序列来生成主键，条件是数据库支持序列。
> IDENTITY：主键由数据库自动生成（主要是自动增长型）
> AUTO：主键由程序控制。 
> TABLE比较复杂，这里不讲解。分别介绍其他三个:

#### 1.SEQUENCE

实体类中的注解

```java
@Id  
@GeneratedValue(strategy =GenerationType.SEQUENCE,generator="aaa")  
@SequenceGenerator(name="aaa", sequenceName="seq_payment")  
```

@SequenceGenerator定义

```java
@Target({TYPE, METHOD, FIELD})   
@Retention(RUNTIME)  
public @interface SequenceGenerator {  
 String name();  
 String sequenceName() default "";  
 int initialValue() default 0;  
 int allocationSize() default 50;  
}  
```

> name属性表示该表主键生成策略的名称，它被引用在@GeneratedValue中设置的“generator”值中。
> sequenceName属性表示生成策略用到的数据库序列名称。
> initialValue表示主键初识值，默认为0。
> allocationSize表示每次主键值增加的大小，例如设置成1，则表示每次创建新记录后自动加1，默认为50。

#### 2.IDENTITY

主键则由数据库自动维护，使用起来很简单

```java
@Id  
@GeneratedValue(strategy = GenerationType.IDENTITY)  12
```

#### 3、AUTO

默认的配置。如果不指定主键生成策略，默认为AUTO。

```java
@Id  
@GeneratedValue(strategy = GenerationType.AUTO)  
```

### 二、hibernate主键策略生成器

hibernate提供多种主键生成策略，有点是类似于JPA，有的是hibernate特有，下面列出几个Hibernate比较常用的生成策略:

> - native: 对于 oracle 采用 Sequence 方式，对于MySQL 和 SQL Server 采用identity（自增主键生成机制），native就是将主键的生成工作交由数据库完成，hibernate不管
> - uuid: 采用128位的uuid算法生成主键，uuid被编码为一个32位16进制数字的字符串。占用空间大（字符串类型）。
>   - assigned: 在插入数据的时候主键由程序处理（即程序员手动指定），这是 <generator>元素没有指定时的默认生成策略。等同于JPA中的AUTO。
>   - identity: 使用SQL Server 和 MySQL 的自增字段，这个方法不能放到 Oracle 中，Oracle 不支持自增字段，要设定sequence（MySQL 和 SQL Server 中很常用）。 等同于JPA中的INDENTITY。
>   - increment: 插入数据的时候hibernate会给主键添加一个自增的主键，但是一个hibernate实例就维护一个计数器，所以在多个实例运行的时候不能使用这个方法。

hibernate提供了多种生成器供选择,基于Annotation的方式通过@GenericGenerator实现. 
hibernate每种主键生成策略提供接口org.hibernate.id.IdentifierGenerator的实现类,如果要实现自定义的主键生成策略也必须实现此接口.

```java
public interface IdentifierGenerator {  
    /** 
     * The configuration parameter holding the entity name 
     */  
    public static final String ENTITY_NAME = "entity_name";  

  /** 
   * Generate a new identifier. 
   * @param session 
   * @param object the entity or toplevel collection for which the id is being generated 
   * 
   * @return a new identifier 
   * @throws HibernateException 
   */  
  public Serializable generate(SessionImplementor session, Object object)   
    throws HibernateException;  
}  
```

IdentifierGenerator提供一generate方法,generate方法返回产生的主键.

### 三、@GenericGenerator

自定义主键生成策略，由@GenericGenerator实现。 
hibernate在JPA的基础上进行了扩展，可以用一下方式引入hibernate独有的主键生成策略，就是通过@GenericGenerator加入的。

比如说，JPA标准用法

```java
@Id  
@GeneratedValue(GenerationType.AUTO)  
```

就可以用hibernate特有以下用法来代替:

```java
@Id
@GeneratedValue(generator = "paymentableGenerator")    
@GenericGenerator(name = "paymentableGenerator", strategy = "assigned")  
```

@GenericGenerator的定义:

```java
@Target({PACKAGE, TYPE, METHOD, FIELD})  
@Retention(RUNTIME)  
```

源代码如下:

```java
public @interface GenericGenerator {  
 /** 
  * unique generator name 
  */  
 String name();  
 /** 
  * Generator strategy either a predefined Hibernate 
  * strategy or a fully qualified class name. 
  */  
 String strategy();  
 /** 
  * Optional generator parameters 
  */  
 Parameter[] parameters() default {};  
}  
```

> - name属性指定生成器名称。
> - strategy属性指定具体生成器的类名。
> - parameters得到strategy指定的具体生成器所用到的参数。

对于这些hibernate主键生成策略和各自的具体生成器之间的关系,在org.hibernate.id.IdentifierGeneratorFactory中指定了,

```java
static {  
  GENERATORS.put("uuid", UUIDHexGenerator.class);  
  GENERATORS.put("hilo", TableHiLoGenerator.class);  
  GENERATORS.put("assigned", Assigned.class);  
  GENERATORS.put("identity", IdentityGenerator.class);  
  GENERATORS.put("select", SelectGenerator.class);  
  GENERATORS.put("sequence", SequenceGenerator.class);  
  GENERATORS.put("seqhilo", SequenceHiLoGenerator.class);  
  GENERATORS.put("increment", IncrementGenerator.class);  
  GENERATORS.put("foreign", ForeignGenerator.class);  
  GENERATORS.put("guid", GUIDGenerator.class);  
  GENERATORS.put("uuid.hex", UUIDHexGenerator.class); //uuid.hex is deprecated  
  GENERATORS.put("sequence-identity", SequenceIdentityGenerator.class);  
}  
```

上面十二种策略，加上native，hibernate一共默认支持十三种生成策略。使用hibernate注解示例如下:

```java
@Id
@GeneratedValue(generator = "IDGenerator")
@GenericGenerator(name = "IDGenerator", strategy = "identity")
```

这种完全类似于:

```java
@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
```