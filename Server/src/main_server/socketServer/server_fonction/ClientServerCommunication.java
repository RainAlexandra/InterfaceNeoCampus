package main_server.socketServer.server_fonction;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class ClientServerCommunication implements Runnable {

    private Msg msg;
    private Socket sock;
    private String idClient = null;
    private ObjectOutputStream writer = null;
    private BufferedInputStream reader = null;
    private boolean isConected = false;
    private ClientsServerSocketInit server = null;

    public ClientServerCommunication(Socket sock) {
        this.sock = sock;
        try {
            writer = new ObjectOutputStream(sock.getOutputStream());
            reader = new BufferedInputStream(sock.getInputStream());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // Le traitement lancé dans un thread séparé
    public void run() {
        try {
            waitCommand();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void waitCommand() throws ClassNotFoundException {
        System.err.println("Lancement du traitement de la connexion cliente");

        boolean closeConnexion = false;
        // tant que la connexion est active, on traite les demandes
        while (!sock.isClosed()) {

            try {

                // writer = new PrintWriter(sock.getOutputStream());
                // reader = new BufferedInputStream(sock.getInputStream());
                String toSend = "";
//				Object objectRecu = reader.readObject();
                String reponse = read();

                if (msgOk(reponse)) {
                    msg = new Msg(reponse);

                    InetSocketAddress remote = (InetSocketAddress) sock.getRemoteSocketAddress();

                    // On affiche quelques infos, pour le débuggage
                    String debug = "";
                    debug = "Thread : " + Thread.currentThread().getName() + ". ";
                    debug += "Demande de l'adresse : " + remote.getAddress().getHostAddress() + ".";
                    debug += " Sur le port : " + remote.getPort() + ".\n";
                    debug += "\t -> Commande reçue : " + msg.getTypeOfmsg() + "\n";
                    System.err.println("\n" + debug);

                    if (!isConected) { // on demande l'authentification avat toute communication
                        closeConnexion = connection();
                    } else { // il est connecter
                        closeConnexion = communication();
                    }
                }

                if (closeConnexion) {
                    System.err.println("COMMANDE CLOSE DETECTEE ! ");
                    writer = null;
                    reader = null;
                    server.dellClient(idClient); // on supprime le client dans ce cas
                    sock.close();
                    break;
                }
            } catch (SocketException e) {
                System.err.println("LA CONNEXION A ETE INTERROMPUE ! ");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return
     */
    private boolean communication() {
        String toSend[] = null;
        boolean closeConnexion = false;
        switch (msg.getTypeOfmsg()) {
            case CREATE_TICKET:
                System.out.println("titre du ticket :"+msg.getTitle());
                toSend = server.createTicket(idClient, msg);
                break;
            case ANSWAR: // repondre à un ticket
                toSend = server.answarback(idClient, msg);
                break;
            case GET_TICKET:
                toSend = server.getListOfTicket(msg, idClient);
                break;
            case GET_MSG:
                toSend = server.getListOfMsg(idClient, msg);
                break;
            case CLOSE_CONN: // fermeture
//			toSend = "Communication terminée";
                closeConnexion = true;
                isConected = false;
                break;
            case GET_GOUPE:
                server.updateInfos(msg.getLogin());
                toSend[0] = "43000";
                toSend[1] = idClient;
                break;
            default:
                toSend = unknowRequest();
                break;
        }

        try {
            writer.writeObject(toSend);
            writer.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return closeConnexion;
    }

    /**
     * @return @throws IOException
     */
    private boolean connection() throws IOException {
        boolean closeConnexion = false;
        System.out.println("Demande de connexion");
        if (msg.getTypeOfmsg() == TypeOfRequest.AUTHENTIFICATION) {
            if (authentification(msg)) {
                server.updateInfos(idClient); // on met à jour les infos du client
                String[] CONN_OK = {"5200", "CONN_OK"};
                writer.writeObject(CONN_OK); //on envoie la confiramation de connexion
                writer.flush();
            } else {
                System.out.println("je ne suis pas connecter et le type de msg n'est pas authent");
                writer.writeObject(loginError()); // la verification des id a échoué
                writer.flush();
            }
        } else if (msg.getTypeOfmsg() == TypeOfRequest.CLOSE_CONN) {
            closeConnexion = true;
            System.out.println("erreur et d'conexion");
            writer.writeObject(loginError()); // la verification des id a échoué
            writer.flush();
        } else {
            System.out.println("erreur d'authentification");
            writer.writeObject(loginError()); // la verification des id a échoué
            writer.flush();
        }
        return closeConnexion;
    }

    /**
     * @param reponse
     * @return
     */
    private boolean msgOk(String reponse) {
        return !reponse.equals("");
    }

    /**
     * @param msg2
     * @return
     */
    private boolean authentification(Msg msg2) {
        // TODO le code d'authentification ici
        String id = msg.getLogin();
        String pwd = msg.getPwd();
        idClient = id; // pour l'instant on met celui la mais on peut mettre un autre
        if (server.login(id, pwd)) {
            server.addClient(idClient, writer); // on ajoute le nouveau client une fois authentifier
            isConected = true;
        }
        return isConected;
    }

    // La méthode que nous utilisons pour lire les réponses
    private String read() throws IOException {
        String response = "";
        int stream;
        byte[] b = new byte[4096];
        stream = reader.read(b);
        // System.out.println("byte : "+b+"\tsteam : "+stream);
        if (stream >= 0) {
            response = new String(b, 0, stream);
        }
        return response;
        // return reader.readLine();
    }

    /**
     * @return the server
     */
    public ClientsServerSocketInit getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(ClientsServerSocketInit server) {
        this.server = server;
    }

    /**
     * @return
     */
    public String[] loginError() {
        String[] error = {"0100", "ERROR_AUTHENTIFICATION"};
        return error;
    }

    public String[] unknowRequest() {
        String[] error = {"0000", "ERROR_UNKNOW_REQUEST"};
        return error;
    }

}
