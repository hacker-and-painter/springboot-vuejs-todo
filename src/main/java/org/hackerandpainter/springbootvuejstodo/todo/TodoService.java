package org.hackerandpainter.springbootvuejstodo.todo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-06-04 22:02
 **/
@Service
public class TodoService {

    private final TodoRepository stockRepository;

    public TodoService(TodoRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Todo> findAll() {
        return stockRepository.findAll();
    }

    public List<Todo> saveAll(List<Todo> todos) {
        return stockRepository.saveAll(todos);
    }
}
