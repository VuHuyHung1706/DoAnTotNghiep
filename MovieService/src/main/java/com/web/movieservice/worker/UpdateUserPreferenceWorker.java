package com.web.movieservice.worker;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
@Service
public class UpdateUserPreferenceWorker {

    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    @PostConstruct
    public void startWorker() {
        Thread workerThread = new Thread(() -> {
            while (true) {
                try {
                    Runnable task = taskQueue.take(); // Lấy task kế tiếp
                    task.run(); // Thực thi tuần tự
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        workerThread.setDaemon(true);
        workerThread.start();
    }

    public void submitTask(Runnable task) {
        taskQueue.offer(task);
    }
}
