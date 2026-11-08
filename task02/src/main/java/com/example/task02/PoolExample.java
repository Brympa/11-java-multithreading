package com.example.task02;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PoolExample {

    public static void main(String[] args) throws InterruptedException {

        // создаем пул для выполнения наших задач
        // максимальное количество созданных задач - 3
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                // не изменяйте эти параметры
                3, 3, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(3));

        // сколько задач выполнилось
        AtomicInteger count = new AtomicInteger(0);

        // сколько задач выполняется
        AtomicInteger inProgress = new AtomicInteger(0);

        // отправляем задачи на выполнение
        for (int i = 0; i < 30; i++) {
            final int number = i;
            Thread.sleep(10);

            // Ожидаем освобождения места в очереди, чтобы цикл создания задач не переполнял очередь
            while (executor.getQueue().remainingCapacity() == 0) {
                Thread.sleep(50);
            }

            System.out.println("creating #" + number);
            executor.submit(() -> {
                int working = inProgress.incrementAndGet();
                System.out.println("start #" + number + ", in progress: " + working);
                try {
                    // тут какая-то полезная работа
                    Thread.sleep(Math.round(1000 + Math.random() * 2000));
                } catch (InterruptedException e) {
                    // ignore
                }
                working = inProgress.decrementAndGet();
                System.out.println("end #" + number + ", in progress: " + working + ", done tasks: " + count.incrementAndGet());
                return null;
            });
        }

        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.MINUTES);
    }

    /**
     * Параметризованный метод выполнения задач для гибкого тестирования логики пула.
     */
    public static void runTasks(ThreadPoolExecutor executor, int totalTasks, long minSleepMs, long maxSleepMs,
                                AtomicInteger count, AtomicInteger inProgress, AtomicInteger maxObservedInProgress) throws InterruptedException {
        for (int i = 0; i < totalTasks; i++) {
            final int number = i;
            Thread.sleep(5);

            while (executor.getQueue().remainingCapacity() == 0) {
                Thread.sleep(10);
            }

            executor.submit(() -> {
                int working = inProgress.incrementAndGet();
                if (maxObservedInProgress != null) {
                    maxObservedInProgress.accumulateAndGet(working, Math::max);
                }
                try {
                    long sleepTime = minSleepMs + (long) (Math.random() * (maxSleepMs - minSleepMs));
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    // ignore
                }
                working = inProgress.decrementAndGet();
                count.incrementAndGet();
                return null;
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
}
