package com.disasterdecoded.nivaranflow.service;

import com.disasterdecoded.nivaranflow.model.ComplaintCase;
import com.disasterdecoded.nivaranflow.model.Institution;
import com.disasterdecoded.nivaranflow.model.OverlapAlert;
import com.disasterdecoded.nivaranflow.repository.DataStore;

import java.util.*;
import java.util.stream.Collectors;

public class InstitutionService {
    private final DataStore dataStore = DataStore.getInstance();

    public List<Institution> getAllInstitutions() {
        return new ArrayList<>(dataStore.getInstitutions().values());
    }

    public Institution getByPermanentId(String aictePermanentId) {
        return dataStore.getInstitutions().get(aictePermanentId);
    }

    /**
     * Builds the unified 360-degree institution profile bringing related cases
     * from all AICTE bureaus into one single view.
     */
    public Map<String, Object> getInstitution360Profile(String aictePermanentId) {
        Institution inst = dataStore.getInstitutions().get(aictePermanentId);
        if (inst == null) return null;

        List<ComplaintCase> relatedCases = dataStore.getCases().values().stream()
                .filter(c -> aictePermanentId.equalsIgnoreCase(c.getAictePermanentId()))
                .sorted(Comparator.comparing(ComplaintCase::getId))
                .collect(Collectors.toList());

        List<OverlapAlert> relatedAlerts = dataStore.getOverlapAlerts().values().stream()
                .filter(a -> aictePermanentId.equalsIgnoreCase(a.getAictePermanentId()))
                .collect(Collectors.toList());

        Set<String> involvedBureaus = relatedCases.stream()
                .map(ComplaintCase::getBureau)
                .collect(Collectors.toSet());

        long activeCount = relatedCases.stream()
                .filter(c -> !"Closed".equalsIgnoreCase(c.getCurrentStatus()) && !"Resolved".equalsIgnoreCase(c.getCurrentStatus()))
                .count();

        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("institution", inst);
        profile.put("totalCases", relatedCases.size());
        profile.put("activeCasesCount", activeCount);
        profile.put("involvedBureaus", new ArrayList<>(involvedBureaus));
        profile.put("hasCrossBureauOverlap", involvedBureaus.size() > 1);
        profile.put("cases", relatedCases);
        profile.put("overlapAlerts", relatedAlerts);

        return profile;
    }
}
