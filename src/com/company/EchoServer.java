package com.company;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class EchoServer {

    private final int port;
    private final ExecutorService pool = Executors.newCachedThreadPool();

    private EchoServer(int port) {
        this.port = port;
    }

    static EchoServer bindToPort() {
        return new EchoServer(8788);
    }

    void run() {
        try (var server = new ServerSocket(port)) {
            // обработка подключения
            while (!server.isClosed()) {
                Socket socket = server.accept();
                pool.submit(() -> handle(socket));
            }
        } catch (IOException e) {
            System.out.printf("Вероятнее всего порт %s занят.%n", port);
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) {
        System.out.printf("Подключен клиент: %s%n", socket);

        // логика обработки

        try (Scanner reader = ServerService.getReader(socket);
             PrintWriter pw = ServerService.getWriter(socket);
             socket) {
            ServerService.sendResponse("Привет " + socket, pw);
            while (true) {
                String message = reader.nextLine().strip();
                System.out.printf("Got: %s%n", message);


                if (ServerService.isEmptyMsg(message) || ServerService.isQuitMsg(message)) {
                    break;
                }

                ServerService.sendResponse(String.valueOf("bye".equalsIgnoreCase(message)), pw);
            }
        } catch (NoSuchElementException e) {
            System.out.println("Клиент закрыл соединение!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.printf("Клиент отключен! : %s%n", socket);
        }
    }
}
