package com.disasterdecoded.nivaranflow.service;

import com.disasterdecoded.nivaranflow.model.AuditTrailItem;
import com.disasterdecoded.nivaranflow.repository.DataStore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuditService {
    private final DataStore dataStore = DataStore.getInstance();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void log(String action, String actor, String bureau, String details, String aictePermanentId, String caseId) {
        String id = "AUD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String timestamp = LocalDateTime.now().format(FORMATTER);
        AuditTrailItem item = new AuditTrailItem(id, timestamp, action, actor, bureau, details, aictePermanentId, caseId);
        dataStore.getAuditTrail().add(0, item);
    }

    public List<AuditTrailItem> getAllLogs() {
        return new ArrayList<>(dataStore.getAuditTrail());
    }
}
