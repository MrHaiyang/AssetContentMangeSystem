package tv.jiaying.acms.controller;

import org.hibernate.annotations.Synchronize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tv.jiaying.acms.controller.pojos.ResultBean;
import tv.jiaying.acms.entity.ActionRecord;
import tv.jiaying.acms.entity.Item;
import tv.jiaying.acms.entity.Provider;
import tv.jiaying.acms.entity.Relevance;
import tv.jiaying.acms.entity.repository.ItemRepository;
import tv.jiaying.acms.entity.repository.ProviderRepository;
import tv.jiaying.acms.entity.repository.RelevanceRepository;
import tv.jiaying.acms.service.ActionService;
import tv.jiaying.acms.service.ServiceErrorCode;
import tv.jiaying.acms.service.SystemService;
import tv.jiaying.acms.util.ChineseCharUtil;
import tv.jiaying.acms.util.TimeUtil;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 节目相关接口
 */

@RestController
@RequestMapping("/item")
@ConfigurationProperties(value = "adapter.processor")
@CrossOrigin(origins = "*")
public class ItemController {

    private static Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final String LOCATION = "http://222.23.86.243";

    @Resource
    ItemRepository itemRepository;

    @Resource
    ProviderRepository providerRepository;

    @Resource
    RelevanceRepository relevanceRepository;

    @Resource
    ActionService actionService;

    @Resource
    SystemService systemService;

    private String imgStaticlocation;

    private String imgVisitedlocation;

    /**
     * 获取节目列表
     *
     * @param page
     * @param size
     * @return
     */
    @PostMapping("/list")
    @CrossOrigin
    public ResultBean getItemList(@RequestParam(required = false) String keyword,
                                  @RequestParam(value = "orderList", required = false, defaultValue = "null") List<String> orderList, int page, int size) {

        List<Sort.Order> orders = new ArrayList<>();
        Pageable pageable;
        //如果不传排序，按照时间倒叙
        if (orderList != null) {
            for (String s : orderList) {
                String property = null;
                String direction = null;
                try {
                    property = s.split(":")[0];
                    direction = s.split(":")[1];
                } catch (Exception e) {
                    logger.info(e.getMessage(), e);
                    return new ResultBean(ServiceErrorCode.UNKNOWN_ASSET_ORDERLIST);
                }
                Sort.Order order;
                switch (direction) {
                    case "ASC":
                        order = Sort.Order.asc(property);
                        break;
                    case "DESC":
                        order = Sort.Order.desc(property);
                        break;
                    default:
                        return new ResultBean(ServiceErrorCode.UNKNOWN_ASSET_ORDERLIST);
                }
                orders.add(order);
            }

            Sort sort = Sort.by(orders);
            pageable = PageRequest.of(page, size, sort);
        } else {
            pageable = PageRequest.of(page, size);
        }

        Page<Item> items;
        if (keyword == null || "".equals(keyword)) {
            items = itemRepository.findByOrderByModifyDateDesc(pageable);
        } else {
            Provider provider = providerRepository.findFirstByProviderId(keyword);
            if (provider == null) {
                provider = providerRepository.findFirstByName(keyword);
            }
            items = itemRepository.findByTitleContainingOrAssetIdContainingOrProvider(keyword, keyword, provider, pageable);
        }
        return new ResultBean(ServiceErrorCode.OK, items);
    }

    /**
     * 获取节目详情
     *
     * @param id
     * @return
     */
    @GetMapping("/detail")
    @CrossOrigin
    public ResultBean getItemDetailInfo(long id) {

        Item item = itemRepository.getOne(id);
        if (item == null) {
            return new ResultBean(ServiceErrorCode.UNKNOWN_ASSET);
        }

//        if (item.getPosterSmall() != null) item.setPosterSmall(LOCATION + item.getPosterSmall());
//        if (item.getPosterMid() != null) item.setPosterMid(LOCATION + item.getPosterMid());
//        if (item.getPosterLarge() != null) item.setPosterLarge(LOCATION + item.getPosterLarge());

        return new ResultBean(ServiceErrorCode.OK, item);
    }

