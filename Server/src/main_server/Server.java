package main_server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import main_server.dataBase.*;

public class Server {

    private Connection conn = null;
    private RequestDB reqDB = null;
    private UpdateDB updDB = null;

    public Server() {
        Connect connect = new Connect();
        this.conn = connect.connectToDB();
        this.reqDB = new RequestDB(conn);
        this.updDB = new UpdateDB(conn);
    }

    /**
     * @param idTicket
     * @param contenue
     * @return
     */
    public Set<String> updateTicket(String idClient, String idTicket, String contenue) {
        return updDB.addMsg(idClient, Integer.parseInt(idTicket), contenue);
    }

    /**
     * @param id
     * @param pwd
     * @return
     */
    public boolean userIsExist(String id, String pwd) {
        boolean connect = reqDB.userExists(id, pwd);
        System.out.println("connexion : " + connect);
        return connect;
    }

    /**
     * @param idClient
     * @return
     */
    public String[] getListOfGroup(String idClient) {
        List<String> groupList = reqDB.getGroups(idClient);
        System.out.println(groupList);
        String[] groups = new String[groupList.size() + 1];
        groups[0] = "4000";
        int i = 1;
        for (String g : groupList) {
            groups[i++] = g;
        }
        return groups;
    }

    /**
     * @param idGroup
     * @param idClient
     * @return
     */
    public String[] getListOfTicket(String idClient, String idGroup) {
        System.out.println("idClient : " + idClient + "  idGroup : " + idGroup);
        ArrayList<String> listOfTickets = reqDB.getGroupTickets(idClient, idGroup);
        String[] tickets = new String[listOfTickets.size() + 1];
        tickets[0] = "3000";
        int i = 1;
        for (String t : listOfTickets) {
            tickets[i++] = t;
        }
        return tickets;
    }

    /**
     * @param idTicket
     * @return
     */
    public String[] getListOfMsg(String idClient, String idTicket) {
        ArrayList<String> listOfMsg = reqDB.getTicketMessages(Integer.parseInt(idTicket));
        updDB.setTicketMsgsToRead(idClient, Integer.parseInt(idTicket));
        updDB.updateTicketMsgsColors(Integer.parseInt(idTicket));
        String[] msg = new String[listOfMsg.size() + 1];
        msg[0] = "2000";
        int i = 1;
        for (String t : listOfMsg) {
            System.out.println("msg : " +t);
            msg[i++] = t;
        }
        return msg;
    }

    public String createTicket(String userName, String idGroup, String title, String contenu) {
        int idTicket;
        idTicket = updDB.addTicket(title, userName, idGroup);
        updDB.addMsg(userName, idTicket, contenu);
        return idTicket + "";
    }

    public void modifyStatOfUserId(List<String> userConn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String[] getListOfGroupBox(String idClient) {
        Set<String> grpB = reqDB.getPossDestGroups(idClient);
        String[] groupBox = new String[grpB.size() + 1];
        groupBox[0] = "4100";
        int i = 1;
        for (String s : grpB) {
            groupBox[i++] = s;
        }
        return groupBox;
    }

    public String[] getLastAndFirstName(String idClient) {
        String lastAndFirst = reqDB.getUserLastFirstName(idClient);
        String[] parts = lastAndFirst.split("/");
        String[] lastNameAndFirstName = new String[parts.length + 1];
        lastNameAndFirstName[0] = "4200";
        lastNameAndFirstName[1] = parts[0];
        lastNameAndFirstName[2] = parts[1];
        return lastNameAndFirstName;
    }

}
