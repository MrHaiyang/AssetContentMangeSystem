package tv.jiaying.acms.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tv.jiaying.acms.entity.Provider;

public interface ProviderRepository extends JpaRepository<Provider,Long>{

    Provider findFirstByProviderId(String providerId);

    Provider findFirstByName(String name);
}
