/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package user;

import java.util.Set;
import java.util.TreeSet;
import client.socketClient.*;

/**
 *
 * @author mmadi
 */
public class User {

    private String psd;
    private boolean socketOk = false;
    private boolean isConnect = false;
    private boolean conn_here = false; //renseigne l'arrivé d'une connexion
    private boolean notifHere = false; // dis s'il y a une notification 
    private boolean ticketRecu = false;
    private boolean msgRecu = false;
    private boolean grpRecu = false;
    private boolean answarMsg = false;

    private boolean notifCreeTicketRecu = false;
    private String idGroup = "";
    private String idTicket = "";
    private String titleOfTicket = "";

    private Set<Group> listOfGroups = new TreeSet<>();
    private Set<Group> listOfGroupsBox = new TreeSet<>();
    private ClientConnexion conn = null;
    private String nom;
    private String prenom;
    //private String host;
    //private int port;
    
    public User() {
        //conn = new ClientConnexion(host, port, this); //a discuter
    }

   
    public void runConnSocket(int port, String host) {
        conn = new ClientConnexion(host, port, this);
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

    @Override
    public String toString() {
        return nom + " " + prenom;
    }

    //------------------Getter and Setter------------------------------------
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
//---------------------fonction --------------------------

    public synchronized void runRecvePassive() {
        conn.recve();
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

    public synchronized void addListOfMessages(Set<Message> listOfMsg) {
        //System.out.println("idTicket : "+idTicket+"  idGroup : "+idGroup
        //+"is null : "+listOfGroups.isEmpty());
        boolean com = false;
        for (Group g : listOfGroups) {
            com = g.compare(idGroup);
            if (com) {
                for (Ticket t : g.getGroupTicket()) {
                    System.out.println("idTicket in :" + idTicket
                            + " IdTicket to compare " + t.getIdTicket());
                    if (t.getIdTicket().equals(idTicket)) {
                        System.out.println("Le ticket : " + t.getTitle());
                        t.setListOfMsg(listOfMsg);
                        msgRecu = true;
                        return;
                    }
                }
            }
        }

        msgRecu = true;
    }

    public boolean login(String mdp, String psd) {
        this.psd = psd;
        sendRequest(createLoginMsg(mdp));
        conn.receveBis();
        System.out.println("isConnected : " + isConnect);
        return isConnect;
    }

    public synchronized void sendRequest(String request) {
        conn.send(request);
    }

    public synchronized String requestGetMsg(String idTicket, String idGroup) {
        this.idTicket = idTicket;
        this.idGroup = idGroup;
        return "5000/" + idTicket;
    }

    public synchronized String requestGetTicket(String idGroup) {
        this.idGroup = idGroup;
        return "4000/" + idGroup;
    }

    public synchronized String requestGetGroup() {
        return "6000/" + psd;
    }

    public synchronized String createMsgAnswer(String idTicket, String msg, String date, String idGroup) {
        this.idTicket = idTicket;
        this.idGroup = idGroup;
        return "3000/" + idTicket + "/" + msg + "/" + date;
    }

    public synchronized String createTicket(String idGroupe, String titleOfTicket, String contenue) {
        this.idGroup = idGroupe;
        this.titleOfTicket = titleOfTicket;
        return "2000/" + idGroupe + "/" + titleOfTicket + "/" + contenue;
    }

    private synchronized String createLoginMsg(String mdp) {
        return "1000/" + psd + "/" + mdp;
    }

    public synchronized void disconnect() {
        conn.close();
        conn.setCloseConnecion(false);
    }

    //testes
    public synchronized boolean afficherGroupe() {
        for (Group g : listOfGroups) {
            System.out.println("nom groupe : " + g.getGroupName()
                    + "     nd msg non lu : " + g.getNbUnreadMsg());
        }

        return true;
    }

    /**
     * @return
     */
    public synchronized void listOfGroup(String[] group) {
        Set<Group> listGpr = new TreeSet<>();
        String[] grp = null;
        for (int i = 1; i < group.length; i++) {
            grp = group[i].split("/");
            listOfGroups.add(new Group(grp[0], Integer.parseInt(grp[1])));
        }
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

    public synchronized void addTicket(String idTicket, String title, String idGroup) {
        for (Group g : listOfGroups) {
            if (g.getGroupName().equals(idGroup)) {
                g.getGroupTicket().add(new Ticket(idTicket, title, 0));
                break;
            }
        }

        notifCreeTicketRecu = true;
    }

    //recupere la liste des ticket et les retourne en tableau de String
    public synchronized String[] listOfTicketString() {
        String[] listOfTicket = null;
        int i = 0;
        for (Group g : listOfGroups) {
            if (g.getGroupName().equals(idGroup)) {
                listOfTicket = new String[g.getGroupTicket().size()];
                for (Ticket t : g.getGroupTicket()) {
                    if (t.getNbUnreadMsg() != 0) {
                        listOfTicket[i++] = "[" + t.getIdTicket() + "]" + t.getTitle()
                                + "(" + t.getNbUnreadMsg() + ")";
                    } else {
                        listOfTicket[i++] = "[" + t.getIdTicket() + "]" + t.getTitle();
                    }
                }
                return listOfTicket;
            }
        }
        return listOfTicket;
    }

    public synchronized String[] listOfGroupString() {
        String[] listOfGroup = new String[listOfGroups.size()];;
        int i = 0;
        for (Group g : listOfGroups) {
            if (g.getNbUnreadMsg() != 0) {
                listOfGroup[i++] = g.getGroupName() + "(" + g.getNbUnreadMsg() + ")";
            } else {
                listOfGroup[i++] = g.getGroupName();
            }
        }
        return listOfGroup;
    }

    public synchronized void listOfGroupBox(String[] groupBox) {
        Set<Group> listGpr = new TreeSet<>();
        String[] grp = null;
        for (int i = 1; i < groupBox.length; i++) {
            grp = groupBox[i].split("/");
            listOfGroupsBox.add(new Group(grp[0], 0));
        }
    }

    public synchronized void afficherLisTicket(String idGrp) {
        for (Group g : listOfGroups) {
            if (g.getGroupName().equals(idGrp)) {
                for (Ticket t : g.getGroupTicket()) {
                    System.out.println("idTicket : " + t.getIdTicket()
                            + "\ntitre ticket : " + t.getTitle() + "\nnb msg in : " + t.getNbUnreadMsg());
                }
                break;
            }
        }
    }

    public synchronized void afficherListMsg(String idGrp, String idTicket) {
        for (Group g : listOfGroups) {
            if (g.getGroupName().equals(idGrp)) {
                for (Ticket t : g.getGroupTicket()) {
                    if (t.getIdTicket().equals(idTicket)) {
                        for (Message m : t.getListOfMsg()) {
                            System.out.println(m.toString());
                        }
                        break;
                    }
                }
            }
        }
    }

    public synchronized void afficherGroupeBox() {
        for (Group g : listOfGroupsBox) {
            System.out.println("nom groupe : " + g.getGroupName()
                    + "     nd msg non lu : " + g.getNbUnreadMsg());
        }
    }

    public synchronized void addListOfTickets(Set<Ticket> listOfTicket) {
        for (Group g : listOfGroups) { //mise à jour des ticket dans le groupe
            if (g.getGroupName().equals(idGroup)) {
                g.setGroupTickets(listOfTicket);
                ticketRecu = true;
                //ui.loadTickets(new DefaultMutableTreeNode(g));
                System.out.println("is null ticket : " + g.getGroupTicket().isEmpty());
                System.out.println(g.getGroupTicket());
                break;
            }
        }
    }

}
