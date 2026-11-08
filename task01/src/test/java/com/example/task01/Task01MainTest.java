package com.example.task01;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Task01MainTest {

    @BeforeEach
    public void setUp() {
        Lucky.reset();
    }

    @Test
    public void testLuckyCountCorrectness() throws InterruptedException {
        Lucky.main(new String[0]);

        Assertions.assertThat(Lucky.getCount())
                .as("Количество счастливых билетов от 1 до 999999 должно быть ровно 55251")
                .isEqualTo(55251);

        Assertions.assertThat(Lucky.getX())
                .as("Максимальное значение x должно быть 999999")
                .isEqualTo(999999);
    }

    @Test
    public void testSequentialMatchesParallel() {
        int sequentialCount = 0;
        for (int i = 1; i <= 999999; i++) {
            int d1 = i % 10;
            int d2 = (i / 10) % 10;
            int d3 = (i / 100) % 10;
            int d4 = (i / 1000) % 10;
            int d5 = (i / 10000) % 10;
            int d6 = (i / 100000) % 10;
            if (d1 + d2 + d3 == d4 + d5 + d6) {
                sequentialCount++;
            }
        }
        Assertions.assertThat(sequentialCount).isEqualTo(55251);
    }

    @Test
    public void testThreadExecution() throws InterruptedException {
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Lucky.LuckyThread();
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        Assertions.assertThat(Lucky.getCount()).isEqualTo(55251);
        Assertions.assertThat(Lucky.getX()).isEqualTo(999999);
    }
}
