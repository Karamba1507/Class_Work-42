package com.company;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class HeavyTasks {

public static void main(String[] args) {
  // особый сервисный объект, пул потоков, который
  // умеет параллельно выполнять задачи.
  ExecutorService pool = Executors.newCachedThreadPool();

  // создаём и закидываем задачи на выполнение
  int taskCount = 400; // попробуйте с числом задач в 256
  submitTasksInto(pool, taskCount);
  System.out.println("-------------------");

  // принудительно закрываем сервис параллельного 
  // выполнения задач. Если это не сделать, то сервис 
  // будет ожидать новые задачи ещё в течении 60 секунд
  // и только после этого завершит свою работу.
  pool.shutdown();

  // измеряем время выполнения всех задач
  measure(pool);
}

private static void submitTasksInto(ExecutorService pool
         , int taskCount) {
  System.out.println("Создаём задачи");
  IntStream.rangeClosed(1, taskCount)
    .mapToObj(i -> makeTask(i))
    .forEach(pool::submit);
}

private static Runnable makeTask(int taskId) {
  var temp = new Random().nextInt(20000) + 10000;
  var taskTime = (int) TimeUnit.MILLISECONDS
                   .toSeconds(temp);
  // вернём функц. объект представляющий "тяжелую" задачу
  // которая будет выполняться в отдельном потоке
  return () -> heavyTask(taskId, taskTime);
}

private static void heavyTask(int taskId, int taskTime) {
  var msg = String.format("Задача %s займёт %s секунд",
                     taskId, taskTime);
  System.out.println(msg);
  try {
    // имитируем долгую задачу. sleep() принимает значение
    // в миллисекундах, по этому умножаем на 1000
    Thread.sleep(taskTime * 1000);
    // задача завершилась
    System.out.printf("Завершилась задача %s," + 
        " выполнилась за %s секунд%n", taskId, taskTime);
  } catch (InterruptedException e) {
    e.printStackTrace();
  }
}

private static void measure(ExecutorService pool) {
  long start = System.nanoTime();
  try {
    // этот метод, обычно, используется для ожидания
    // завершения всех накопившихся задач,
    // но мы его используем для блокирования основного 
    // потока и тем самым замеряем время выполнения
    // наших задач
    pool.awaitTermination(600, TimeUnit.SECONDS);
  } catch (InterruptedException e) {
    e.printStackTrace();
  }
  long delta = TimeUnit.NANOSECONDS
                  .toMillis(System.nanoTime() - start);
  System.out.printf("Выполнение заняло: %s мсек %n"
                       , delta);
}

}