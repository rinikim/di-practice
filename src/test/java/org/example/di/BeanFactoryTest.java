package org.example.di;

import org.example.annotation.Controller;
import org.example.annotation.Service;
import org.example.controller.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class BeanFactoryTest {
    private Reflections reflections;
    private BeanFactory beanFactory;

    /**
     * Reflection 기술을 사용하는데 org.example base package 아래에 있는 class 가 대상이 된다.
     * (@Controller, @Service 가 붙은 Class 타입이 대상이 된다.)
     * -> getTypesAnnotatedWith() 를 실행하면 UserController 와 UserService 의 class 타입을 저장하게 된다.
     * 하지만 class type 객체만 필요한 것이 아니라 instance 객체가 필요하기 때문에 Class type 객체를 key 로 갖고 instance 를 value 로 갖는
     * beans 라는 인스턴스 변수를 갖고 초기화 해준다.
     */
    @BeforeEach
    void setUp() {
        reflections = new Reflections("org.example");
        Set<Class<?>> preInstantiatedClazz = getTypesAnnotatedWith(Controller.class, Service.class);    // UserController, UserService
        beanFactory = new BeanFactory(preInstantiatedClazz);
    }

    /**
     * @ Controller, @ Service 가 붙은 class 를 파라미터로 받아와
     * @param annotations
     * @return
     */
    private Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = new HashSet<>();
        for (Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }

    @Test
    void diTest() {
        UserController userController = beanFactory.getBean(UserController.class);

        assertThat(userController).isNotNull();
        assertThat(userController.getUserService()).isNotNull();
    }
}