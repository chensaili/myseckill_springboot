package com.csl.common.shiro;

import org.apache.shiro.session.mgt.SessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

@Configuration
public class ShiroConfig {
    private static final int DEFAULT_SESSION_ENABLE_TIME = 2 * 3600 * 1000;

    @Bean("sessionManager")
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager=new DefaultWebSessionManager();
        sessionManager.setSessionValidationInterval(DEFAULT_SESSION_ENABLE_TIME);
        return sessionManager;
    }
}
