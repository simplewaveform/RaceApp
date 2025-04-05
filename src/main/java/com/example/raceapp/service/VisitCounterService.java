package com.example.raceapp.service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class VisitCounterService {
    private final AtomicLong totalCounter = new AtomicLong(0);
    private final Set<String> uniqueClients = ConcurrentHashMap.newKeySet();

    public synchronized void recordVisit(String clientId) {
        totalCounter.incrementAndGet();
        uniqueClients.add(clientId);
    }

    public VisitStats getStats() {
        return new VisitStats(totalCounter.get(), uniqueClients.size());
    }

    public record VisitStats(long totalVisits, long uniqueVisits) {}
}