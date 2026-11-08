package com.example.task02;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Task02MainTest {

    @Test
    public void testPoolExecutionCompletesAllTasksWithoutRejection() throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                3, 3, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(3));

        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger inProgress = new AtomicInteger(0);
        AtomicInteger maxObservedInProgress = new AtomicInteger(0);

        PoolExample.runTasks(executor, 30, 20, 50, count, inProgress, maxObservedInProgress);

        Assertions.assertThat(count.get())
                .as("Все 30 задач должны успешно завершиться")
                .isEqualTo(30);

        Assertions.assertThat(inProgress.get())
                .as("После завершения количество выполняемых задач должно быть 0")
                .isEqualTo(0);

        Assertions.assertThat(maxObservedInProgress.get())
                .as("Количество параллельно работающих задач не должно превышать 3")
                .isLessThanOrEqualTo(3);

        Assertions.assertThat(executor.isTerminated())
                .as("Пул потоков должен корректно завершить работу")
                .isTrue();
    }

    @Test
    public void testQueueRemainingCapacityLogic() {
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(3);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                3, 3, 1, TimeUnit.SECONDS, queue);

        Assertions.assertThat(queue.remainingCapacity()).isEqualTo(3);
        executor.shutdown();
    }
}
