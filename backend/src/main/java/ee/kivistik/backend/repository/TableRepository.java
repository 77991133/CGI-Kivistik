package ee.kivistik.backend.repository;

import ee.kivistik.backend.entity.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for managing TableEntity objects.
 * <p>
 * Provides standard CRUD operations for restaurant tables,
 * such as saving, updating, deleting, and finding tables.
 * <p>
 * Since this interface extends JpaRepository, no implementation
 * is needed; Spring Data automatically provides the implementation
 * at runtime.
 */
public interface TableRepository extends JpaRepository<TableEntity, Integer> {

}