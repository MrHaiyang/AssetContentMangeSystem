package tv.jiaying.acms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tv.jiaying.acms.controller.pojos.ResultBean;
import tv.jiaying.acms.entity.ActionRecord;
import tv.jiaying.acms.entity.SystemUser;
import tv.jiaying.acms.entity.repository.SystemUserRepository;
import tv.jiaying.acms.service.ActionService;
import tv.jiaying.acms.service.ServiceErrorCode;
import tv.jiaying.acms.service.SystemService;
import tv.jiaying.acms.service.UserAuthService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Date;
import java.util.Iterator;

@RestController
@RequestMapping("/system")
@EnableConfigurationProperties
@CrossOrigin(origins = "*")
public class SystemController {

    private static Logger logger = LoggerFactory.getLogger(SystemController.class);

    @Resource
    UserAuthService userAuthService;

    @Resource
    SystemUserRepository systemUserRepository;

    @Resource
    ActionService actionService;

    @Resource
    SystemService systemService;

    /**
     * 获取系统用户信息
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/user/list")
    @CrossOrigin
    public ResultBean getSystemUserInfo(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return new ResultBean(ServiceErrorCode.OK, systemUserRepository.findAll(pageable));

    }

    /**
     * 注册系统用户
     *
     * @param username
     * @param password
     * @param role
     * @return
     */
    @PostMapping("/user/register")
    @CrossOrigin
    public ResultBean registerSystemUser(String username, String password, SystemUser.ROLE role) {
        SystemUser systemUser = systemUserRepository.getFirstByUsername(username);
        if (systemUser != null) {
            return new ResultBean(ServiceErrorCode.UNKNOWN_SYSTEM_USER_REPEATNAME);
        }
        systemUser = new SystemUser();
        systemUser.setUsername(username);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        systemUser.setPassword(passwordEncoder.encode(password));
        systemUser.setEnable(true);
        systemUser.setRole(role);

        systemUserRepository.save(systemUser);

        return new ResultBean(ServiceErrorCode.OK);

    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @DeleteMapping("/user")
    @CrossOrigin
    public ResultBean deleteSystemUser(long id) {

        systemUserRepository.deleteById(id);

        actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.SYSTEM_USER_DELETE.getContent(), ServiceErrorCode.OK.getMsg(), ActionRecord.LEVEL.normalRecord, new Date()), systemService.getCurrentUserName());

        return new ResultBean(ServiceErrorCode.OK);

    }

    /**
     * 更改系统用户状态
     *
     * @param id
     * @param enable
     * @param authority
     * @return
     */
    @PostMapping("/user")
    @CrossOrigin
    public ResultBean updateSystemUser(long id,
                                       @RequestParam(required = false) Boolean enable,
                                       @RequestParam(required = false) String authority) {
        SystemUser user = systemUserRepository.getOne(id);
        if (user == null) {

            actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.SYSTEM_USER_UPDATE.getContent(), ServiceErrorCode.UNKNOWN_SYSTEM_USER.getMsg(), ActionRecord.LEVEL.errorRecord, new Date()), systemService.getCurrentUserName());

            return new ResultBean(ServiceErrorCode.UNKNOWN_SYSTEM_USER);
        }
        if (enable != null) {
            user.setEnable(enable);
        }
        if (authority != null) {
            user.setAuthority(authority);
        }

        systemUserRepository.save(user);

        actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.SYSTEM_USER_UPDATE.getContent(), ServiceErrorCode.OK.getMsg(), ActionRecord.LEVEL.normalRecord, new Date()), systemService.getCurrentUserName());

        return new ResultBean(ServiceErrorCode.OK);
    }

    /**
     * 用户登陆
     *
     * @param request
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/login")
    @CrossOrigin
    public ResultBean login(HttpServletRequest request, String username, String password) {

        logger.info("username:{}",username);
        UserDetails user = userAuthService.loadUserByUsername(username);

        if (user == null) {
            return new ResultBean(ServiceErrorCode.UNKNOWN_SYSTEM_USER);
        } else if (!new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            return new ResultBean(ServiceErrorCode.UNKNOWN_SYSTEM_USER_WRONGPW);
        }

        HttpSession session = request.getSession();

        session.setAttribute("principal", user.getUsername());

        SecurityContext context = SecurityContextHolder.getContext();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());

        context.setAuthentication(authentication);


        //logger.info("session:{}", (String) session.getAttribute("principal"));

        return new ResultBean(ServiceErrorCode.OK,user);

    }

    /**
     * 获取当前登陆用户名
     *
     * @return
     */
    @GetMapping("/currentUser")
    @CrossOrigin
    public ResultBean getCurrentUserName() {

        SecurityContext context = SecurityContextHolder.getContext();

        Object principal = context.getAuthentication().getPrincipal();

        //logger.info("currentUser:{}",principal.toString());

        SystemUser systemUser = systemUserRepository.getFirstByUsername(principal.toString());
        systemUser.setPassword(null);
        return new ResultBean(ServiceErrorCode.OK, systemUser);
    }

}
