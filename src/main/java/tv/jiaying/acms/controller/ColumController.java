package tv.jiaying.acms.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tv.jiaying.acms.controller.pojos.RelativeType;
import tv.jiaying.acms.controller.pojos.ResultBean;
import tv.jiaying.acms.entity.ActionRecord;
import tv.jiaying.acms.entity.Colum;
import tv.jiaying.acms.entity.Item;
import tv.jiaying.acms.entity.Relevance;
import tv.jiaying.acms.entity.repository.ColumRepository;
import tv.jiaying.acms.entity.repository.ItemRepository;
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
import java.util.*;
import java.util.function.Function;

/**
 * 栏目接口
 */
@RestController
@RequestMapping("/column")
@CrossOrigin(origins = "*")
@ConfigurationProperties(value = "adapter.processor")
public class ColumController {

    private static Logger logger = LoggerFactory.getLogger(ColumController.class);

    private final String LOCATION = "http://222.23.86.243";

    @Resource
    RelevanceRepository relevanceRepository;

    @Resource
    ColumRepository columRepository;

    @Resource
    ItemRepository itemRepository;

    @Resource
    ActionService actionService;

    @Resource
    SystemService systemService;

    private String imgStaticlocation;

    private String imgVisitedlocation;

    /**
     * 获取节目详情
     *
     * @param id
     * @return
     */
    @GetMapping("/detail")
    @CrossOrigin
    public ResultBean getOneByColumId(long id) {
        Colum colum = columRepository.getOne(id);
        if (colum == null) return new ResultBean(ServiceErrorCode.UNKNOWN_COLUM);
//        if (colum.getPosterSmall() != null) colum.setPosterSmall(LOCATION + colum.getPosterSmall());
//        if (colum.getPosterMid() != null) colum.setPosterMid(LOCATION + colum.getPosterMid());
//        if (colum.getPosterLarge() != null) colum.setPosterLarge(LOCATION + colum.getPosterLarge());
        return new ResultBean(ServiceErrorCode.OK, colum);
    }

