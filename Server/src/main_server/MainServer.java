package main_server;

import main_server.socketServer.server_fonction.ClientsServerSocketInit;

public class MainServer {

    public static void main(String[] args) {
        Server main_server = new Server();
        String host  = "127.0.0.1";
        int port = 2345;
       
        
        ClientsServerSocketInit ts = new ClientsServerSocketInit(host, port, main_server);
        ts.open();
    }
}
