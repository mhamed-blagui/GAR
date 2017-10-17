package com.gar.system.service.impl;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gar.system.repository.PersistenceAuditEventRepository;
import com.gar.system.service.GarAuditEventConverterService;
import com.gar.system.service.GarAuditEventService;

public class GarAuditEventServiceImpl implements GarAuditEventService {

	@Autowired
	private PersistenceAuditEventRepository persistenceAuditEventRepository;

	@Autowired
    private GarAuditEventConverterService auditEventConverter;

    public Page<AuditEvent> findAll(Pageable pageable) {
        return persistenceAuditEventRepository.findAll(pageable)
            .map(auditEventConverter::convertToAuditEvent);
    }

    public Page<AuditEvent> findByDates(Instant fromDate, Instant toDate, Pageable pageable) {
        return persistenceAuditEventRepository.findAllByAuditEventDateBetween(fromDate, toDate, pageable)
            .map(auditEventConverter::convertToAuditEvent);
    }

    public Optional<AuditEvent> find(Long id) {
        return Optional.ofNullable(persistenceAuditEventRepository.findOne(id)).map
            (auditEventConverter::convertToAuditEvent);
    }

}
