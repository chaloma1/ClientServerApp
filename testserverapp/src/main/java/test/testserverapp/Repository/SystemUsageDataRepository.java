package test.testserverapp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import test.testserverapp.model.SystemUsageData;

import java.util.List;

@Transactional
@Repository
public interface SystemUsageDataRepository extends JpaRepository<SystemUsageData, Integer> {

    @Query(value = "SELECT * FROM testmorosystems.system_usage_data u WHERE u.hostname LIKE :hostname ORDER BY u.date_Of_Measurement DESC LIMIT 6", nativeQuery = true)
    List<SystemUsageData> findLatestUsages(@Param("hostname") String hostname);

    @Query(value = "SELECT DISTINCT u.hostname FROM testmorosystems.system_usage_data u" , nativeQuery = true)
    String[] findAllHostNames();

    @Query(value =
            "SELECT DISTINCT * FROM testmorosystems.system_usage_data u WHERE ((u.processor_usage > 90.0 AND u.free_memory < 1.0) OR u.processor_usage > 90.0 OR u.free_memory < 1.0) " +
                    "ORDER BY u.date_Of_Measurement DESC", nativeQuery = true)
    List<SystemUsageData> findDangerousUsages();
}
