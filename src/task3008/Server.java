package com.javarush.task.task30.task3008;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static ServerSocket serverSocket;
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(ConsoleHelper.readInt());
            System.out.println("Сервер запущен");
            while (true)
                new Handler(serverSocket.accept()).start();
        } catch (IOException e) {
            try {
                serverSocket.close();
                System.out.println("Произошла ошибка");
            }catch (IOException e1){

            }
        }

    }

    public static void sendBroadcastMessage(Message message){
        try {
            for (Map.Entry<String, Connection> temp : connectionMap.entrySet()) {
                temp.getValue().send(message);
            }
        } catch (IOException e){

        }

    }



     private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

         private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException{
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                Message answer = connection.receive();
                if (answer.getType() == MessageType.USER_NAME){
                    if(!answer.getData().isEmpty()){
                        if(!connectionMap.containsKey(answer.getData())){
                            connectionMap.put(answer.getData(), connection);
                            connection.send(new Message(MessageType.NAME_ACCEPTED));
                            return answer.getData();
                        }
                    }
                }
            }

         }

         private void sendListOfUsers(Connection connection, String userName) throws IOException{
             for (Map.Entry<String, Connection> temp : connectionMap.entrySet()){
                 if(!temp.getKey().equals(userName)){
                     connection.send(new Message(MessageType.USER_ADDED, temp.getKey()));
                 }
             }
         }

         private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException{
            while (true) {
                Message answer = connection.receive();
                if (answer.getType() == MessageType.TEXT) {
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + answer.getData()));
                }
                else {
                    ConsoleHelper.writeMessage("Ошибка");
                }

            }
         }

         public void run(){
            ConsoleHelper.writeMessage("Установлено соединение с удаленным адресом " + socket.getRemoteSocketAddress());
            String name = null;
            try(Connection connection = new Connection(socket)) {

                name = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, name));
                sendListOfUsers(connection, name);
                serverMainLoop(connection, name);

            } catch (IOException | ClassNotFoundException e){
                ConsoleHelper.writeMessage("Произошла ошибка при обмене данных с удаленным сервером");
            } finally {
                if(name != null){
                    connectionMap.remove(name);
                    sendBroadcastMessage(new Message(MessageType.USER_REMOVED, name));
                }
                ConsoleHelper.writeMessage("Соединение с удаленным адресом закрыто");
            }


         }
    }


}