    /**
     * 更改节目上下架状态
     *
     * @param idList
     * @param online
     * @return
     */
    @PostMapping("/online")
    @CrossOrigin
    public ResultBean updateItemOnline(@RequestParam("idList") List<Long> idList, Boolean online) {

        for (long id : idList) {
            Item item = itemRepository.getOne(id);
            if (item == null) {
                continue;
            }
            item.setOnline(online);
            itemRepository.save(item);
            if(!online){
                List<Relevance> relevances = relevanceRepository.findByChildItemId(id);
                for (Relevance relevance: relevances) {
                    relevance.setOnline(online);
                    relevanceRepository.save(relevance);
                }
            }

            actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.ITEM_UPDATE_ONLINE.getContent() + ">>>" + item.getTitle() + "->online:" + online, "ok", ActionRecord.LEVEL.normalRecord, new Date()), systemService.getCurrentUserName());
        }

        return new ResultBean(ServiceErrorCode.OK);
    }

    /**
     * 更新item
     *
     * @param id
     * @param title
     * @param showType
     * @param year
     * @param director
     * @param actors
     * @param suggestedPrice
     * @param language
     * @param countryOfOrigin
     * @param licensingWindowStart
     * @param licensingWindowEnd
     * @param summaryShort
     * @param posterSmall
     * @param posterMid
     * @param posterLarge
     * @return
     */
    @PostMapping("/update")
    @CrossOrigin

    public ResultBean updateItem(long id,
                                 @RequestParam(required = false) String title,
                                 @RequestParam(required = false) Item.ShowType showType,
                                 @RequestParam(required = false) String year,
                                 @RequestParam(required = false) String director,
                                 @RequestParam(required = false) String actors,
                                 @RequestParam(required = false, defaultValue = "0") double suggestedPrice,
                                 @RequestParam(required = false) String language,
                                 @RequestParam(required = false) String countryOfOrigin,
                                 @RequestParam(required = false) String licensingWindowStart,
                                 @RequestParam(required = false) String licensingWindowEnd,
                                 @RequestParam(required = false) String summaryShort,
                                 @RequestParam(required = false) String posterSmall,
                                 @RequestParam(required = false) String posterMid,
                                 @RequestParam(required = false) String posterLarge) {

        Item item = itemRepository.getOne(id);
        if (item == null) {
            actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.ITEM_UPDATE.getContent(), ServiceErrorCode.UNKNOWN_ASSET.getMsg(), ActionRecord.LEVEL.errorRecord, new Date()), systemService.getCurrentUserName());
            return new ResultBean(ServiceErrorCode.UNKNOWN_ASSET);
        }
        if (title != null) item.setTitle(title);
        if (showType != null) item.setShowType(showType);
        if (year != null) item.setYear(year);
        if (director != null) item.setDirector(director);
        if (actors != null) item.setActors(actors);
        if (suggestedPrice != 0) item.setSuggestedPrice(suggestedPrice);
        if (language != null) item.setLanguage(language);
        if (countryOfOrigin != null) item.setCountryOfOrigin(countryOfOrigin);
        if (summaryShort != null) item.setSummaryShort(summaryShort);
        String titleSpell = ChineseCharUtil.getFirstSpell(item.getTitle());
        item.setTitleSpell(titleSpell);
        String directorSpell = ChineseCharUtil.getFirstSpell(item.getDirector());
        item.setDirectorSpell(directorSpell);
        String actorSpell = ChineseCharUtil.getFirstSpell(item.getActors());
        item.setActorsSpell(actorSpell);
        if (licensingWindowStart != null & licensingWindowEnd != null) {
            item.setLicensingWindowStart(TimeUtil.getDateByyyyyMMdd(licensingWindowStart));
            item.setLicensingWindowEnd(TimeUtil.getDateByyyyyMMdd(licensingWindowEnd));
        }

        if (posterSmall != null) {
            item.setPosterSmall(posterSmall);
        } else {
            item.setPosterSmall(null);
        }
        if (posterMid != null) {
            item.setPosterMid(posterMid);
        } else {
            item.setPosterMid(null);
        }
        if (posterLarge != null) {
            item.setPosterLarge(posterLarge);
        } else {
            item.setPosterLarge(null);
        }

        itemRepository.save(item);
        actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.ITEM_UPDATE.getContent() + ">>>" + item.getTitle(), "ok", ActionRecord.LEVEL.normalRecord, new Date()), systemService.getCurrentUserName());

        return new ResultBean(ServiceErrorCode.OK);
    }

    /**
     * 删除节目信息，但是不会删除已经注入的内容
     *
     * @param idList
     * @return
     */
    @DeleteMapping("/delete")
    @CrossOrigin
    public ResultBean deleteItem(@RequestParam("idList") List<Long> idList) {
        //logger.info("idList:{}",idList);
        for (long id : idList) {
            relevanceRepository.deleteByChildItemId(id);
            if (!itemRepository.existsById(id)) {
                continue;
            }

            itemRepository.deleteById(id);
        }
        return new ResultBean(ServiceErrorCode.OK);

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
