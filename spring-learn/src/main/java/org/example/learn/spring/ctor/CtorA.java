package org.example.learn.spring.ctor;

import org.springframework.stereotype.Component;

@Component
public class CtorA {
    public CtorA(CtorB b) {
    }
}
