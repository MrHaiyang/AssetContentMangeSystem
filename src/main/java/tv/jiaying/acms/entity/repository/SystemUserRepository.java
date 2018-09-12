package tv.jiaying.acms.entity.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tv.jiaying.acms.entity.SystemUser;

public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {

    Page<SystemUser> findAll(Pageable pageable);

    SystemUser getFirstByUsername(String username);
}
