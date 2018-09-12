package tv.jiaying.acms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Service
public class RestAuthenticationAccessDeniedHandler implements AccessDeniedHandler {

    private static Logger logger = LoggerFactory.getLogger(RestAuthenticationAccessDeniedHandler.class);
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {

        response.getWriter().print("111You need to login first in order to perform this action.");
        //        logger.info("response.status:{}",response.getStatus());
//        if(response.getStatus()==403){
//            PrintWriter out =response.getWriter();
//            out.write("{\"status\":\"403\",\"msg\":\"access denied\"}");
//            //out.println(403);
//            out.flush();
//            out.close();
//        }
//
//        httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
//
//        httpServletResponse.setCharacterEncoding("UTF-8");

    }
}
