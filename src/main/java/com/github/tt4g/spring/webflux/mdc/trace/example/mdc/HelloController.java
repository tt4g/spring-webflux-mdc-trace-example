package com.github.tt4g.spring.webflux.mdc.trace.example.mdc;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HelloController {

    @RequestMapping(path = "/hello", method = RequestMethod.GET)
    public Mono<String> hello() {
        return Mono.just("Hello");
    }

}
