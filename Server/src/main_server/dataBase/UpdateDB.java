/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main_server.dataBase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mmadi
 */
public class UpdateDB {

    Statement state = null;
    Connection conn = null;

    public UpdateDB(Connection conn) {
        this.conn = conn;
    }

    public int getUserIDNumber(String userName) {
        try {
            int idPerson;
            state = conn.createStatement();
            String sql = "SELECT idPerson FROM PERSON WHERE userName = \"" + userName + "\"";
            ResultSet rs = state.executeQuery(sql);
            rs.beforeFirst();
            rs.next();
            idPerson = rs.getInt("idPerson");
            rs.close();
            state.close();
            return idPerson;
        } catch (Exception e) {
            System.err.println("Unable to retrieve " + userName + "'s ID");
            e.printStackTrace();
            return -1;
        }
    }

    public int addTicket(String title, String authorUserName, String groupName) {
        int idTicket = -1;
        try {
            int idAuthor;
            String sql;
            ResultSet rs;
            idAuthor = getUserIDNumber(authorUserName);
            sql = "INSERT INTO TICKET (title, nameGrp, idPerson) VALUES (\"" + title + "\", \"" + groupName + "\", " + idAuthor + ");";
            state = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            state.executeUpdate(sql);
            sql = "SELECT idTicket FROM TICKET WHERE title = \"" + title + "\" AND nameGrp = \"" + groupName + "\" AND idPerson = " + idAuthor + " ORDER BY idTicket DESC;";
            rs = state.executeQuery(sql);
            rs.beforeFirst();
            rs.next();
            idTicket = rs.getInt("idTicket");
            rs.close();
            state.close();
        } catch (Exception e) {
            System.err.println("Unable to add ticket");
            e.printStackTrace();
        }
        return idTicket;
    }

    public Set<String> addMsg(String authorUserName, int idTicket, String text){
    Set<String> membersOfGroup = new HashSet<String>();
    try {
      int idMsg; int idAuthor; int idMember; String writeDate; String memberUserName; String sql; ResultSet rs;
      state = conn.createStatement();
      idAuthor = getUserIDNumber(authorUserName);
      state = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
      writeDate = getWriteDate();
      sql = "INSERT INTO MESSAGE (msgText, color, idTicket, idPerson, writeDate) VALUES (\"" + text + "\", 'RED', " + idTicket + ", " + idAuthor + ", \"" + writeDate + "\");";
      state.executeUpdate(sql);
      sql = "UPDATE TICKET SET creationDate = \"" + writeDate + "\" WHERE idTicket = " + idTicket + ";";
      state.executeUpdate(sql);
      sql = "SELECT idMsg FROM MESSAGE WHERE idTicket = " + idTicket + " AND idPerson = " + idAuthor + " AND writeDate = \"" + writeDate + "\";";
      rs = state.executeQuery(sql);
      rs.beforeFirst(); rs.next();
      idMsg = rs.getInt("idMsg");
      sql = "SELECT M.idPerson, P.userName FROM MEMBER_OF M, PERSON P, TICKET T WHERE M.nameGrp = T.nameGrp AND T.idTicket = " + idTicket + " AND P.idPerson = M.idPerson;";
      rs = state.executeQuery(sql);
      rs.beforeFirst();
      while (rs.next()){
        memberUserName = rs.getString("P.userName");
        idMember = rs.getInt("M.idPerson");
        state = conn.createStatement();
        sql = "INSERT INTO RECEIVE VALUES (" + idMember + ", " + idMsg + ", 'RECU')";
        state.executeUpdate(sql);
        membersOfGroup.add(memberUserName);
      }
      rs.close();
      state.close();
    } catch (Exception e){
      System.err.println("Error adding message to database");
      e.printStackTrace();
    }
    return membersOfGroup;
  }

    private String getWriteDate() {
        long time = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(time);
        String date = timestamp.toString();
        String[] noMilli = date.split("\\.");
        return noMilli[0];
    }

    
    // updates the colors of all the messages of a ticket
  public void updateTicketMsgsColors(int idTicket){
    try {
      int idMsg; String sql; ResultSet rs;
      sql = "SELECT M.idMsg FROM MESSAGE M, TICKET T WHERE T.idTicket = " + idTicket + " AND M.idTicket = T.idTicket;";
      state = conn.createStatement();
      rs = state.executeQuery(sql);
      rs.beforeFirst();
      while (rs.next()){
        idMsg = rs.getInt("M.idMsg");
        updateMsgColor(idMsg);
      }
      rs.close();
      state.close();
    } catch (Exception e){
      System.err.println("Unable to edit message colors of ticket " + idTicket);
      e.printStackTrace();
    }
  }
  
  
  // updates the color of a given message
  private void updateMsgColor(int idMsg){
    try {
      int nbLus; int nbMsg; String sql; ResultSet rs;
      sql = "SELECT COUNT(*) FROM RECEIVE WHERE idMsg = " + idMsg + " AND status = 'LU';";
      state = conn.createStatement();
      rs = state.executeQuery(sql);
      rs.beforeFirst(); rs.next();
      nbLus = ((Number) rs.getObject(1)).intValue();
      sql = "SELECT COUNT(*) FROM RECEIVE WHERE idMsg = " + idMsg + ";";
      rs = state.executeQuery(sql);
      rs.beforeFirst(); rs.next();
      nbMsg = ((Number) rs.getObject(1)).intValue();
      if (nbLus != 0){
        if (nbMsg == nbLus){
          sql = "UPDATE MESSAGE SET color = 'GREEN' WHERE idMsg = " + idMsg + ";";
        } else if (nbLus > 0){
          sql = "UPDATE MESSAGE SET color = 'ORANGE' WHERE idMsg = " + idMsg + ";";
        }
        state.executeUpdate(sql);
      }
      rs.close();
      state.close();
    } catch (Exception e){
      System.err.println("Unable to update message color " + idMsg);
      e.printStackTrace();
    }
  }
  
  // when ticket is clicked all concerned messages become 'lu' (read)
  public void setTicketMsgsToRead(String userName, int idTicket){
    try {
      String sql; ResultSet rs; int idMsg; List<Integer> listIdMsg = new ArrayList<Integer>();
      int idPerson = getUserIDNumber(userName);
      state = conn.createStatement();
      sql = "SELECT idMsg FROM MESSAGE WHERE idTicket = " + idTicket + ";";
      rs = state.executeQuery(sql);
      rs.beforeFirst();
      while (rs.next()){
        idMsg = rs.getInt("idMsg");
        listIdMsg.add(idMsg);
      }
      for (Integer msg : listIdMsg){
        sql = "UPDATE RECEIVE SET status = 'LU' WHERE idPerson = " + idPerson + " AND idMsg = "+ msg + ";";
        state.executeUpdate(sql);
      }
      rs.close();
      state.close();
    } catch (Exception e){
      System.err.println("Unable to change message status to READ");
      e.printStackTrace();
    }
  }
}
