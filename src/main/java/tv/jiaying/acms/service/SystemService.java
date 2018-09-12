package tv.jiaying.acms.service;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tv.jiaying.acms.controller.pojos.ResultBean;

@Service
public class SystemService {

    public String getCurrentUserName() {

        SecurityContext context = SecurityContextHolder.getContext();

        Object principal = context.getAuthentication().getPrincipal();

        return principal.toString();
    }
}
