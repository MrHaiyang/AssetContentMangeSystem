package tv.jiaying.acms;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class CustomWebConfigurer extends WebMvcConfigurationSupport {

    //关键，将拦截器作为bean写入配置中
    @Bean
    public ErrorInterceptor errorInterceptor(){
        return new ErrorInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(errorInterceptor());
       // super.addInterceptors(registry);
    }
}
