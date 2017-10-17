package com.gar.system.service;

import java.util.List;
import java.util.Map;

import org.springframework.boot.actuate.audit.AuditEvent;

import com.gar.system.model.PersistentAuditEvent;

public interface GarAuditEventConverterService {

	Map<String, String> convertDataToStrings(Map<String, Object> data);

	Map<String, Object> convertDataToObjects(Map<String, String> data);

	AuditEvent convertToAuditEvent(PersistentAuditEvent persistentAuditEvent);

	List<AuditEvent> convertToAuditEvent(Iterable<PersistentAuditEvent> persistentAuditEvents);

}
