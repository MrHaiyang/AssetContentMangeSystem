package tv.jiaying.acms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tv.jiaying.acms.controller.pojos.ResultBean;
import tv.jiaying.acms.entity.ActionRecord;
import tv.jiaying.acms.entity.Colum;
import tv.jiaying.acms.entity.Item;
import tv.jiaying.acms.entity.Provider;
import tv.jiaying.acms.entity.repository.ColumRepository;
import tv.jiaying.acms.entity.repository.ItemRepository;
import tv.jiaying.acms.entity.repository.ProviderRepository;
import tv.jiaying.acms.service.ActionService;
import tv.jiaying.acms.service.ServiceErrorCode;
import tv.jiaying.acms.service.SystemService;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Principal;
import java.util.Date;

@RestController
@RequestMapping("/upload")
@ConfigurationProperties(value = "adapter.processor")
@CrossOrigin(origins = "*")
public class UploadController {

    private static Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Resource
    ActionService actionService;

    @Resource
    SystemService systemService;

    @Resource
    ItemRepository itemRepository;

    @Resource
    ColumRepository columRepository;

    @Resource
    ProviderRepository providerRepository;

    private String adiLocation;

    private String imgStaticlocation;

    private String imgVisitedlocation;

    private String imgMountAppOne;

    private String imgMountApptwo;


