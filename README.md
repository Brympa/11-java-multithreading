# 11. Многопоточность

Задания по многопоточному программированию в Java.

## Список задач

* [Задание 01 Lucky (Задача 11.1)](task01/README.md)
* [Задание 02 ThreadPoolExecutor (Задача 11.2)](task02/README.md)

## Сборка и запуск тестов

Требуется JDK 21 и Maven 3.8+.

```bash
mvn test              # прогнать тесты всех заданий
mvn -pl task01 test   # прогнать тесты одного задания
mvn -fae test         # не останавливаться на первом упавшем задании
```

## Структура модуля

```
taskNN/
├── pom.xml
├── README.md                                            условие задания
└── src
    ├── main/java/com/example/taskNN/TaskNNMain.java      здесь пишется решение
    └── test/java/com/example/taskNN/TaskNNMainTest.java  тесты
```
