package org.example.di;

import org.example.annotation.Inject;
import org.example.controller.UserController;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class BeanFactory {

    private final Set<Class<?>> preInstantiatedClazz;
    private Map<Class<?>, Object> beans = new HashMap<>();

    public BeanFactory(Set<Class<?>> preInstantiatedClazz) {
        this.preInstantiatedClazz = preInstantiatedClazz;
        initialize();
    }

    /**
     * BeanFactory 를 초기화하는 로직이다.
     * class type 객체를 가지고 instance 객체를 생성해서 beans 에 넣어준다.
     */
    private void initialize() {
        // class 타입 객체로 instance 를 만들어 준 다음에 beans 넣어 초기화 해준다.
        for (Class<?> clazz : preInstantiatedClazz) {
            Object instance = createInstance(clazz);
            beans.put(clazz, instance);
        }
    }

    /**
     * class type 객체의 인스턴스를 생성한다.
     * 생성자, 파라미터, 인스턴스 생성을 순서대로 진행한다.
     * 생성자는 class type 객체로 생성자를 조회해온다. (인스턴스를 생성하기 위해서는 생성자가 필요하기 때문이다.)
     * @param clazz
     * @return instance 객체
     */
    private Object createInstance(Class<?> clazz) {
        // 생성자 : class type 의 생성자를 조회해온다.
        Constructor<?> constructor = findConstructor(clazz);

        // 파라미터
        List<Object> parameters = new ArrayList<>();
        for (Class<?> typeClass : constructor.getParameterTypes()) {    // typeClass : UserService (UserController 에 대한 생성자 파라미터)
            parameters.add(getParameterByClass(typeClass));
        }

        // 인스턴스 생성
        try {
            return constructor.newInstance(parameters.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * class type 객체에 모든 생성자를 가져온다. 단, Inject annotation 이 붙은 class 만 가져온다.
     * @param clazz
     * @return Inject annotation 이 붙여진 class 의 생성자
     */
    private Constructor<?> findConstructor(Class<?> clazz) {
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);

        if (Objects.nonNull(constructor)) {
            return constructor;
        }

        return clazz.getConstructors()[0];  // class type 객체에 첫번째 constructor 를 반환
    }

    /**
     * class type 객체를 key 로 가지는 Instance 가 있는지 확인한 후 있으면 인스턴스를 return 하고 없으면 class 의 첫번째 생성자를 가져온다.
     * @param typeClass
     * @return instance 객체
     */
    private Object getParameterByClass(Class<?> typeClass) {
        Object instanceBean = getBean(typeClass);

        if (Objects.nonNull(instanceBean)) {
            return instanceBean;
        }

        // class 에 대한 instance 생성 전이라면 instance 를 다시 생성 (재귀함수)
        // 예를 들어 UserController 가 들어왔는데 UserService 의 인스턴스가 생성되기 이전이라면 UserService 의 인스턴스를 다시 생성해주러 이동
        return createInstance(typeClass);
    }

    /**
     * class type 객체를 key 로 가지는 instance 가 있는지 확인한다.
     * @param requiredType
     * @return instance 객체
     * @param <T>
     */
    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }
}
