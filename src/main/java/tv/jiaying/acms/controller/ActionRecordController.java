package tv.jiaying.acms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tv.jiaying.acms.controller.pojos.ResultBean;
import tv.jiaying.acms.entity.ActionRecord;
import tv.jiaying.acms.entity.SystemUser;
import tv.jiaying.acms.entity.repository.ActionRecordRepository;
import tv.jiaying.acms.entity.repository.SystemUserRepository;
import tv.jiaying.acms.service.ServiceErrorCode;

import javax.annotation.Resource;

@RestController
@RequestMapping("/action")
@CrossOrigin
public class ActionRecordController {

    private static Logger logger = LoggerFactory.getLogger(ActionRecordController.class);

    @Resource
    ActionRecordRepository actionRecordRepository;

    @Resource
    SystemUserRepository systemUserRepository;

    /**
     * 获取操作记录
     *
     * @param username
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list")
    @CrossOrigin
    public ResultBean getActionRecord(String username, int page, int size) {
        SystemUser systemUser = systemUserRepository.getFirstByUsername(username);
        if (systemUser == null) {
            return new ResultBean(ServiceErrorCode.UNKNOWN_SYSTEM_USER);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<ActionRecord> actionRecordPage = actionRecordRepository.findByUsernameOrderByDateDesc(systemUser.getUsername(), pageable);

        return new ResultBean(ServiceErrorCode.OK, actionRecordPage);
    }
}
