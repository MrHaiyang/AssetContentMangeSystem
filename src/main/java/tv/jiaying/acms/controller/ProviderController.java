package tv.jiaying.acms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tv.jiaying.acms.controller.pojos.ResultBean;
import tv.jiaying.acms.entity.ActionRecord;
import tv.jiaying.acms.entity.Provider;
import tv.jiaying.acms.entity.repository.ProviderRepository;
import tv.jiaying.acms.service.ActionService;
import tv.jiaying.acms.service.ServiceErrorCode;
import tv.jiaying.acms.service.SystemService;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/provider")
@CrossOrigin(origins = "*")
@ConfigurationProperties(value = "adapter.processor")
public class ProviderController {

    private static Logger logger = LoggerFactory.getLogger(ProviderController.class);

    private final String LOCATION = "http://222.23.86.243";

    @Resource
    ProviderRepository providerRepository;

    @Resource
    ActionService actionService;

    @Resource
    SystemService systemService;

    private String imgStaticlocation;

    private String imgVisitedlocation;

    @GetMapping("/list")
    @CrossOrigin
    public ResultBean getProviders() {

        List<Provider> providers = providerRepository.findAll();
//        for (Provider provider : providers) {
//            //if (provider.getLogo() != null) provider.setLogo(LOCATION + provider.getLogo());
//        }
        return new ResultBean(ServiceErrorCode.OK, providers);
    }

    /**
     * 更新提供商
     *
     * @param id
     * @param logo
     * @param logoTag
     * @return
     */
    @PostMapping("/update")
    @CrossOrigin
    public ResultBean updateProvider(long id,
                                     @RequestParam(required = false) String logo,
                                     @RequestParam(required = false) Boolean logoTag) {

        //logger.info("logotag:{}",logoTag);
        Provider provider = providerRepository.getOne(id);
        if (provider == null) {
            actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.PROVIDER_UPDATE.getContent(), ServiceErrorCode.UNKNOWN_PROVIDER.getMsg(), ActionRecord.LEVEL.errorRecord, new Date()), systemService.getCurrentUserName());

            return new ResultBean(ServiceErrorCode.UNKNOWN_PROVIDER);
        }
        //logger.info("logo:{}", logo);
        if (logo != null) {
            provider.setLogo(logo);
        } else {
            provider.setLogo(null);
        }

        if (logoTag != null) provider.setLogoTag(logoTag);


        providerRepository.save(provider);

        actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.PROVIDER_UPDATE.getContent() + ">>>" + provider.getName(), ServiceErrorCode.OK.getMsg(), ActionRecord.LEVEL.normalRecord, new Date()), systemService.getCurrentUserName());

        return new ResultBean(ServiceErrorCode.OK, provider);
    }

    public String getImgStaticlocation() {
        return imgStaticlocation;
    }

    public void setImgStaticlocation(String imgStaticlocation) {
        this.imgStaticlocation = imgStaticlocation;
    }

    public String getImgVisitedlocation() {
        return imgVisitedlocation;
    }

    public void setImgVisitedlocation(String imgVisitedlocation) {
        this.imgVisitedlocation = imgVisitedlocation;
    }
}
