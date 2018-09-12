package tv.jiaying.acms.entity.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import tv.jiaying.acms.entity.Item;
import tv.jiaying.acms.entity.Provider;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByOrderByModifyDateDesc(Pageable pageable);

    Item getFirstByAssetIdAndProvider(String assetId, Provider provider);

    Page<Item> findByTitleContainingOrAssetIdContainingOrProvider(String title, String asset,Provider provider ,Pageable pageable);

    @Transactional
    @Modifying
    void deleteById(long id);
}
