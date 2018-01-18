package client.socketClient;

import user.Ticket;
import user.Message;
import user.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnexion {

    private Socket connexion = null;
    private PrintWriter writer = null;
    private ObjectInputStream reader = null;
    private boolean closeConnexion = true;
    private Thread tr = null;
    private static int count = 0;
    private String name = "Client-";

    private User user = null;
    private String[] error = null;
    private String[] notification = null;
    private String ack = "";								// été ajouté à la base de donnée

    // private List<String> group = new ArrayList<>();
    public ClientConnexion(String host, int port, User user) {
        this.user = user;
        name += ++count;
        try {
            connexion = new Socket(host, port);

            // initialisation des flux
            // writer = new ObjectOutputStream(connexion.getOutputStream());
            writer = new PrintWriter(connexion.getOutputStream());
            reader = new ObjectInputStream(connexion.getInputStream());
        } catch (UnknownHostException e) {
            System.out.println("une erreur de connection "+e.getMessage());
            
            return;
        } catch (IOException e) {
            System.out.println("une erreur de connection "+e.getMessage());
            return;
        }
        user.setSocketOk(true);
    }

    public void send(String toSend) {
        writer.write(toSend);
        writer.flush();
        System.out.println("Commande " + toSend + " envoyée au serveur");
    }

    public void receveBis() {
        try {
            boolean continuer = true;
            while (continuer) { //on attends des reponse tenqu'on est pas connecter
                Object objetRecu = reader.readObject();
                if (objetRecu != null) {
                    String[] reponse = (String[]) objetRecu;
                    int choix = reponse[0].charAt(0) - 48;
                    if (choix == 5 || choix == 0) {
                        int c = reponse[0].charAt(1) - 48;
                        if (choix == 5) {
                            System.out.println("conn ok");
                            user.setIsConnected(true);
                        }
                        continuer = false;
                    } else {
                        switch (reponse[0].charAt(1)) {
                            case '0':
                                user.listOfGroup(reponse);
                                user.afficherGroupe();
                                break;
                            case '1':
                                user.listOfGroupBox(reponse);
                                break;
                            default:
                                user.setNom(reponse[1]);
                                user.setPrenom(reponse[2]);
                                System.out.println(user.getNom() + " " + user.getPrenom());
                                break;
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ClientConnexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     */
    public void recve() {
        tr = new Thread(new Runnable() {
            public void run() {
                while (closeConnexion) {
                    try {
                        Object objetRecu = reader.readObject();
                        if (objetRecu != null) {
                            String[] reponse = (String[]) objetRecu;
                            int choix = reponse[0].charAt(0) - 48;
                            switch (choix) {
                                case 5:
                                    int c = reponse[0].charAt(1) - 48;
                                    switch (c) {
                                        case 1:
                                            user.addTicket(reponse[1], user.getTitleOfTicket(), user.getIdGroup());
                                            user.setNotifCreeTicketRecu(true);
                                            break;
                                        case 2:
                                            user.setAnswarMsg(true);
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case 4:
                                    if (reponse[0].charAt(1) == '0') {
                                        user.listOfGroup(reponse);
                                    } else if (reponse[0].charAt(1) == '2') {
                                        user.listOfGroupBox(reponse);
                                    } else {
                                        user.setGrpRecu(true);
                                    }
                                    break;
                                case 1:
                                    System.out.println("notification recu"); // ici on declanche une fonction de mis à jour
                                    notification = reponse;
                                    user.setNotifHere(true);
                                    break;
                                case 2:
                                    user.addListOfMessages(getListOfMsg(reponse));
                                    break;
                                case 3:
                                    System.out.println("is empy listOfGroup :" + user.getListOfGroup().isEmpty());
                                    user.addListOfTickets(listOfTicket(reponse));
                                    break;
                                case 0:// traitement de l'erreur et demander ici

                                    //on peux avoir une ereur de
                                    error = reponse;
                                    break;

                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                    }
                }
                try { //fermeture du flux
                    reader.close();
                    reader = null;
                } catch (IOException e) {
                }
            }
        });
        tr.start();
    }

    /**
     * @param msg
     * @return
     */
    synchronized private String getErrorMsg(String[] msg) {
        return msg[1];
    }

    /**
     *
     */
    synchronized public void close() {
        writer.write("0000/CLOSE");
        writer.close();
        writer = null;
    }

    synchronized public boolean getCLoseConnexion() {
        return closeConnexion;
    }

    synchronized public void setCloseConnecion(boolean closeConnexion) {
        this.closeConnexion = closeConnexion;
    }

    synchronized public Thread getTr() {
        return this.tr;
    }

    private Set<Ticket> listOfTicket(String[] ticket) {
        Set<Ticket> tickets = new HashSet<>();
        String[] tck;
        for (int i = 1; i < ticket.length; i++) {
            tck = ticket[i].split("/");
            tickets.add(new Ticket(tck[0], tck[1], Integer.parseInt(tck[2])));
        }
        return tickets;
    }

    /**
     * @return
     */
    private Set<Message> getListOfMsg(String[] msg) {
        Set<Message> listMsg = new HashSet<>();
        String[] mess;
        for (int i = 1; i < msg.length; i++) {
            mess = msg[i].split("/");
            String[] nomEtStatus = mess[5].split("#");
            listMsg.add(new Message(mess[0], mess[1], mess[2], mess[3], mess[4], nomEtStatus));
        }
        return listMsg;
    }

    public String getAck() {
        return ack;
    }

}
