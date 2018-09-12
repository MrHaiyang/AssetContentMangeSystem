package tv.jiaying.acms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ErrorInterceptor implements HandlerInterceptor {

    private static Logger logger = LoggerFactory.getLogger(ErrorInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if(response.getStatus()==500){
//            response.sendRedirect(request.getContextPath()+"/error/500");
//            response.addHeader("REDIRECT", "REDIRECT");//告诉ajax这是重定向
//            response.addHeader("CONTEXTPATH", request.getContextPath()+"/error/500");//重定向地址
//            return false;
//        }else if(response.getStatus()==404){
//            response.sendRedirect(request.getContextPath()+"/error/404");
//            response.addHeader("REDIRECT", "REDIRECT");//告诉ajax这是重定向
//            response.addHeader("CONTEXTPATH", request.getContextPath()+"/error/404");//重定向地址
//            return false;
//        }else if(response.getStatus()==403){
//            response.sendRedirect(request.getContextPath()+"/error/403");
//            response.addHeader("REDIRECT", "REDIRECT");//告诉ajax这是重定向
//            response.addHeader("CONTEXTPATH", request.getContextPath()+"/error/403");//重定向地址
//            return false;
//        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
