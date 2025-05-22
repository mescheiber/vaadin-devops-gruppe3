package net.mci.seii.group3.config;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import net.mci.seii.group3.views.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/","/images/**" , "/error", "/favicon.ico", "/VAADIN/**", "/frontend/**").permitAll()
        );

        super.configure(http);

        setLoginView(http, LoginView.class, "/login");
    }



    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        AccessDeniedHandlerImpl handler = new AccessDeniedHandlerImpl();
        handler.setErrorPage("/access-denied");
        return handler;
    }
}

