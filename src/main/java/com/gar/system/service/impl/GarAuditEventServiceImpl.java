package com.gar.system.service.impl;

import java.time.Instant;
import java.util.Optional;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gar.system.service.GarAuditEventService;

public class GarAuditEventServiceImpl implements GarAuditEventService {

	@Override
	public Page<AuditEvent> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<AuditEvent> findByDates(Instant instant, Instant instant2, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<AuditEvent> find(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

}
