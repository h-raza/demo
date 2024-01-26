package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value="/demo", headers="Accept=*/*",  produces="application/json")
public class DemoController {
  @Autowired
  TestService testService;

  @GetMapping(produces="application/json")
  public Mono<Health> test(){
    return testService.health();
  }

}
