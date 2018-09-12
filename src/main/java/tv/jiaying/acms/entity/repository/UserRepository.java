package tv.jiaying.acms.entity.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tv.jiaying.acms.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAll(Pageable pageable);
}
