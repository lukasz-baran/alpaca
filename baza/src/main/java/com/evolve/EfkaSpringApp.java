package com.evolve;

import com.evolve.gui.Registration;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(scanBasePackages = "com.evolve")
@SpringBootApplication
public class EfkaSpringApp {

    public static void main(String[] args) {
        Application.launch(Registration.class, args);
    }

//    @Bean
//    public FxWeaver fxWeaver(ConfigurableApplicationContext applicationContext) {
//        // Would also work with javafx-weaver-core only:
//        // return new FxWeaver(applicationContext::getBean, applicationContext::close);
//        return new SpringFxWeaver(applicationContext);
//    }
//
//    @Bean
//    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//    public <C, V extends Node> FxControllerAndView<C, V> controllerAndView(FxWeaver fxWeaver,
//            InjectionPoint injectionPoint) {
//        return new InjectionPointLazyFxControllerAndViewResolver(fxWeaver)
//                .resolve(injectionPoint);
//    }


}
