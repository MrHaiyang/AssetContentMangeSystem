package tv.jiaying.acms.entity.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tv.jiaying.acms.entity.Order;

import java.util.Date;

public interface OrderRepository extends JpaRepository<Order,Long> {

    Page<Order> findAllByOrderTypeAndOrderDateBetween(int orderType, Date start, Date end, Pageable pageable);

    Page<Order> findAll(Pageable pageable);

    Page<Order> findAllByStatus(int status,Pageable pageable);

    Page<Order> findAllByOrderType(int orderType,Pageable pageable);

    Page<Order> findAllByOrderDateBetween(Date start,Date end,Pageable pageable);


}
