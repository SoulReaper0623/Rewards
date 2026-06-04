package org.visa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.visa.entities.PointTypes;

public interface PointTypeRepository extends JpaRepository<PointTypes, Long> {

}
