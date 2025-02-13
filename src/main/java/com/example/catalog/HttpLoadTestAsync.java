package com.example.catalog;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HttpLoadTestAsync {
    private static final String TARGET_URL = "http://localhost:8080";
    private static final int REQUEST_COUNT = 1000;

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        List<CompletableFuture<Long>> futures = new ArrayList<>();
        Instant startTime = Instant.now();

        for (int i = 0; i < REQUEST_COUNT; i++) {
            System.out.println("Dispatching request: " + i);
            futures.add(sendAsyncRequest(client, i));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        double avgTime = futures.stream()
                .mapToLong(CompletableFuture::join)
                .average()
                .orElse(0);

        System.out.println("All requests completed in: " + Duration.between(startTime, Instant.now()).toMillis() + " ms");
        System.out.println("Average Response Time: " + avgTime + " ms");
    }

    private static CompletableFuture<Long> sendAsyncRequest(HttpClient client, int requestId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TARGET_URL))
                .GET()
                .build();

        Instant start = Instant.now();

        return client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenApply(response -> {
                    long duration = Duration.between(start, Instant.now()).toMillis();
                    System.out.println("Request " + requestId + " completed in " + duration + " ms");
                    return duration;
                })
                .exceptionally(e -> {
                    System.err.println("Request " + requestId + " failed: " + e.getMessage());
                    return -1L;
                });
    }
}
