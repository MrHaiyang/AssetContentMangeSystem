package tv.jiaying.acms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tv.jiaying.acms.entity.ActionRecord;
import tv.jiaying.acms.entity.repository.ActionRecordRepository;

import javax.annotation.Resource;

@Service
@ConfigurationProperties("cms.websocket")
public class ActionService {

    private static Logger logger = LoggerFactory.getLogger(ActionService.class);

    @Resource
    private SimpMessagingTemplate messaging;

    private String actionDestination;

    @Resource
    ActionRecordRepository actionRecordRepository;

    public void sendActionRecord2User(ActionRecord action, String userName) {

        //数据推送到前端
        messaging.convertAndSendToUser(userName, actionDestination, action);
        //保存到数据库
        addActionRecord(action);
    }

    public void addActionRecord(ActionRecord action) {
        actionRecordRepository.save(action);
    }

    public String getActionDestination() {
        return actionDestination;
    }

    public void setActionDestination(String actionDestination) {
        this.actionDestination = actionDestination;
    }
}