    /**
     * 获取根栏目或者栏目子列表
     *
     * @param id
     * @param contentType
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/child")
    @CrossOrigin
    public ResultBean getColumDetail(Boolean getRoot,
                                     @RequestParam(required = false, defaultValue = "0") long id,
                                     @RequestParam(required = false) String contentType,
                                     @RequestParam(required = false, defaultValue = "0") int page,
                                     @RequestParam(required = false, defaultValue = "0") int size) {

        //获取根栏目信息
        if (getRoot) {
            Colum colum = columRepository.findFirstByIsRootIsTrue();
            return new ResultBean(ServiceErrorCode.OK, colum);
        }
        //获取子项目
        if (!columRepository.existsById(id)) {
            return new ResultBean(ServiceErrorCode.UNKNOWN_COLUM);
        }
        Colum colum = columRepository.getOne(id);
        if (colum == null) {
            actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_GET.getContent(), ServiceErrorCode.UNKNOWN_COLUM.getMsg(), ActionRecord.LEVEL.errorRecord, new Date()), systemService.getCurrentUserName());
            return new ResultBean(ServiceErrorCode.UNKNOWN_COLUM);
        }
        switch (contentType) {
            case "colum":
                List<Relevance> relevances = relevanceRepository.findByParentColumIdOrderByPositionDesc(id);
                List<ColumBean> columBeans = new ArrayList<>();
                for (Relevance relevance : relevances) {
                    if (relevance.getChildColumId() != 0) {
                        ColumBean columBean = new ColumBean();
                        Colum childColum = columRepository.getOne(relevance.getChildColumId());

//                        if (colum.getPosterSmall() != null)
//                            childColum.setPosterSmall(LOCATION + colum.getPosterSmall());
//                        if (colum.getPosterMid() != null) childColum.setPosterMid(LOCATION + colum.getPosterMid());
//                        if (colum.getPosterLarge() != null)
//                            childColum.setPosterLarge(LOCATION + colum.getPosterLarge());

                        columBean.setContent(childColum);
                        columBean.setPosition(relevance.getPosition());
                        columBeans.add(columBean);
                    }
                }
                return new ResultBean(ServiceErrorCode.OK, columBeans);
            case "item":
                Pageable pageable = PageRequest.of(page, size);
                Page<Relevance> relevancePage = relevanceRepository.findByParentColumIdOrderByPositionDesc(id, pageable);
                Page<ItemBean> itemBeanPage = new Page<ItemBean>() {
                    @Override
                    public int getTotalPages() {
                        return relevancePage.getTotalPages();
                    }

                    @Override
                    public long getTotalElements() {
                        return relevancePage.getTotalElements();
                    }

                    @Override
                    public <U> Page<U> map(Function<? super ItemBean, ? extends U> function) {
                        return null;
                    }

                    @Override
                    public int getNumber() {
                        return relevancePage.getNumber();
                    }

                    @Override
                    public int getSize() {
                        return relevancePage.getSize();
                    }

                    @Override
                    public int getNumberOfElements() {
                        return relevancePage.getNumberOfElements();
                    }

                    @Override
                    public List<ItemBean> getContent() {
                        List<ItemBean> itemBeans = new ArrayList<>();
                        for (Relevance relevance : relevancePage.getContent()) {
                            if (relevance.getChildItemId() != 0) {
                                Item item = itemRepository.getOne(relevance.getChildItemId());
                                ItemBean itemBean = new ItemBean();
                                itemBean.setId(item.getId());
                                itemBean.setProviderId(item.getProvider().getProviderId());
                                itemBean.setAssetId(item.getAssetId());
                                itemBean.setTitle(item.getTitle());
                                itemBean.setPlayUrl(item.getPlayUrl());
                                itemBean.setPosition(relevance.getPosition());
                                itemBean.setOnline(relevance.getOnline());
                                itemBean.setItemOnline(item.getOnline());
                                itemBeans.add(itemBean);
                            }
                        }
                        return itemBeans;
                    }

                    @Override
                    public boolean hasContent() {

                        return relevancePage.hasContent();
                    }

                    @Override
                    public Sort getSort() {
                        return null;
                    }

                    @Override
                    public boolean isFirst() {
                        return relevancePage.isFirst();
                    }

                    @Override
                    public boolean isLast() {
                        return relevancePage.isLast();
                    }

                    @Override
                    public boolean hasNext() {
                        return relevancePage.hasNext();
                    }

                    @Override
                    public boolean hasPrevious() {
                        return relevancePage.hasPrevious();
                    }

                    @Override
                    public Pageable nextPageable() {
                        return relevancePage.nextPageable();
                    }

                    @Override
                    public Pageable previousPageable() {
                        return relevancePage.previousPageable();
                    }

                    @Override
                    public Iterator<ItemBean> iterator() {
                        return null;
                    }
                };

                return new ResultBean(ServiceErrorCode.OK, itemBeanPage);
            default:
                actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_GET.getContent(), ServiceErrorCode.UNKNOWN_COLUM_CONTENTTYPE.getMsg(), ActionRecord.LEVEL.errorRecord, new Date()), systemService.getCurrentUserName());
                return new ResultBean(ServiceErrorCode.UNKNOWN_COLUM_CONTENTTYPE);
        }
    }

    /**
     * 新增栏目
     *
     * @param name
     * @param type
     * @param isRoot
     * @param posterSmall
     * @param posterMid
     * @param posterLarge
     * @param brief
     * @param director
     * @param actors
     * @param year
     * @param licensingWndowStart
     * @param licensingWndowEnd
     * @param parentColumId
     * @return
     */
    @PostMapping("/add")
    @CrossOrigin
    public ResultBean addColumInfo(String name,
                                   Colum.Type type,
                                   Boolean isRoot,
                                   @RequestParam(required = false) String providerId,
                                   @RequestParam(required = false) String posterSmall,
                                   @RequestParam(required = false) String posterMid,
                                   @RequestParam(required = false) String posterLarge,
                                   @RequestParam(required = false) String brief,
                                   @RequestParam(required = false) String director,
                                   @RequestParam(required = false) String actors,
                                   @RequestParam(required = false) String year,
                                   @RequestParam(required = false) String licensingWndowStart,
                                   @RequestParam(required = false) String licensingWndowEnd,
                                   @RequestParam(required = false, defaultValue = "0") long parentColumId) {
        //logger.info("parentColunmId:{}",parentColumId);
        Colum colum = columRepository.findFirstByName(name);
        if (colum != null) {
            actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_ADD.getContent(), ServiceErrorCode.UNKNOWN_COLUM_REPEATNAME.getMsg(), ActionRecord.LEVEL.errorRecord, new Date()), systemService.getCurrentUserName());
            return new ResultBean(ServiceErrorCode.UNKNOWN_COLUM_REPEATNAME);
        }
        if (isRoot && columRepository.existsByIsRootTrue()) {
            actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_ADD.getContent(), ServiceErrorCode.UNKNOWN_COLUM_ROOTEXIST.getMsg(), ActionRecord.LEVEL.errorRecord, new Date()), systemService.getCurrentUserName());
            return new ResultBean(ServiceErrorCode.UNKNOWN_COLUM_ROOTEXIST);
        }

        colum = new Colum();
        colum.setName(name);
        colum.setType(type);
        colum.setRoot(isRoot);
        colum.setBrief(brief);
        colum.setDirector(director);
        colum.setActors(actors);
        colum.setYear(year);
        colum.setProviderId(providerId);
        String nameSpell = ChineseCharUtil.getFirstSpell(colum.getName());
        colum.setNameSpell(nameSpell);
        String directorSpell = ChineseCharUtil.getFirstSpell(colum.getDirector());
        colum.setDirectorSpell(directorSpell);
        String actorSpell = ChineseCharUtil.getFirstSpell(colum.getActors());
        colum.setActorsSpell(actorSpell);
        colum.setRoot(isRoot);
        if (licensingWndowStart != null & licensingWndowEnd != null) {
            colum.setLicensingWindowStart(TimeUtil.getDateByyyyyMMdd(licensingWndowStart));
            colum.setLicensingWindowEnd(TimeUtil.getDateByyyyyMMdd(licensingWndowEnd));
        }
        if (posterSmall != null) colum.setPosterSmall(posterSmall);
        if (posterMid != null) colum.setPosterMid(posterMid);
        if (posterLarge != null) colum.setPosterLarge(posterLarge);

        colum = columRepository.save(colum);

        Relevance relevance = new Relevance();
        if (isRoot) {
            return new ResultBean(ServiceErrorCode.OK);
        }
        Colum parentColum = columRepository.getOne(parentColumId);
        if (parentColum == null) {
            return new ResultBean(ServiceErrorCode.UNKNOWN_COLUM);
        }
        relevance.setParentColumId(parentColum.getId());
        relevance.setChildColumId(colum.getId());
        //relevance.setPosition(index);
        relevance.setOnline(false);//默认下线
        relevance.setLastUpdateTime(new Date());

        relevanceRepository.save(relevance);

        actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_ADD.getContent() + ">>>" + colum.getName(), ServiceErrorCode.OK.getMsg(), ActionRecord.LEVEL.normalRecord, new Date()), systemService.getCurrentUserName());

        return new ResultBean(ServiceErrorCode.OK);

    }

    /**
     * 删除栏目
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete")
    @CrossOrigin
    public ResultBean deleteColum(long id) {

        deleteRelevance(id);

        relevanceRepository.deleteByChildColumId(id);

        actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_DELETE.getContent(), ServiceErrorCode.OK.getMsg(), ActionRecord.LEVEL.normalRecord, new Date()), systemService.getCurrentUserName());

        return new ResultBean(ServiceErrorCode.OK);

    }

    private void deleteRelevance(long id) {
        //logger.info("colum:{}",id);
        columRepository.deleteById(id);

        if (relevanceRepository.existsByParentColumId(id)) {
            List<Relevance> relevances = relevanceRepository.findByParentColumId(id);
            for (Relevance relevance : relevances) {
                if (relevance.getChildColumId() != 0) {
                    deleteRelevance(relevance.getChildColumId());
                }

            }
            //logger.info("deleteRelevanceByParent:{}",id);
            relevanceRepository.deleteByParentColumId(id);
        }

    }

    /**
     * 更新栏目
     *
     * @param id
     * @param name
     * @param posterSmall
     * @param posterMid
     * @param posterLarge
     * @param type
     * @param brief
     * @param director
     * @param actors
     * @param year
     * @param licensingWndowStart
     * @param licensingWndowEnd
     * @return
     */
    @PostMapping("/update")
    @CrossOrigin
    public ResultBean updateColum(long id,
                                  @RequestParam(required = false) String name,
                                  @RequestParam(required = false) String posterSmall,
                                  @RequestParam(required = false) String posterMid,
                                  @RequestParam(required = false) String posterLarge,
                                  @RequestParam(required = false) Colum.Type type,
                                  @RequestParam(required = false) String brief,
                                  @RequestParam(required = false) String director,
                                  @RequestParam(required = false) String actors,
                                  @RequestParam(required = false) String year,
                                  @RequestParam(required = false) String licensingWndowStart,
                                  @RequestParam(required = false) String licensingWndowEnd) {


        Colum colum = columRepository.getOne(id);
        if (colum == null) {

            actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_UPDATE.getContent(), ServiceErrorCode.UNKNOWN_COLUM.getMsg(), ActionRecord.LEVEL.errorRecord, new Date()), systemService.getCurrentUserName());

            return new ResultBean(ServiceErrorCode.UNKNOWN_COLUM);
        }

