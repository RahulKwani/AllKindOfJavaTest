package com.test.rahul.spring.bigtable;

import java.util.Random;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CountService {

  @GetMapping
  public String rootAccess() {
    return "This is root of this app";
  }

  @GetMapping("Hi/{n}")
  public MyBean sayHi(@PathVariable(name = "n", required = true) String name) {
    return new MyBean(new Random().nextInt(), name);
  }

  @GetMapping("Hello")
  public MyBean sayHello(@RequestParam("name") String name) {
    return new MyBean(new Random().nextInt(), name);
  }

  static class MyBean {
    private Integer id;
    private String name;

    MyBean() {}

    MyBean(Integer id, String name) {
      this.id = id;
      this.name = name;
    }

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
