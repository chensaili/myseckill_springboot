package com.csl.common;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 切面处理类
 */
@Aspect
@Component
public class AspectConfig {
    private static final Logger logger= LoggerFactory.getLogger(AspectConfig.class);

    //@Pointcut("execution(public * com.csl.controller.*(..))")
    // 会报错Error creating bean with name 'tomcatServletWebServerFactory' defined.......
    @Pointcut("execution(public * com.csl.controller.*.*(..))")
    public void pointCut(){

    }

    @Before("pointCut()")
    public void doBefore(JoinPoint point){
        logger.info("请求url为：{}",((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getRequestURL());
    }
}
