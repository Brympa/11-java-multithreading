# Задание 02 ThreadPoolExecutor (Задача 11.2)

## Условие

Эта программа предназначена для того, чтобы выполнить 30 долгих и ресурсоемких задач. Для ускорения процесса программист использовал ThreadPoolExecutor. Так как данные для каждой задачи занимают много оперативной памяти, он ограничил количество задач в очереди до 3. Но что-то пошло не так, и программа отказывается выполнять все задачи. Исправьте код так, чтобы все 30 задач выполнились в 3 потока, и цикл создания задач не переполнял очередь задач.

## Исходный код

```java
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
    }
}
```

## Анализ проблемы

1. **Переполнение очереди (`RejectedExecutionException`)**:
   - `corePoolSize = 3`, `maximumPoolSize = 3`.
   - Очередь `LinkedBlockingQueue<>(3)` вмещает максимум 3 задачи.
   - Всего одновременно в пуле может находиться до 6 задач (3 выполняются + 3 ждут в очереди).
   - Цикл в главном потоке генерирует задачи каждые 10 мс, тогда как выполнение каждой задачи занимает 1000–3000 мс.
   - При попытке добавить 7-ю задачу очередь переполняется, и `ThreadPoolExecutor` со стандартным `AbortPolicy` выбрасывает исключение `RejectedExecutionException`. Из-за этого цикл падает, и оставшиеся задачи не выполняются.
2. **Преждевременное завершение `main`**:
   - Главный поток заканчивает цикл и выходит из `main`, не дожидаясь завершения фоновых задач и не вызывая `executor.shutdown()`.

## Решение

1. В цикле создания задач перед отправкой новой задачи проверяется свободное место в очереди с помощью `executor.getQueue().remainingCapacity()`. Если очередь заполнена (`remainingCapacity() == 0`), главный поток ожидает освобождения слота в очереди:
   ```java
   while (executor.getQueue().remainingCapacity() == 0) {
       Thread.sleep(50);
   }
   ```
2. После добавления всех 30 задач вызывается корректное завершение пула потоков:
   ```java
   executor.shutdown();
   executor.awaitTermination(2, TimeUnit.MINUTES);
   ```
Это гарантирует, что очередь никогда не переполняется, все 30 задач успешно распределяются между 3 потоками пула, и программа корректно дожидается их выполнения.
