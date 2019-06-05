package org.hackerandpainter.springbootvuejstodo.todo;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-06-04 22:02
 **/
public interface TodoRepository extends JpaRepository<Todo, Long> {
}

