package com.github.hbq969.gateway.manage.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SimpleRedirectInvalidSessionStrategy;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ApplicationContext context;

    @Value("${spring.security.enabled:true}")
    private boolean enabled;

    @Override
    protected void configure(HttpSecurity http) throws Exception {


        if (enabled) {
            CsrfConfigurer<HttpSecurity> csrf = http
                    // 会话设置
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .invalidSessionUrl("/login")
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false)
                    .expiredUrl("/login")
                    .and()
                    .sessionFixation().migrateSession()
                    .invalidSessionUrl("/login")
                    .invalidSessionStrategy(invalidSessionStrategy())

                    // 跨站请求伪造安全设置
                    .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
            // http请求方法权限控制
            csrf.and().authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/ui-gw/**").authenticated()
                    .antMatchers(HttpMethod.POST, "/ui-gw/**").authenticated()
                    .antMatchers(HttpMethod.OPTIONS, "/ui-gw/**").authenticated()
                    .anyRequest().permitAll()

                    // 登陆、注销页面
                    .and().httpBasic()
                    .and().formLogin().defaultSuccessUrl("/ui-gw/index.html")

                    // 注销后操作
                    .and().logout().logoutUrl("/logout").logoutSuccessUrl("/login")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll();
        } else {
            http.csrf().disable();
        }

    }

    @Bean
    public InvalidSessionStrategy invalidSessionStrategy() {
        SimpleRedirectInvalidSessionStrategy strategy = new SimpleRedirectInvalidSessionStrategy(
                "/login");
        return strategy;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jdbcUserDetailsManager());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager() {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager();
        userDetailsManager.setDataSource(context.getBean(DataSource.class));
        userDetailsManager.setUsersByUsernameQuery(
                "SELECT username, password, enabled FROM tab_gateway_user WHERE username = ?");
        userDetailsManager.setAuthoritiesByUsernameQuery(
                "SELECT username, authority FROM tab_gateway_u_authorities WHERE username = ?");
        return userDetailsManager;
    }
}
