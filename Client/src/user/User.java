/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import InterfaceUser.UserInterface;
import java.util.Set;
import java.util.TreeSet;
import client.socketClient.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

/**
 *
 * @author mmadi
 */
public class User {

    //ensembles des notification 
    private boolean socketOk = false;
    private boolean isConnect = false;
    private boolean conn_here = false; //renseigne l'arrivé d'une connexion
    private boolean notifHere = false; // dis s'il y a une notification 
    private boolean ticketRecu = false;
    private boolean msgRecu = false;
    private boolean grpRecu = false;
    private boolean answarMsg = false;
    private boolean notifCreeTicketRecu = false;

    //information du user
    private String psd; // 
    private String nom;
    private String prenom;

    private String idGroup = ""; //l'id du group selectionné
    private String idTicket = ""; //id du ticket selectionné
    private String titleOfTicket = "";
    private String idGroupNotif = "";
    private String idTicketNotif = "";

    //des ensemble contenat les groupe les tickets et les message en fonction d'une selection 
    private Set<Group> listOfGroups = new TreeSet<>();
    private Set<Group> listOfGroupsBox = new TreeSet<>();
    private Set<Ticket> listOfTickets = new HashSet<>();
    private Set<Message> listOfMsg = new HashSet<>();

    private ClientConnexion conn = null;

    private Semaphore sem;
    private UserInterface ui;
    
    public User(UserInterface ui) {
        sem = new Semaphore(1, true);
        this.ui = ui;
    }

    public void runConnSocket(int port, String host) {
        conn = new ClientConnexion(host, port, this);
    }

    @Override
    public String toString() {
        return nom + " " + prenom;
    }

    //------------------Getter and Setter------------------------------------
    private String getWriteDate() {
        long time = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(time);
        String date = timestamp.toString();
        String[] noMilli = date.split("\\.");
        return noMilli[0];
    }
    
    public void setIdGroupNotif(String grpN){
        this.idGroupNotif = grpN;
    }
    
    public void setIdTicketNotif(String idTicketNotif) {
        this.idTicketNotif = idTicketNotif;
    }
    
    public synchronized boolean isGrpRecu() {
        return grpRecu;
    }

    public synchronized void setGrpRecu(boolean grpRecu) {
        this.grpRecu = grpRecu;
    }

    public boolean isSocketOk() {
        return socketOk;
    }

    public void setSocketOk(boolean sockOk) {
        this.socketOk = sockOk;
    }

    public boolean isIsConnect() {
        return isConnect;
    }

    public boolean isNotifCreeTicketRecu() {
        return notifCreeTicketRecu;
    }

    public Set<Group> getListOfGroups() {
        return listOfGroups;
    }

    public Set<Group> getListOfGroupsBox() {
        return listOfGroupsBox;
    }

    public synchronized void setNotifCreeTicketRecu(boolean recu) {
        this.notifCreeTicketRecu = recu;
    }

    public synchronized boolean getNotifCreeTicketRecu() {
        return this.notifCreeTicketRecu;
    }

    public synchronized void setMsgRecu(boolean recu) {
        this.msgRecu = recu;
    }

    public synchronized boolean isMsgRecu() {
        return this.msgRecu;
    }

    public synchronized void setTicketRecu(boolean recu) {
        this.ticketRecu = recu;
    }

    public synchronized boolean isTicketRecu() {
        return this.ticketRecu;
    }

    public synchronized void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public synchronized void setNom(String nom) {
        this.nom = nom;
    }

    public synchronized String getNom() {
        return this.nom;
    }

    public synchronized String getPrenom() {
        return this.prenom;
    }

    public synchronized Set<Group> getListOfGroup() {
        return listOfGroups;
    }

    public synchronized void setConnHere(boolean conn) {
        this.conn_here = conn;
    }

    /**
     *
     * @return
     */
    public synchronized boolean isConnHere() {
        return conn_here;
    }

    public synchronized void setNotifHere(boolean notif) {
        this.notifHere = notif;
    }

    public synchronized boolean isNotifHere() {
        return notifHere;
    }

    public synchronized boolean isConnected() {
        return this.isConnect;
    }

    public synchronized void setIsConnected(boolean conn) {
        this.isConnect = conn;
    }

    public synchronized void setIdTicket(String idTicket) {
        this.idTicket = idTicket;
    }

    public synchronized String getIdTicket() {
        return idTicket;
    }