//        colum = new Colum();
        colum.setName(name);
        colum.setType(type);
        colum.setBrief(brief);
        colum.setDirector(director);
        colum.setActors(actors);
        colum.setYear(year);
        String nameSpell = ChineseCharUtil.getFirstSpell(colum.getName());
        colum.setNameSpell(nameSpell);
        String directorSpell = ChineseCharUtil.getFirstSpell(colum.getDirector());
        colum.setDirectorSpell(directorSpell);
        String actorSpell = ChineseCharUtil.getFirstSpell(colum.getActors());
        colum.setActorsSpell(actorSpell);
        if (licensingWndowStart != null & licensingWndowEnd != null) {
            colum.setLicensingWindowStart(TimeUtil.getDateByyyyyMMdd(licensingWndowStart));
            colum.setLicensingWindowEnd(TimeUtil.getDateByyyyyMMdd(licensingWndowEnd));
        }
        if (posterSmall != null) {
            colum.setPosterSmall(posterSmall);
        } else {
            colum.setPosterSmall(null);
        }
        if (posterMid != null) {
            colum.setPosterMid(posterMid);
        } else {
            colum.setPosterMid(null);
        }
        if (posterLarge != null) {
            colum.setPosterLarge(posterLarge);
        } else {
            colum.setPosterLarge(null);
        }

        columRepository.save(colum);

        actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_UPDATE.getContent(), ServiceErrorCode.OK.getMsg(), ActionRecord.LEVEL.normalRecord, new Date()), systemService.getCurrentUserName());

        return new ResultBean(ServiceErrorCode.OK, colum);

    }

    /**
     * 关联项目到栏目
     *
     * @param columId
     * @param relativeIdList
     * @param relativeType
     * @return
     */
    @PostMapping("/relevance")
    @CrossOrigin
    public ResultBean relevanceProjectToColum(long columId,
                                              @RequestParam("relativeIdList") List<Long> relativeIdList,
                                              RelativeType relativeType
    ) {
        Colum colum = columRepository.getOne(columId);
        if (colum == null) {

            actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_RELEVANCE.getContent(), ServiceErrorCode.UNKNOWN_COLUM.getMsg(), ActionRecord.LEVEL.errorRecord, new Date()), systemService.getCurrentUserName());

            return new ResultBean(ServiceErrorCode.UNKNOWN_COLUM);
        }
        switch (relativeType) {
            case item:
                for (long relativeId : relativeIdList) {
                    Item childItem = itemRepository.getOne(relativeId);
                    //如果节目数据为空 或者 已有关联，跳过处理
                    if (childItem == null || (relevanceRepository.existsByParentColumIdAndChildItemId(columId, relativeId))) {
                        continue;
                    }

                    Relevance relevance = new Relevance();
                    relevance.setParentColumId(columId);
                    relevance.setChildItemId(relativeId);
                    relevance.setOnline(false);
                    relevance.setLastUpdateTime(new Date());

                    relevanceRepository.save(relevance);

                }
                break;
            case colum:
                for (int i = 0; i < relativeIdList.size(); i++) {
                    //Colum childColum = columRepository.getOne(relativeIdList.get(i));
                    Relevance relevance = relevanceRepository.findByParentColumIdAndChildColumId(columId, relativeIdList.get(i));
                    if (relevance == null) {
                        relevance = new Relevance();
                        relevance.setParentColumId(columId);
                        relevance.setChildColumId(relativeIdList.get(i));
                        relevance.setOnline(false);
                        relevance.setLastUpdateTime(new Date());
                    } else {
                        relevance.setLastUpdateTime(new Date());
                    }

                    relevanceRepository.save(relevance);

                }
                break;
            default:

                actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_RELEVANCE.getContent(), ServiceErrorCode.UNKNOWN_RELEVANCE_WRONGTYPE.getMsg(), ActionRecord.LEVEL.errorRecord, new Date()), systemService.getCurrentUserName());

                return new ResultBean(ServiceErrorCode.UNKNOWN_RELEVANCE_WRONGTYPE);
        }

        actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_RELEVANCE.getContent(), ServiceErrorCode.OK.getMsg(), ActionRecord.LEVEL.normalRecord, new Date()), systemService.getCurrentUserName());

        return new ResultBean(ServiceErrorCode.OK);

    }

    /**
     * 删除关联关系
     *
     * @param columId
     * @param relativeIdList
     * @param relativeType
     * @return
     */
    @DeleteMapping("/relavance")
    @CrossOrigin
    public ResultBean deleteRelevance(long columId,
                                      @RequestParam("relativeIdList") List<Long> relativeIdList,
                                      RelativeType relativeType) {
        Colum colum = columRepository.getOne(columId);
        if (colum == null) {

            actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_RELEVANCE_DELETE.getContent(), ServiceErrorCode.UNKNOWN_COLUM.getMsg(), ActionRecord.LEVEL.errorRecord, new Date()), systemService.getCurrentUserName());

            return new ResultBean(ServiceErrorCode.UNKNOWN_COLUM);
        }
        switch (relativeType) {
            case item:
                for (long itemId : relativeIdList) {
                    //Item childItem = itemRepository.getOne(itemId);
                    relevanceRepository.deleteByParentColumIdAndChildItemId(columId, itemId);
                }
                break;
            case colum:
                for (long childColumId : relativeIdList) {
                    //Colum childcolum = columRepository.getOne(childColumId);
                    relevanceRepository.deleteByParentColumIdAndChildColumId(columId, childColumId);
                }
                break;
            default:
                actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_RELEVANCE_DELETE.getContent(), ServiceErrorCode.UNKNOWN_RELEVANCE_WRONGTYPE.getMsg(), ActionRecord.LEVEL.errorRecord, new Date()), systemService.getCurrentUserName());

                return new ResultBean(ServiceErrorCode.UNKNOWN_RELEVANCE_WRONGTYPE);
        }

        actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_RELEVANCE_DELETE.getContent(), ServiceErrorCode.OK.getMsg(), ActionRecord.LEVEL.normalRecord, new Date()), systemService.getCurrentUserName());

        return new ResultBean(ServiceErrorCode.OK);

    }

    /**
     * 更改栏目中项目的上下架状态
     *
     * @param columId
     * @param relativeIdList
     * @param relativeType
     * @param online
     * @return
     */
    @PostMapping("/relavance/update")
    @CrossOrigin
    public ResultBean updateRelevance(long columId,
                                      @RequestParam("relativeIdList") List<Long> relativeIdList,
                                      RelativeType relativeType,
                                      Boolean online) {
        Colum colum = columRepository.getOne(columId);
        if (colum == null) {
            actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_UPDATE_ONLINE.getContent(), ServiceErrorCode.UNKNOWN_COLUM.getMsg(), ActionRecord.LEVEL.errorRecord, new Date()), systemService.getCurrentUserName());

            return new ResultBean(ServiceErrorCode.UNKNOWN_COLUM);
        }
        switch (relativeType) {
            case item:
                for (long relativeId : relativeIdList) {
                    Item childItem = itemRepository.getOne(relativeId);
                    //如果节目数据为空 或者 已有关联，跳过处理
                    if (childItem == null || (!relevanceRepository.existsByParentColumIdAndChildItemId(columId, relativeId))) {
                        continue;
                    }
                    Relevance relevance = relevanceRepository.findByParentColumIdAndChildItemId(columId, relativeId);

                    relevance.setOnline(online);
                    relevance.setLastUpdateTime(new Date());


                    relevanceRepository.save(relevance);

                }
                break;
            case colum:
                for (long relativeId : relativeIdList) {
                    Colum childColum = columRepository.getOne(relativeId);
                    //如果节目数据为空 或者 已有关联，跳过处理
                    if (childColum == null || (!relevanceRepository.existsByParentColumIdAndChildColumId(columId, relativeId))) {
                        continue;
                    }
                    Relevance relevance = relevanceRepository.findByParentColumIdAndChildColumId(columId, relativeId);

                    childColum.setOnline(online);

                    columRepository.save(childColum);

                    relevance.setOnline(online);
                    relevance.setLastUpdateTime(new Date());

                    relevanceRepository.save(relevance);

                }
                break;
            default:
                actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_UPDATE_ONLINE.getContent(), ServiceErrorCode.UNKNOWN_RELEVANCE_WRONGTYPE.getMsg(), ActionRecord.LEVEL.errorRecord, new Date()), systemService.getCurrentUserName());

                return new ResultBean(ServiceErrorCode.UNKNOWN_RELEVANCE_WRONGTYPE);
        }

        actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_UPDATE_ONLINE.getContent(), ServiceErrorCode.OK.getMsg(), ActionRecord.LEVEL.normalRecord, new Date()), systemService.getCurrentUserName());

        return new ResultBean(ServiceErrorCode.OK);
    }

    /**
     * 对关联关系中子栏目排序
     *
     * @param sortList
     * @return
     */
    @PostMapping("/sort")
    @CrossOrigin
    public ResultBean sortColum(long parentId, String type,
                                @RequestParam(value = "sortList") List<String> sortList) {

        List<Relevance> relevances = new ArrayList<>();
        for (String relevance : sortList) {
            long id = Long.parseLong(relevance.split(":")[0]);
            int position = Integer.parseInt(relevance.split(":")[1]);
            if ("colum".equals(type)) {
                Relevance res = relevanceRepository.findByParentColumIdAndChildColumId(parentId, id);
                if (res == null) {

                    actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_SORT.getContent(), ServiceErrorCode.UNKNOWN_RELEVANCE.getMsg(), ActionRecord.LEVEL.errorRecord, new Date()), systemService.getCurrentUserName());

                    return new ResultBean(ServiceErrorCode.UNKNOWN_RELEVANCE);
                }
                res.setPosition(position);
                relevances.add(res);
            } else if ("item".equals(type)) {
                Relevance res = relevanceRepository.findByParentColumIdAndChildItemId(parentId, id);
                if (res == null) {

                    actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_SORT.getContent(), ServiceErrorCode.UNKNOWN_RELEVANCE.getMsg(), ActionRecord.LEVEL.errorRecord, new Date()), systemService.getCurrentUserName());

                    return new ResultBean(ServiceErrorCode.UNKNOWN_RELEVANCE);
                }
                res.setPosition(position);
                relevances.add(res);
            }
        }

        relevanceRepository.saveAll(relevances);

        actionService.sendActionRecord2User(new ActionRecord(systemService.getCurrentUserName(), ActionRecord.ACTION.COLUM_SORT.getContent(), ServiceErrorCode.OK.getMsg(), ActionRecord.LEVEL.normalRecord, new Date()), systemService.getCurrentUserName());

        return new ResultBean(ServiceErrorCode.OK);
    }



    public class ColumBean {

        private Colum content;
        private int position;

        public Colum getContent() {
            return content;
        }

        public void setContent(Colum content) {
            this.content = content;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

    public class ItemBean {

        private long id;
        private String providerId;
        private String assetId;
        private String title;
        private int position;
        private String playUrl;
        private Boolean online;
        private Boolean itemOnline;

        public Boolean getItemOnline() {
            return itemOnline;
        }

        public void setItemOnline(Boolean itemOnline) {
            this.itemOnline = itemOnline;
        }

        public String getPlayUrl() {
            return playUrl;
        }

        public void setPlayUrl(String playUrl) {
            this.playUrl = playUrl;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getProviderId() {
            return providerId;
        }

        public void setProviderId(String providerId) {
            this.providerId = providerId;
        }

        public String getAssetId() {
            return assetId;
        }

        public void setAssetId(String assetId) {
            this.assetId = assetId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public Boolean getOnline() {
            return online;
        }

        public void setOnline(Boolean online) {
            this.online = online;
        }
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
