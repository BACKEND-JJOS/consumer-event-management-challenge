package com.ias.repository;

import com.ias.model.CapacityEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapacityRepository extends R2dbcRepository<CapacityEntity, Integer> {
}
