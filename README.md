# 使用 Spring Boot + vue 实现 TODO 应用

> 项目地址: [springboot-vuejs-todo](https://github.com/hacker-and-painter/springboot-vuejs-todo)
>
> 原文地址:  [Spring Boot VueJS Example with TodoMVC, REST APIs, JPA, Hibernate and MySQL](https://hellokoding.com/build-a-full-stack-todomvc-web-app-and-rest-api-service-with-spring-boot-jpa-hibernate-mysql-freemarker-vuejs-and-docker/)
>
> 原项目地址: [springboot-todomvc-mysql-vuejs](https://github.com/hellokoding/hellokoding-courses/tree/master/springboot-examples/springboot-todomvc-mysql-vuejs)
>
> 译者: 高行行

本教程将引导您使用TodoMVC，REST API，JPA，Hibernate和MySQL构建完整Spring Boot VueJS示例

## 效果展示

![](https://raw.githubusercontent.com/gaohanghang/images/master/img20190605201552.png)

### 需要安装的软件

- JDK 8+ or OpenJDK 8+
- Maven 3+
- MySQL Server 5+ or Docker CE 18+

## 项目结构和依赖项

### 项目结构

```java
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── hellokoding
│       │           └── springboot
│       │               └── fullstack
│       │                   ├── todo
│       │                   │   ├── Todo.java
│       │                   │   ├── TodoAPI.java
│       │                   │   ├── TodoController.java
│       │                   │   ├── TodoRepository.java
│       │                   │   └── TodoService.java
│       │                   └── Application.java
│       └── resources
│           ├── static
│           │   ├── todo.css
│           │   └── todo.js
│           ├── templates
│           │   └── todo.html
│           └── application.properties
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

### 项目依赖

[pom.xml](https://github.com/hellokoding/hellokoding-courses/blob/master/springboot-examples/springboot-todomvc-mysql-vuejs/pom.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.hellokoding.springboot</groupId>
    <artifactId>todomvc-mysql-vuejs</artifactId>
    <version>1.0-SNAPSHOT</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.1.RELEASE</version>
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-freemarker</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>javax.validation</groupId>
            <artifactId>validation-api</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

## 定义 JPA Entity, Repository and Service

[Todo.java](https://github.com/hellokoding/hellokoding-courses/blob/master/springboot-examples/springboot-todomvc-mysql-vuejs/src/main/java/com/hellokoding/springboot/fullstack/todo/Todo.java)

```java
package com.hellokoding.springboot.fullstack.todo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity

@Data
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    private String title;

    private boolean completed;

    private Date updatedDate = new Date();
}
```

[TodoRepository.java](https://github.com/hellokoding/hellokoding-courses/blob/master/springboot-examples/springboot-todomvc-mysql-vuejs/src/main/java/com/hellokoding/springboot/fullstack/todo/TodoRepository.java)

```java
package com.hellokoding.springboot.fullstack.todo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
```

[TodoService.java](https://github.com/hellokoding/hellokoding-courses/blob/master/springboot-examples/springboot-todomvc-mysql-vuejs/src/main/java/com/hellokoding/springboot/fullstack/todo/TodoService.java)

```java
package com.hellokoding.springboot.fullstack.todo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository stockRepository;

    public List<Todo> findAll() {
        return stockRepository.findAll();
    }

    public List<Todo> saveAll(List<Todo> todos) {
        return stockRepository.saveAll(todos);
    }
}
```

## Define REST API, Controller and View Template

### REST API and Controller

[TodoAPI.java](https://github.com/hellokoding/hellokoding-courses/blob/master/springboot-examples/springboot-todomvc-mysql-vuejs/src/main/java/com/hellokoding/springboot/fullstack/todo/TodoAPI.java)

```java
package com.hellokoding.springboot.fullstack.todo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/v1/todos")
@Slf4j
public class TodoAPI {
    private final TodoService stockService;

    @Autowired
    public TodoAPI(TodoService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> findAll() {
        return ResponseEntity.ok(stockService.findAll());
    }

    @PostMapping
    public ResponseEntity saveAll(@Valid @RequestBody List<Todo> todos) {
        log.info(todos.toString());
        return ResponseEntity.ok(stockService.saveAll(todos));
    }
}
```

[TodoController.java](https://github.com/hellokoding/hellokoding-courses/blob/master/springboot-examples/springboot-todomvc-mysql-vuejs/src/main/java/com/hellokoding/springboot/fullstack/todo/TodoController.java)

```java
package com.hellokoding.springboot.fullstack.todo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TodoController {
    @GetMapping("/")
    public String list(){
        return "todo";
    }
}
```

### FreeMarker/HTML View Template

[todo.html](https://github.com/hellokoding/hellokoding-courses/blob/master/springboot-examples/springboot-todomvc-mysql-vuejs/src/main/resources/templates/todo.html)

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Todo MVC using VueJS, Spring Boot, MySQL</title>
    <link href="https://unpkg.com/todomvc-app-css@2.0.6/index.css" rel="stylesheet"/>
    <link href="/todo.css" rel="stylesheet"/>
</head>
<body>

<section class="todoapp">
    <header class="header">
        <h1>todos</h1>
        <input class="new-todo"
               autofocus autocomplete="off"
               placeholder="What needs to be done?"
               v-model="newTodo"
               @keyup.enter="addTodo">
    </header>
    <section class="main" v-show="todos.length" v-cloak>
        <input class="toggle-all" type="checkbox" v-model="allDone">
        <ul class="todo-list">
            <li v-for="todo in filteredTodos"
                class="todo"
                :key="todo.id"
                :class="{ completed: todo.completed, editing: todo == editedTodo }">
                <div class="view">
                    <input class="toggle" type="checkbox" v-model="todo.completed">
                    <label @dblclick="editTodo(todo)">{{ todo.title }}</label>
                    <button class="destroy" @click="removeTodo(todo)"></button>
                </div>
                <input class="edit" type="text"
                       v-model="todo.title"
                       v-todo-focus="todo == editedTodo"
                       @blur="doneEdit(todo)"
                       @keyup.enter="doneEdit(todo)"
                       @keyup.esc="cancelEdit(todo)">
            </li>
        </ul>
    </section>
    <footer class="footer" v-show="todos.length" v-cloak>
    <span class="todo-count">
      <strong>{{ remaining }}</strong> {{ remaining | pluralize }} left
    </span>
        <ul class="filters">
            <li><a href="#/all" :class="{ selected: visibility == 'all' }">All</a></li>
            <li><a href="#/active" :class="{ selected: visibility == 'active' }">Active</a></li>
            <li><a href="#/completed" :class="{ selected: visibility == 'completed' }">Completed</a></li>
        </ul>
        <button class="clear-completed" @click="removeCompleted" v-show="todos.length > remaining">
            Clear completed
        </button>
    </footer>
</section>
<footer class="info">
    <p>Double-click to edit a todo</p>
</footer>

<script src="https://unpkg.com/vue@2.5.8/dist/vue.js"></script>
<script src="https://unpkg.com/axios@0.17.1/dist/axios.min.js"></script>
<script src="/todo.js"></script>

</body>
</html>
```

### Static Files

[todo.js](https://github.com/hellokoding/hellokoding-courses/blob/master/springboot-examples/springboot-todomvc-mysql-vuejs/src/main/resources/static/todo.js)

```js
// Full spec-compliant TodoMVC with localStorage persistence
// and hash-based routing in ~120 effective lines of JavaScript.

// localStorage persistence
var STORAGE_KEY = 'todos-vuejs-2.0'
var todoStorage = {
  fetch: function () {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]')
  },
  sync: function (todos) {
    axios
      .post('/api/v1/todos', todos)
      .then(response => (todoStorage.save(response.data)))
      .catch(error => console.log(error))
  },
  save: function(todos){
    console.log(todos)
    localStorage.setItem(STORAGE_KEY, JSON.stringify(todos))
  }
}

// visibility filters
var filters = {
  all: function (todos) {
    return todos
  },
  active: function (todos) {
    return todos.filter(function (todo) {
      return !todo.completed
    })
  },
  completed: function (todos) {
    return todos.filter(function (todo) {
      return todo.completed
    })
  }
}

// app Vue instance
var app = new Vue({
  // app initial state
  data () {
    return {
      todos: todoStorage.fetch(),
      newTodo: '',
      editedTodo: null,
      visibility: 'all'
    }
  },

  // watch todos change for localStorage persistence
  watch: {
    todos: {
      handler: function (todos) {
        todoStorage.sync(todos)
      },
      deep: true
    }
  },

  // computed properties
  // http://vuejs.org/guide/computed.html
  computed: {
    filteredTodos: function () {
      return filters[this.visibility](this.todos)
    },
    remaining: function () {
      return filters.active(this.todos).length
    },
    allDone: {
      get: function () {
        return this.remaining === 0
      },
      set: function (value) {
        this.todos.forEach(function (todo) {
          todo.completed = value
        })
      }
    }
  },

  filters: {
    pluralize: function (n) {
      return n === 1 ? 'item' : 'items'
    }
  },

  mounted() {
    axios.get('/api/v1/todos')
      .then(response => (todoStorage.save(response.data)))
      .catch(error => console.log(error))
  },

  // methods that implement data logic.
  // note there's no DOM manipulation here at all.
  methods: {
    addTodo: function () {
      var value = this.newTodo && this.newTodo.trim()
      if (!value) {
        return
      }
      this.todos.push({
        title: value,
        completed: false
      })
      this.newTodo = ''
    },

    removeTodo: function (todo) {
      this.todos.splice(this.todos.indexOf(todo), 1)
    },

    editTodo: function (todo) {
      this.beforeEditCache = todo.title
      this.editedTodo = todo
    },

    doneEdit: function (todo) {
      if (!this.editedTodo) {
        return
      }
      this.editedTodo = null
      todo.title = todo.title.trim()
      if (!todo.title) {
        this.removeTodo(todo)
      }
    },

    cancelEdit: function (todo) {
      this.editedTodo = null
      todo.title = this.beforeEditCache
    },

    removeCompleted: function () {
      this.todos = filters.active(this.todos)
    }
  },

  // a custom directive to wait for the DOM to be updated
  // before focusing on the input field.
  // http://vuejs.org/guide/custom-directive.html
  directives: {
    'todo-focus': function (el, binding) {
      if (binding.value) {
        el.focus()
      }
    }
  }
})

// handle routing
function onHashChange () {
  var visibility = window.location.hash.replace(/#\/?/, '')
  if (filters[visibility]) {
    app.visibility = visibility
  } else {
    window.location.hash = ''
    app.visibility = 'all'
  }
}

window.addEventListener('hashchange', onHashChange)
onHashChange()

// mount
app.$mount('.todoapp')
```

[todo.css](https://github.com/hellokoding/hellokoding-courses/blob/master/springboot-examples/springboot-todomvc-mysql-vuejs/src/main/resources/static/todo.css)

```css
[v-cloak] { display: none; }
```

## 配置并运行

### Application 配置

[Application.java](https://github.com/hellokoding/hellokoding-courses/blob/master/springboot-examples/springboot-todomvc-mysql-vuejs/src/main/java/com/hellokoding/springboot/fullstack/Application.java)

```java
package com.hellokoding.springboot.fullstack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

[application.properties](https://github.com/hellokoding/hellokoding-courses/blob/master/springboot-examples/springboot-todomvc-mysql-vuejs/src/main/resources/application.properties)

```properties
spring.datasource.url=jdbc:mysql://hk-mysql:3306/test?useSSL=false
spring.datasource.username=root
spring.datasource.password=hellokoding
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=create
spring.jpa.database-platform=org.hibernate.dialect.MySQL57Dialect
spring.jpa.generate-ddl=true
spring.jpa.show-sql=true

spring.freemarker.suffix=.html
```

`hk-mysql` 是指 `docker-compose.yml` 文件中定义的Docker Compose服务

`spring.jpa.hibernate.ddl-auto=create` 允许JPA / Hibernate为您自动创建数据库和表数据

```
在生产环境中，您可能希望使用spring.jpa.hibernate.ddl-auto = validate或spring.jpa.hibernate.ddl-auto = none（默认值）禁用DDL Auto功能。查看此示例作为方法之一
Flyway Example of Database Migration/Evolution with Spring Boot, JPA and Hibernate
```

[Flyway Example of Database Migration/Evolution with Spring Boot, JPA and Hibernate](https://hellokoding.com/database-migration-evolution-with-flyway-and-jpa-hibernate/)

### 使用Docker运行

准备用于Java / Spring Boot应用程序的 `Dockerfile` 和用于MySQL Server的 `docker-compose.yml`

[Dockerfile](https://github.com/hellokoding/hellokoding-courses/blob/master/springboot-examples/springboot-todomvc-mysql-vuejs/Dockerfile)

```java
FROM maven:3.5-jdk-8
```

[docker-compose.yml](https://github.com/hellokoding/hellokoding-courses/blob/master/springboot-examples/springboot-todomvc-mysql-vuejs/docker-compose.yml)

```java
version: '3'
services:
  hk-mysql:
    container_name: hk-mysql
    image: mysql/mysql-server:5.7
    environment:
      MYSQL_DATABASE: test
      MYSQL_ROOT_PASSWORD: hellokoding
      MYSQL_ROOT_HOST: '%'
    ports:
    - "3306:3306"
    restart: always

  todomvc-mysql-vuejs:
    build: .
    volumes:
    - .:/app
    - ~/.m2:/root/.m2
    working_dir: /app
    ports:
    - 8080:8080
    command: mvn clean spring-boot:run
    depends_on:
    - hk-mysql
```

在项目根目录下键入以下命令，需要确保本地Docker正在运行

```shell
docker-compose up
```

### 使用 Maven 和 本地MySQL 启动

将 [application.properties](https://hellokoding.com/build-a-full-stack-todomvc-web-app-and-rest-api-service-with-spring-boot-jpa-hibernate-mysql-freemarker-vuejs-and-docker/index.html#applicationconfigurations) 上的 `hk-mysql` 更新为 `localhost` ，并在项目根目录下键入以下命令

```
mvn clean spring-boot:run
```

### 测试

访问  `localhost:8080` 查看app

### Source code

https://github.com/hellokoding/hellokoding-courses/tree/master/springboot-examples/springboot-todomvc-mysql-vuejs

### References

- https://vuejs.org/v2/examples/todomvc.html
- https://vuejs.org/v2/cookbook/using-axios-to-consume-apis.html

