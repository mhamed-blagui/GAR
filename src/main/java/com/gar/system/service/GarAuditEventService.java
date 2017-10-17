package com.gar.system.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GarAuditEventService {

	Page<AuditEvent> findAll(Pageable pageable);

	Page<AuditEvent> findByDates(Instant instant, Instant instant2, Pageable pageable);

	Optional<AuditEvent> find(Long id);

}
