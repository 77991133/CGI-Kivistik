package ee.kivistik.backend.repository;

import ee.kivistik.backend.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<TableEntity, Integer> {
}