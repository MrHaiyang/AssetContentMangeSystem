package tv.jiaying.acms.entity.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tv.jiaying.acms.entity.ActionRecord;

public interface ActionRecordRepository extends JpaRepository<ActionRecord, Long> {

    Page<ActionRecord> findByUsernameOrderByDateDesc(String username,Pageable pageable);
}
