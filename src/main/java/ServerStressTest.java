import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerStressTest {

    public static void main(String[] args) throws InterruptedException {
        String targetUrl = "http://localhost:" + System.getenv("API_PORT") + "/" + System.getenv("TEST_ROUTE");
        int numberOfThreads = 50; // concurrent clients
        int requestsPerThread = 20; // requests per thread

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        AtomicInteger successfulRequests = new AtomicInteger();
        AtomicInteger failedRequests = new AtomicInteger();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < requestsPerThread; j++) {
                    try {
                        URL url = new URL(targetUrl);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setRequestMethod("GET");
                        int status = con.getResponseCode();

                        if (status == 200) {
                            successfulRequests.incrementAndGet();
                        } else {
                            failedRequests.incrementAndGet();
                        }

                        // Optional: read response
                        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                        while (in.readLine() != null) { }
                        in.close();
                        con.disconnect();

                    } catch (Exception e) {
                        failedRequests.incrementAndGet();
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES); // wait for all threads to finish

        long endTime = System.currentTimeMillis();
        double seconds = (endTime - startTime) / 1000.0;
        int totalRequests = successfulRequests.get() + failedRequests.get();
        double rps = totalRequests / seconds;

        System.out.println("Successful requests: " + successfulRequests.get());
        System.out.println("# of Threads: " + numberOfThreads);
        System.out.println("# requests per Thread : " + requestsPerThread);
        System.out.println("Failed requests: " + failedRequests.get());
        System.out.println("Total requests: " + totalRequests);
        System.out.println("Time taken (s): " + seconds);
        System.out.println("Requests per second (RPS): " + rps);
    }
}