    public synchronized void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }

    public synchronized String getIdGroup() {
        return idGroup;
    }

    public synchronized void setTitleOfTicket(String title) {
        this.titleOfTicket = title;
    }

    public synchronized String getTitleOfTicket() {
        return titleOfTicket;
    }

    public synchronized boolean isAnswarMsg() {
        return this.answarMsg;
    }

    public synchronized void setAnswarMsg(boolean b) {
        this.answarMsg = b;
    }

//---------------------fonction --------------------------
    public void runRecvePassive() {
        conn.recve();
    }

    public synchronized void addListOfMessages(Set<Message> listOfMsg) {
        this.listOfMsg = listOfMsg;
        msgRecu = true;
    }

    // fonction d'envoie du login et de reception de reponse de connexion 
    public boolean login(String mdp, String psd) {
        this.psd = psd;
        sendRequest(createLoginMsg(mdp));
        conn.receveBis(); // à la sortie de cette fonction on sait si on est connecté ou pas 
        return isConnect;
    }

    //testes
    public void afficherGroupe() {
        listOfGroups.forEach((g) -> {
            System.out.println("nom groupe : " + g.getGroupName()
                    + "     nd msg non lu : " + g.getNbUnreadMsg());
        });
    }

    /**
     * @param group
     */
    public void listOfGroup(String[] group) {
        String[] grp;
        for (int i = 1; i < group.length; i++) {
            grp = group[i].split("/");
            listOfGroups.add(new Group(grp[0], Integer.parseInt(grp[1])));
        }
        grpRecu = true;
    }

    public synchronized void listOfGroupBox(String[] groupBox) {
        String[] grp;
        for (int i = 1; i < groupBox.length; i++) {
            grp = groupBox[i].split("/");
            listOfGroupsBox.add(new Group(grp[0], 0));
        }
    }
    // ensemble des requets et commende d'envoie 
    //--------------------------------------------------------------------------

    //utiliser pour envoyer une requete
    public void sendRequest(String request) {
        conn.send(request);
    }

    public String requestGetMsg(String idTicket, String idGroup) {
        this.idTicket = idTicket;
        this.idGroup = idGroup;
        return "5000/" + idTicket;
    }

    public String requestGetTicket(String idGroup) {
        this.idGroup = idGroup;
        return "4000/" + idGroup;
    }

    public String requestGetGroup() {
        return "6000/" + psd;
    }

    public String createMsgAnswer(String idTicket, String msg, String idGroup) {
        String date;
        date = getWriteDate();
        this.idTicket = idTicket;
        this.idGroup = idGroup;
        return "3000/" + idTicket + "/" + msg + "/" + date;
    }

    public String createTicket(String idGroupe, String titleOfTicket, String contenue) {
        this.idGroup = idGroupe;
        this.titleOfTicket = titleOfTicket;
        return "2000/" + idGroupe + "/" + titleOfTicket + "/" + contenue;
    }

    private String createLoginMsg(String mdp) {
        return "1000/" + psd + "/" + mdp;
    }

    public void disconnect() {
        conn.close();
        conn.setCloseConnecion(false);
    }
//------------------------------------------------------------------------------

    public synchronized void addTicket(String idTicket, String title, String idGroup) {
        listOfTickets.add(new Ticket(idTicket, title, 0));
        notifCreeTicketRecu = true;
    }

    public synchronized void afficherLisTicket() {
        listOfTickets.forEach((t) -> {
            System.out.println("idTicket : " + t.getIdTicket()
                    + "\ntitre ticket : " + t.getTitle() + "\nnb msg in : " + t.getNbUnreadMsg());
        });
    }

    public synchronized void afficherListMsg() {
        listOfMsg.forEach((m) -> {
            System.out.println(m.toString());
        });
    }

    public synchronized void afficherGroupeBox() {
        listOfGroupsBox.forEach((g) -> {
            System.out.println("nom groupe : " + g.getGroupName()
                    + "     nd msg non lu : " + g.getNbUnreadMsg());
        });
    }

    public synchronized void addListOfTickets(Set<Ticket> listOfTicket) {
        this.listOfTickets = listOfTicket;
        ticketRecu = true;
    }
    
    
    //cette fonction est applée que si le user est connecter
    public void updateNotification() {
        Thread update = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isConnect) { 
                    if (notifHere) {
                        if(idGroup.compareTo(idGroupNotif) == 0) {
                            if(idTicket.compareTo(idTicketNotif) == 0) { //je met à jour les mssage
                                //demande de mis à jour des message
                                sendRequest(requestGetMsg(idTicket, idGroup));
                            }else { //je demande les tickets 
                                sendRequest(requestGetTicket(idGroup));
                            }
                        }else {
                            sendRequest(requestGetGroup());
                        }
                    }
                }
            }
        });
        
        update.start();
    }
}
