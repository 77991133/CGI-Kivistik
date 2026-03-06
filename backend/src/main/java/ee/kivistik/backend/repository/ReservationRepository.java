package ee.kivistik.backend.repository;

import ee.kivistik.backend.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<ReservationEntity,Integer> {
}