    @PostMapping("/adi")
    @CrossOrigin
    public ResultBean uploadADI(MultipartFile file) {
        File adi = new File(adiLocation, file.getOriginalFilename());
        try {
            FileOutputStream os = new FileOutputStream(adi);
            os.write(file.getBytes());
            os.close();
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
        }

        actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.UPLOAD_ADI.getContent() + ">>>" + file.getOriginalFilename(), ServiceErrorCode.OK.getMsg(), ActionRecord.LEVEL.normalRecord, new Date()), systemService.getCurrentUserName());

        return new ResultBean(ServiceErrorCode.OK);
    }

    @PostMapping("/img")
    @CrossOrigin
    public ResultBean uploadPoster(String type,
                                   @RequestParam(required = false) MultipartFile posterSmall,
                                   @RequestParam(required = false) MultipartFile posterMid,
                                   @RequestParam(required = false) MultipartFile posterLarge,
                                   @RequestParam(required = false) MultipartFile providerLogo) {
        if ("colum".equals(type)) {
            ImgBean imgBean = new ImgBean();
            if (null != posterSmall) {

                try {
                    String imgType = posterSmall.getOriginalFilename().substring(posterSmall.getOriginalFilename().lastIndexOf("."));
                    String filename = "column_small" + "_" + new Date().getTime() + imgType;

                    File posterSmallFile = new File(imgStaticlocation, filename);
                    FileOutputStream os = new FileOutputStream(posterSmallFile);
                    os.write(posterSmall.getBytes());
                    os.close();
                    //copy to mountPath
                    this.copyFile(posterSmall,imgMountAppOne,filename);
                    this.copyFile(posterSmall,imgMountApptwo,filename);
                    //imgBean.setPosterSmall(imgVisitedlocation + posterSmallFile.getName());
                    imgBean.setPosterSmall(imgVisitedlocation + posterSmallFile.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != posterMid) {
                try {
                    String imgType = posterMid.getOriginalFilename().substring(posterMid.getOriginalFilename().lastIndexOf("."));
                    String filename = "column_mid" + "_" + new Date().getTime() + imgType;

                    File posterMidFile = new File(imgStaticlocation, filename);
                    FileOutputStream os = new FileOutputStream(posterMidFile);
                    os.write(posterMid.getBytes());
                    os.close();
                    this.copyFile(posterMid,imgMountAppOne,filename);
                    this.copyFile(posterMid,imgMountApptwo,filename);
                    imgBean.setPosterMid(imgVisitedlocation + posterMidFile.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != posterLarge) {
                try {
                    String imgType = posterLarge.getOriginalFilename().substring(posterLarge.getOriginalFilename().lastIndexOf("."));
                    String filename = "column_large" + "_" + new Date().getTime() + imgType;

                    File posterLargeFile = new File(imgStaticlocation, filename);
                    FileOutputStream os = new FileOutputStream(posterLargeFile);
                    os.write(posterLarge.getBytes());
                    os.close();
                    this.copyFile(posterLarge,imgMountAppOne,filename);
                    this.copyFile(posterLarge,imgMountApptwo,filename);
                    imgBean.setPosterLarge(imgVisitedlocation + posterLargeFile.getName());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //columRepository.save(colum);
            return new ResultBean(ServiceErrorCode.OK, imgBean);

        } else if ("item".equals(type)) {
            ImgBean imgBean = new ImgBean();

            if (null != posterSmall) {

                try {
                    String imgType = posterSmall.getOriginalFilename().substring(posterSmall.getOriginalFilename().lastIndexOf("."));
                    String filename = "item_small" + "_" + new Date().getTime() + imgType;

                    File posterSmallFile = new File(imgStaticlocation, filename);
                    FileOutputStream os = new FileOutputStream(posterSmallFile);
                    os.write(posterSmall.getBytes());
                    os.close();
                    this.copyFile(posterSmall,imgMountAppOne,filename);
                    this.copyFile(posterSmall,imgMountApptwo,filename);
                    imgBean.setPosterSmall(imgVisitedlocation + posterSmallFile.getName());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != posterMid) {
                try {
                    String imgType = posterMid.getOriginalFilename().substring(posterMid.getOriginalFilename().lastIndexOf("."));
                    String filename = "item_mid" + "_" + new Date().getTime() + imgType;

                    File posterMidFile = new File(imgStaticlocation, filename);
                    FileOutputStream os = new FileOutputStream(posterMidFile);
                    os.write(posterMid.getBytes());
                    os.close();
                    this.copyFile(posterMid,imgMountAppOne,filename);
                    this.copyFile(posterMid,imgMountApptwo,filename);
                    imgBean.setPosterMid(imgVisitedlocation + posterMidFile.getName());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != posterLarge) {
                try {
                    String imgType = posterLarge.getOriginalFilename().substring(posterLarge.getOriginalFilename().lastIndexOf("."));
                    String filename = "item_large" + "_" + new Date().getTime() + imgType;

                    File posterLargeFile = new File(imgStaticlocation, filename);
                    FileOutputStream os = new FileOutputStream(posterLargeFile);
                    os.write(posterLarge.getBytes());
                    os.close();
                    this.copyFile(posterLarge,imgMountAppOne,filename);
                    this.copyFile(posterLarge,imgMountApptwo,filename);
                    imgBean.setPosterLarge(imgVisitedlocation + posterLargeFile.getName());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //itemRepository.save(item);

            return new ResultBean(ServiceErrorCode.OK, imgBean);
        } else if ("provider".equals(type)) {

            ImgBean imgBean = new ImgBean();

            if (providerLogo != null) {
                String imgType = providerLogo.getOriginalFilename().substring(providerLogo.getOriginalFilename().lastIndexOf("."));
                String filename = "provider_logo" + "_" + new Date().getTime() + imgType;
                File logoFile = new File(imgStaticlocation, filename);
                try {

                    FileOutputStream os = new FileOutputStream(logoFile);
                    os.write(providerLogo.getBytes());
                    os.close();
                    this.copyFile(providerLogo,imgMountAppOne,filename);
                    this.copyFile(providerLogo,imgMountApptwo,filename);
                    imgBean.setLogo(imgVisitedlocation + logoFile.getName());
                   // providerRepository.save(provider);
                } catch (IOException e) {
                    logger.info(e.getMessage(), e);
                }
            }
            return new ResultBean(ServiceErrorCode.OK,imgBean);
        }

        return new ResultBean(ServiceErrorCode.OK);
    }

    private void copyFile(MultipartFile file,String targetPath,String fileName){

        try {
            File mountFile = new File(targetPath,fileName);
            FileOutputStream os = new FileOutputStream(mountFile);
            os.write(file.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ImgBean{
        private String posterSmall;
        private String posterMid;
        private String posterLarge;
        private String logo;

        public String getPosterSmall() {
            return posterSmall;
        }

        public void setPosterSmall(String posterSmall) {
            this.posterSmall = posterSmall;
        }

        public String getPosterMid() {
            return posterMid;
        }

        public void setPosterMid(String posterMid) {
            this.posterMid = posterMid;
        }

        public String getPosterLarge() {
            return posterLarge;
        }

        public void setPosterLarge(String posterLarge) {
            this.posterLarge = posterLarge;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }
    }

    public String getAdiLocation() {
        return adiLocation;
    }

    public void setAdiLocation(String adiLocation) {
        this.adiLocation = adiLocation;
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

    public String getImgMountAppOne() {
        return imgMountAppOne;
    }

    public void setImgMountAppOne(String imgMountAppOne) {
        this.imgMountAppOne = imgMountAppOne;
    }

    public String getImgMountApptwo() {
        return imgMountApptwo;
    }

    public void setImgMountApptwo(String imgMountApptwo) {
        this.imgMountApptwo = imgMountApptwo;
    }
}
