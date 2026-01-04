package com.example.fileproc.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RequestLogRepository extends JpaRepository<RequestLogEntity, UUID> {
}
