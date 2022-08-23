package com.company;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

class ServerService {
    static PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream stream = socket.getOutputStream();
        return new PrintWriter(stream);
    }

    static Scanner getReader(Socket socket) throws IOException{
        InputStream stream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        return new Scanner(inputStreamReader);
    }

    static boolean isQuitMsg(String msg) {
        return "bye".equalsIgnoreCase(msg);
    }

    static boolean isEmptyMsg(String msg) {
        return msg == null || msg.isBlank();
    }

    static void sendResponse(String resp, Writer writer) throws IOException {
        writer.write(resp);
        writer.write(System.lineSeparator());
        writer.flush();
    }
}
