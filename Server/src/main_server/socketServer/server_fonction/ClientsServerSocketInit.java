package main_server.socketServer.server_fonction;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import main_server.Server;

public class ClientsServerSocketInit {
    //contiendera la liste des client leur identifiant et leur flux de sortie

    private Map<String, ObjectOutputStream> listeDeClient = new HashMap<>();
    // private List<ObjectOutputStream> userToSend;

    private int nbClient = 0;
    // On initialise des valeurs par défaut
    private int port = 2345;
    private String host = "127.0.0.1";
    private ServerSocket server = null;
    private boolean isRunning = true;
    private Server main_server = null;

    public ClientsServerSocketInit(Server main_server) {
        this.main_server = main_server;
        try {
            server = new ServerSocket(port, 100, InetAddress.getByName(host));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientsServerSocketInit(String host, int port, Server main_server) {
        this.main_server = main_server;
        this.host = host;
        this.port = port;
        try {
            server = new ServerSocket(port, 100, InetAddress.getByName(host));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // On lance notre serveur
    public void open() {
        // Toujours dans un thread à part vu qu'il est dans une boucle infinie
        Thread t = new Thread(new Runnable() {
            public void run() {
                while (isRunning == true) {
                    try {
                        // On attend une connexion d'un client
                        Socket client = server.accept();
                        ClientServerCommunication newClient
                                = new ClientServerCommunication(client);
                        //on utilise cette fonction pour pouvoir utiliser les fonction de maniere
                        //globale dans la gestion des flux client server
                        indiquerServer(newClient);
                        // Une fois reçue, on la traite dans un thread séparé
                        System.out.println("Connexion cliente reçue.");
                        Thread t = new Thread(newClient);
                        t.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    server = null;
                }
            }
        });
        t.start();
    }

    //prend une liste d'utilisateur et retourne un tableau d'objets
    //aves l'ensemble des id de client co et leur writer
    //cette fonctionner est accesssible par u thread à la fois
    synchronized public Object[] userConnect(Set<String> userId) {
        List<String> userConn = new ArrayList<>();
        List<ObjectOutputStream> userToSend = new ArrayList<>();
        Object[] user = new Object[2];
        for (String u : userId) {
            if (listeDeClient.containsKey(u)) {
                userToSend.add(listeDeClient.get(u));
                userConn.add(u);
            }
        }
        user[0] = userConn;
        user[1] = userToSend;
        return user;
    }

    /**
     * @param idClient
     * @param writer
     * @return la position d'ajoue du client
     */
    synchronized public int addClient(String idClient, ObjectOutputStream writer) {
        nbClient++;
        listeDeClient.put(idClient, writer);
        return listeDeClient.size();
    }

    /**
     * supprime un client deconnecter
     *
     * @param idClient
     */
    synchronized public void dellClient(String idClient) {
        if (listeDeClient.containsKey(idClient)) {
            listeDeClient.remove(idClient);
            nbClient--;
        }
    }

    /**
     * permet de faire connaitre le server au differrent thread qui gere les
     * cleint
     *
     * @param client
     */
    private void indiquerServer(ClientServerCommunication client) {
        client.setServer(this);
    }

    /**
     * @param idClient
     * @param msg
     * @return
     */
    public String[] createTicket(String idClient, Msg msg) {
        String idGroup = msg.getIdGroup();
        String title = msg.getTitle();
        String idTicket = main_server.createTicket(idClient, idGroup, title, msg.getContenuDuTcket());
        String[] toSend = new String[3];
        if (!idTicket.equals("")) {
            toSend[0] = "5100"; //ici on repond en plus de ok on return l'id du nouveau ticket crréé
            toSend[1] = idTicket;
        } else {
            toSend[0] = "0000";
            toSend[1] = "ERRER_TICKET_CREAT";
        }
        return toSend;
    }

    public void close() {
        isRunning = false;
    }

    public String[] answarback(String idClient, Msg msg) {
        String idTicket = msg.getIdTicket();
        String contenue = msg.getContenue();
        String date = msg.getDateOfMsg();
        String[] toSend = new String[3];
        String[] notif = new String[3];
        Set<String> userId;
        //si l'ensemble est different de null tu rentre de dans si non erreur
        if ((userId = main_server.updateTicket(idClient, idTicket, contenue)) != null) {
            toSend[0] = "5200"; // ici on repond just par un ok
            toSend[1] = "MESSAGE_OK";
            notif[0] = "1000";
            notif[1] = "NOTIF_RECU";
            Object[] user = userConnect(userId); // recupère seulemnt ceux qui sont connecter 
            sendNotification(notif, user); //on enoie la notification à tout le monde 
        } else {//erreur null pointer exception
            toSend[0] = "0000";
            toSend[1] = "ERRER_TICKET_ANSWAR";
        }
        return toSend;
    }

    /**
     * @param id
     * @param pwd
     * @return
     */
    public boolean login(String id, String pwd) {
        return main_server.userIsExist(id, pwd);
    }

    /**
     * @param idClient
     */
    public void updateInfos(String idClient) { //on lui envoie juste la liste des groupes
        //on lui signifie que c'est bon il est connecter 
        //avec des indice dessus lui signifiant le nombre de notfication qu'il y a dans chaque
        ObjectOutputStream writer = listeDeClient.get(idClient);
        String[] toSend = main_server.getListOfGroup(idClient);
        String[] toSend2 = main_server.getListOfGroupBox(idClient);
        String[] lastFirstName = main_server.getLastAndFirstName(idClient);
        try {
            writer.writeObject(toSend);
            writer.flush();// à voir je ne connait pas trop son utilisation 
            writer.writeObject(toSend2);
            writer.flush();
            writer.writeObject(lastFirstName);
            writer.flush();
        } catch (IOException e) {
        }
    }

    /**
     * avec un thread à part on traite l'envoie des notifications au différent
     * client une fois un ticket creé ou un msg repondu
     *
     * @param notification : la notification à envoyer
     * @param user : ensemble des utilisateur connecter et leur flux de reponse
     */
    public void sendNotification(String[] notification, Object[] user) {
        Thread envoyerNotification;
        envoyerNotification = new Thread(() -> {
            List<String> userConn = (List<String>) user[0];
            List<ObjectOutputStream> writer = (List<ObjectOutputStream>) user[1];
            //main_server.modifyStatOfUserId(userConn); //met à jour la base de donnée
            for (ObjectOutputStream cl : writer) {
                try {
                    cl.writeObject(notification);
                } catch (IOException ex) {
                    Logger.getLogger(ClientsServerSocketInit.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        envoyerNotification.start();
    }

    /**
     * @param msg
     * @param idclient
     * @return
     */
    public String[] getListOfTicket(Msg msg, String idclient) {
        return main_server.getListOfTicket(idclient, msg.getIdGroup());
    }

    /**
     * @param msg
     * @return
     */
    public String[] getListOfMsg(String idClient, Msg msg) {
        // TODO Auto-generated method stub
        System.out.println("id Ticket : "+msg.getIdTicket());
        return main_server.getListOfMsg(idClient, msg.getIdTicket());
    }

    /**
     * @param msg
     * @return
     */
    public String[] getListOfGroup(Msg msg) {
        return main_server.getListOfGroup(msg.getLogin());
    }
}
