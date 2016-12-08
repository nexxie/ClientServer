package com.VakhovYS;

import java.io.*;
        import java.net.ServerSocket;
        import java.net.Socket;
        import java.text.SimpleDateFormat;
        import java.util.Date;


public class CryptoServer {

    String data = new String("Message recieved");

    private final String LOG = "log.txt";
    private int port;

    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");

    Users users = new Users();

    public CryptoServer(int port) {
        this.port = port;
    }

    private void start() throws IOException {
        try (ServerSocket ssocket = new ServerSocket(port)) {
            System.out.println("Server started on " + ssocket);

            while (true) {
                Socket sock = ssocket.accept();

                try {
                    process(sock);
                }
                catch (ClassNotFoundException e) {
                    System.err.println("Wrong message was received");

                    e.printStackTrace();
                }
                finally {
                    sock.close();
                }
            }
        }
    }

    private void process(Socket sock) throws IOException, ClassNotFoundException {
        String host = sock.getInetAddress().getHostAddress();

        try (ObjectInputStream objIn = new ObjectInputStream(new CryptoInputStream(sock.getInputStream()));
             ObjectOutputStream out = new ObjectOutputStream(new CryptoOutputStream(sock.getOutputStream()))) {
            Object obj = objIn.readObject();

            if (obj instanceof Showusers) {
                data = users.getUsers();
            } else if (obj instanceof Ping) {
                long delay = System.currentTimeMillis() - ((Ping) obj).getTime();
                data = "Ping time: " + Long.toString(delay) + " ms";
            }else {
                addToUsers((Message) obj, host);
                printMessage((Message) obj, host);
                writeToLog((Message) obj, host);
                data = "Message recieved";
            }
            out.writeObject(data);


        }
        catch (IOException | ClassNotFoundException | RuntimeException e) {
            System.err.println("Failed process connection from: " + host);

            e.printStackTrace();

            throw e;
        }
    }

    private void printMessage(Message msg, String senderAddr) {
        System.out.printf("%s (%s) at %s wrote: %s\n", msg.getSender(), senderAddr, format.format(new Date(msg.getTimestamp())), msg.getText());
    }


    public void writeToLog(Message msg, String senderAddr) throws IOException {

        FileWriter fileWriter = new FileWriter(LOG, true);

        StringBuilder line = new StringBuilder();
        line.append("Name: ").append(msg.getSender()).append(" Address: ").append(senderAddr).append(" Date: ").append(format.format(new Date(msg.getTimestamp()))).append(" Message: ").append(msg.getText());
        fileWriter.write(line.toString() + "\n");
        fileWriter.close();

    }

    private void addToUsers(Message msg, String senderAddr) throws IOException {

        users.addUser(msg.getSender());

    }


    public static void main(String[] args) throws IOException {
        if (args == null || args.length == 0)
            throw new IllegalArgumentException("Port must be specified");

        int port = Integer.parseInt(args[0]);

        CryptoServer cryptoServer = new CryptoServer(port);

        cryptoServer.start();


    }
}