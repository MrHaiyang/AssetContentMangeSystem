package tv.jiaying.acms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tv.jiaying.acms.controller.pojos.ResultBean;
import tv.jiaying.acms.entity.Order;
import tv.jiaying.acms.entity.repository.OrderRepository;
import tv.jiaying.acms.service.ServiceErrorCode;
import tv.jiaying.acms.util.TimeUtil;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController {

    private static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Resource
    OrderRepository orderRepository;

    @GetMapping("/list")
    @CrossOrigin
    public ResultBean getOrders(@RequestParam(required = false, defaultValue = "-1") int orderType,
                                @RequestParam(required = false) String start,
                                @RequestParam(required = false) String end, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = null;
        if (orderType == -1 & start == null & end == null) {
            orderPage = orderRepository.findAll(pageable);
        } else if (orderType == -1 & start != null & end != null) {
            Date startTime = TimeUtil.getDateByyyyyMMdd(start);
            Date endTime = TimeUtil.getDateByyyyyMMdd(end);
            orderPage = orderRepository.findAllByOrderDateBetween(startTime, endTime, pageable);
        } else if (orderType != -1 & start != null & end != null) {
            Date startTime = TimeUtil.getDateByyyyyMMdd(start);
            Date endTime = TimeUtil.getDateByyyyyMMdd(end);
            orderPage = orderRepository.findAllByOrderTypeAndOrderDateBetween(orderType, startTime, endTime, pageable);
        } else if (orderType != -1 & start == null & end == null) {
            orderPage = orderRepository.findAllByOrderType(orderType, pageable);
        }

        return new ResultBean(ServiceErrorCode.OK, orderPage);
    }
}
