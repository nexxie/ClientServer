package com.VakhovYS;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Scanner;


public class CryptoClient {
    ps


    private SocketAddress serverAddr;

    private static String name;

    private Scanner scanner;

    public CryptoClient(SocketAddress serverAddr, Scanner scanner) {
        this.serverAddr = serverAddr;
        this.scanner = scanner;
    }
    private void start() throws IOException, ClassNotFoundException {
        System.out.println("Enter your name: ");

        name = scanner.nextLine();

        while (true) {
            System.out.println("Enter message to send: ");

            String msg = scanner.nextLine();

            if ("/exit".equals(msg))
                break;
            else if ("/nick".equals(msg)) {
                System.out.println("Enter new name:");

                name = scanner.nextLine();

                continue;
            }
            else if ("/myaddr".equals(msg)) {
                printAddresses();

                continue;
            }

            buildAndSendMessage(msg);
        }
    }

    private void printAddresses() throws SocketException {
        Enumeration e = NetworkInterface.getNetworkInterfaces();

        while(e.hasMoreElements()) {
            NetworkInterface n = (NetworkInterface) e.nextElement();

            Enumeration ee = n.getInetAddresses();

            while (ee.hasMoreElements()) {
                InetAddress i = (InetAddress) ee.nextElement();

                System.out.println(i.getHostAddress());
            }
        }
    }

    private void buildAndSendMessage(String msg) throws IOException, ClassNotFoundException {

        if (msg.equals("showusers")) {
            sendPrintMessage(new Showusers());
        } else if (msg.equals("ping")) {
            sendPing(new Ping(System.currentTimeMillis()));
        } else {
            Message message = new Message(System.currentTimeMillis(), name, msg);
            sendPrintMessage(message);
            System.out.println("Sent: " + message);
        }

    }


    private void readAnswer(Socket sock) throws IOException, ClassNotFoundException {

        try (ObjectInputStream objIn = new ObjectInputStream(new CryptoInputStream(sock.getInputStream()))) {

            Object confirm = objIn.readObject(); // принимаем ответ от сервера

            System.err.println(confirm.toString());
        }
    }

    private void sendPing(Ping p) throws IOException, ClassNotFoundException {
        try (Socket sock = new Socket()) {
            sock.connect(serverAddr);

            try (ObjectOutputStream objOut = new ObjectOutputStream(new CryptoOutputStream(sock.getOutputStream()))) {

                objOut.writeObject(p);
                objOut.toString()

                readAnswer(sock);

                objOut.flush();

            }
        }
    }

    private void sendPrintMessage(Message msg) throws IOException, ClassNotFoundException {
        try (Socket sock = new Socket()) {
            sock.connect(serverAddr);

            try (ObjectOutputStream objOut = new ObjectOutputStream(new CryptoOutputStream(sock.getOutputStream()))) {

                objOut.writeObject(msg);

                readAnswer(sock);

                objOut.flush();

            }
        }
    }

    private static SocketAddress parseAddress(String addr) {
        String[] split = addr.split(":");
        return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String addr = null;

        if (args != null && args.length > 0)
            addr = args[0];

        Scanner scanner = new Scanner(System.in);

        if (addr == null) {
            System.out.println("Enter server address");

            addr = scanner.nextLine();
        }

        CryptoClient client = new CryptoClient(parseAddress(addr), scanner);

        client.start();

    }
}