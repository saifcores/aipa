package com.aipa.infrastructure.persistence.repository;

import com.aipa.infrastructure.persistence.entity.CustomerEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {
}
