package com.example.raceapp.service;

import com.example.raceapp.exception.BadRequestException;
import com.example.raceapp.exception.InternalServerException;
import com.example.raceapp.exception.NotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class LogTaskService {
    private final Map<String, TaskWrapper> tasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final LogService logService;

    private static final int TTL_MINUTES = 60;

    public LogTaskService(LogService logService) {
        this.logService = logService;
        startTaskCleanupScheduler();
    }

    public String startLogGeneration(String date) {
        String taskId = generateShortId();
        CompletableFuture<Resource> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000);
                return logService.getLogFileForDate(date);
            } catch (NotFoundException | BadRequestException e) {
                throw new CompletionException(e);
            } catch (Exception e) {
                throw new CompletionException(new InternalServerException("Log generation failed"));
            }
        }).orTimeout(30, TimeUnit.SECONDS);

        tasks.put(taskId, new TaskWrapper(future));
        return taskId;
    }

    public String getTaskStatus(String taskId) {
        TaskWrapper wrapper = tasks.get(taskId);
        if (wrapper == null) return "NOT_FOUND";

        if (wrapper.future.isDone()) {
            return wrapper.future.isCompletedExceptionally() ? "FAILED" : "COMPLETED";
        }
        return "IN_PROGRESS";
    }

    public Resource getTaskResult(String taskId) {
        TaskWrapper wrapper = tasks.get(taskId);
        if (wrapper == null) throw new NotFoundException("Task not found");

        if (!wrapper.future.isDone()) {
            throw new IllegalStateException("Task not completed");
        }
        return wrapper.future.join();
    }

    private void startTaskCleanupScheduler() {
        scheduler.scheduleAtFixedRate(() ->
                tasks.entrySet().removeIf(entry ->
                        entry.getValue().isExpired()
                ), 1, 1, TimeUnit.HOURS);
    }

    private String generateShortId() {
        return UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 8)
                .toLowerCase();
    }

    public static class TaskWrapper {
        public final CompletableFuture<Resource> future;
        final Instant creationTime;

        TaskWrapper(CompletableFuture<Resource> future) {
            this.future = future;
            this.creationTime = Instant.now();
        }

        boolean isExpired() {
            return Instant.now().isAfter(
                    creationTime.plus(TTL_MINUTES, ChronoUnit.MINUTES)
            );
        }
    }

    public Map<String, TaskWrapper> getTasks() {
        return tasks;
    }
}