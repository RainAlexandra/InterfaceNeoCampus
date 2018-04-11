package main_server;

import com.mysql.jdbc.CommunicationsException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import main_server.dataBase.*;

public class Server {

    private Connection conn = null;
    private Connect connect = null;
    private RequestDB reqDB = null;
    private UpdateDB updDB = null;

    /**
     * @param port the port of the database
     * @param dbthe database name
     * @param user the username of the database
     * @param pwd the pwd to the database
     */
    public Server(String port, String db, String user, String pwd) {
        connect = new Connect(port, db, user, pwd);
	try {
	    this.conn = connect.connectToDB();
	} catch (CommunicationsException ce){
	    connect.setConnected(false);
	}
        this.reqDB = new RequestDB(conn);
        this.updDB = new UpdateDB(conn);
    }

    public Connect getConnect(){
	   return connect;
    }
    
    /**
     * @param idTicket
     * @param contenue
     * @return the set of the members of the directed group of the message
     */
    public Set<String> updateTicket(String idClient, String idTicket, String contenue) {
        return updDB.addMsg(idClient, Integer.parseInt(idTicket), contenue);
    }

    /**
     * @param id
     * @param pwd
     * @return true if the user exists
     */
    public boolean userIsExist(String id, String pwd) {
        boolean connect = reqDB.userExists(id, pwd);
        System.out.println("connexion : " + connect);
        return connect;
    }

    /**
     * @param idClient
     * @return the list of groups concerning a user
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
     * @return the list of tickets concerning a group depending on the user that is logged in
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
     * @param idClient
     * @param idTicket
     * @return the list of messages along with the statuses of each user concerned by the message
     */
    public String[] getListOfMsg(String idClient, String idTicket) {
        ArrayList<String> listOfMsg = reqDB.getTicketMessages(Integer.parseInt(idTicket));
        updDB.setTicketMsgsToRead(idClient, Integer.parseInt(idTicket));
        updDB.updateTicketMsgsColors(Integer.parseInt(idTicket));
        String[] msg = new String[listOfMsg.size() + 1];
        msg[0] = "2000";
        int i = 1;
        for (String t : listOfMsg) {
            msg[i++] = t;
        }
        return msg;
    }

    /**
     * @param userName
     * @param idGroup
     * @param title
     * @param contenu
     * @return the id of the ticket in string form
     */
    public String createTicket(String userName, String idGroup, String title, String contenu) {
        int idTicket;
        idTicket = updDB.addTicket(title, userName, idGroup);
        updDB.addMsg(userName, idTicket, contenu);
        return idTicket + "";
    }

    public void modifyStatOfUserId(List<String> userConn) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @param idClient
     * @return the array of groups a user can send to
     */
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

    /**
     * @param idClient
     * @return the last and first name of a user in array form
     */
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
