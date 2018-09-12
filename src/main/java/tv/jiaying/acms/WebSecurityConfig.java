package tv.jiaying.acms;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
@ConfigurationProperties("cms.redirect")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private String logout;

    @Bean
    public AccessDeniedHandler getAccessDeniedHandler() {
        return new RestAuthenticationAccessDeniedHandler();
    }

    public AuthenticationEntryPoint getAuthenticationEntryPoint(){
        return new CustomHttp403ForbiddenEntryPoint();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .exceptionHandling().accessDeniedHandler(getAccessDeniedHandler()).authenticationEntryPoint(getAuthenticationEntryPoint())
                .and()
                .authorizeRequests()

                .antMatchers("/login", "/system/login","/error/**").permitAll()
                .antMatchers(HttpMethod.GET, "/system/user/list").hasAnyAuthority("ADMIN", "USER", "SUPER")
                .antMatchers(HttpMethod.POST, "/system/user/register").hasAnyAuthority("SUPER")
                .antMatchers(HttpMethod.DELETE, "/system/user").hasAnyAuthority("SUPER")

                .antMatchers(HttpMethod.GET, "/item/detail").hasAnyAuthority("ADMIN", "USER", "SUPER")
                .antMatchers(HttpMethod.POST, "/item/list").hasAnyAuthority("ADMIN", "USER", "SUPER")
                .antMatchers(HttpMethod.POST, "/item/online", "/item/update").hasAnyAuthority("ADMIN", "SUPER")
                .antMatchers(HttpMethod.DELETE, "/item/delete", "/item/**").hasAnyAuthority("ADMIN", "SUPER")

                .antMatchers(HttpMethod.GET, "/column/detail", "/column/child").hasAnyAuthority("ADMIN", "USER", "SUPER")
                .antMatchers(HttpMethod.POST, "/column/add", "/column/update", "/column/relevance", "/column/relevance/update", "/column/sort").hasAnyAuthority("ADMIN", "SUPER")
                .antMatchers(HttpMethod.DELETE, "/column/delete", "/column/relevance").hasAnyAuthority("ADMIN", "SUPER")

                .antMatchers(HttpMethod.GET, "/user", "/user/**").hasAnyAuthority("ADMIN", "USER", "SUPER")

                .antMatchers(HttpMethod.GET, "/action", "/action/**").hasAnyAuthority("ADMIN", "USER", "SUPER")

                .antMatchers(HttpMethod.POST, "/upload", "/upload/**").hasAnyAuthority("ADMIN", "SUPER")

                .antMatchers(HttpMethod.GET, "/provider/list").hasAnyAuthority("ADMIN", "USER", "SUPER")
                .antMatchers(HttpMethod.POST, "/provider/update").hasAnyAuthority("ADMIN", "SUPER")

//                .anyRequest().permitAll()
                //.and()
                //.rememberMe()
                .and()
                //.requiresChannel()
                //.antMatchers("/system/user/register").requiresSecure()
                //.and()
                .logout()
                .logoutUrl("/logout").logoutSuccessUrl(logout + "?logout").deleteCookies("JSESSIONID").permitAll()
                .and()
                .csrf()
                //.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .disable()
                .headers().disable()
                ;


    }


    public String getLogout() {
        return logout;
    }

    public void setLogout(String logout) {
        this.logout = logout;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    //    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//
//        super.configure(auth);
//    }


}
