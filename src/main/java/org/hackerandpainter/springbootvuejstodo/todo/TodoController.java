package org.hackerandpainter.springbootvuejstodo.todo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description
 * @Author Gao Hang Hang
 * @Date 2019-06-04 22:04
 **/
@Controller
public class TodoController {
    @GetMapping("/")
    public String list() {
        return "todo";
    }
}
