package com.example.task01;

public class Lucky {
    static int x = 0;
    static int count = 0;

    static final Object xLock = new Object();
    static final Object countLock = new Object();

    public static void reset() {
        synchronized (xLock) {
            x = 0;
        }
        synchronized (countLock) {
            count = 0;
        }
    }

    public static int getX() {
        synchronized (xLock) {
            return x;
        }
    }

    public static int getCount() {
        synchronized (countLock) {
            return count;
        }
    }

    static class LuckyThread extends Thread {
        @Override
        public void run() {
            while (true) {
                int currentX;
                synchronized (xLock) {
                    if (x >= 999999) {
                        break;
                    }
                    x++;
                    currentX = x;
                }

                if ((currentX % 10) + (currentX / 10) % 10 + (currentX / 100) % 10 ==
                        (currentX / 1000) % 10 + (currentX / 10000) % 10 + (currentX / 100000) % 10) {
                    System.out.println(currentX);
                    synchronized (countLock) {
                        count++;
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        reset();
        Thread t1 = new LuckyThread();
        Thread t2 = new LuckyThread();
        Thread t3 = new LuckyThread();
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();
        System.out.println("Total: " + count);
    }
}
