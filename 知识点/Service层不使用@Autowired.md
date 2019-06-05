
这个项目Service没有使用@Autowire，从Spring 4.3开始，就不需要在构造函数上添加@Autowired注释

```java
@Service
public class TodoService {

    private final TodoRepository stockRepository;

    // @Autowired
    public TodoService(TodoRepository stockRepository) {
        this.stockRepository = stockRepository;
    }
}

```

> 参考链接 https://stackoverflow.com/questions/51199885/spring-boot-how-to-access-repository-in-java-class-autowired-not-working

推荐使用构造方法注入实例

构造方法加载顺序 > @Autowired



stackoverflow的答案

你不应该在构造函数中加入任何逻辑。构造方法只是初始化您的实例。storeRepository应该在单独的方法中调用。

```java
@Component
public class StoreWorker {
    @Autowired
    private StoreRepository storeRepository;

    public void findAll() {
        System.out.println(storeRepository.findAll()); 
    }
}
```

当Spring调用你的构造函数时，上下文没有完全初始化，因为你没有使用构造函数注入，所以storeRepository还没有注入。

构造函数注入是Spring团队推荐的注入方法。从Spring 4.3开始，您甚至不需要@Autowired在构造函数上添加注释。

```java
@Component
public class StoreWorker {

    private StoreRepository storeRepository;

    //@Autowired not needed since v4.3
    public StoreWorker(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    ...
}
```

如果您想依赖于其他外部repository 实例进一步初始化bean，请在另外注释的方法中进行@PostConstruct。

```java
@Component
public class StoreWorker {

    private StoreRepository storeRepository;
    private List<Store> stores;

    public StoreWorker(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @PostConstruct
    public void initializeMyBean() {
        stores = storeRepository.findAll(); 
    }
}
```