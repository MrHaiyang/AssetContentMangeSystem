package tv.jiaying.acms.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tv.jiaying.acms.entity.Colum;

import javax.swing.*;

public interface ColumRepository extends JpaRepository<Colum, Long> {

    Colum findFirstById(long Id);

    Colum findFirstByName(String name);

    Boolean existsByIsRootTrue();

    //Boolean existsByRootIsTrue();

    Colum findFirstByIsRootIsTrue();
}
