package org.hackerandpainter.springbootvuejstodo.todo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-06-04 21:38
 **/
@Entity

@Data
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 主键由数据库自动生成（主要是自动增长型）
    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    private String title;

    private boolean completed;

    private Date updateDate = new Date();
}
