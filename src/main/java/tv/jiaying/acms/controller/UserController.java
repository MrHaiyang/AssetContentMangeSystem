package tv.jiaying.acms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tv.jiaying.acms.controller.pojos.ResultBean;
import tv.jiaying.acms.entity.repository.UserRepository;
import tv.jiaying.acms.service.ServiceErrorCode;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Resource
    UserRepository userRepository;

    /**
     * 获取应用用户列表
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    @CrossOrigin
    public ResultBean getUserInfo(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return new ResultBean(ServiceErrorCode.OK, userRepository.findAll(pageable));

    }

}
