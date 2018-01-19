package main_server.dataBase;

import com.mysql.jdbc.CommunicationsException;
import java.sql.*;

/**
 * class of functions for connecting to the database
 * @author RainAlex
 */
public class Connect {

    Statement state = null;
    String port= "";
    String url = "";
    String user = "";
    String pwd = "";
    boolean connected = false;

    /**
     * @param port the port of the database
     * @param dbthe database name
     * @param user the username of the database
     * @param pwd the pwd to the database
     */
    public Connect(String port, String db, String user, String pwd){
    	this.url = "jdbc:mysql://localhost:" + port + "/" + db;
    	this.user = user;
    	this.pwd = pwd;
    }
    
    /**
     * @return true if connected to the database
     */
    public boolean isConnected(){
	   return connected;
    }
    
    /**
     * @param newStat the new status of the connection (true = connected, false = disconnected)
     */
    public void setConnected(boolean newStat){
	   connected = newStat;
    }
    
    /**
     * @return conn the Connection entity
     */
    public Connection connectToDB() throws CommunicationsException {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            Connection conn = DriverManager.getConnection(url, user, pwd);

            state = conn.createStatement();
            String sql = "SELECT * FROM INITIALIZED";
            ResultSet rs = state.executeQuery(sql);
            rs.next();
            if (rs.getInt("INIT") == 0) {
                sql = "UPDATE INITIALIZED SET INIT = 1 WHERE INIT = 0";
                state.executeUpdate(sql);
                initUsernamesSaltAndHash(conn);
            }
            connected = true;
            return conn;
	} catch (CommunicationsException ce){
	    System.err.println("Impossible de se connecter a la base de donnees avec ces identifiants");
	    ce.printStackTrace();
            return null;
	} catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    /**
     * @param conn
     * @return initializes the usernames, salts, and hash values of the users in the sql file if not already done
     */
    private void initUsernamesSaltAndHash(Connection conn) {
        try {
            SaltGen sg = new SaltGen();
            int id;
            String firstName, lastName, salt, userName, pwd, hashValue;
            state = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql = "SELECT * FROM PERSON";
            ResultSet rs = state.executeQuery(sql);
            while (rs.next()) {
                id = rs.getInt("idPerson");
                firstName = rs.getString("firstName");
                lastName = rs.getString("lastName");
                salt = sg.saltValueGen();
                userName = UsernameAndPassGen.userNameGen(lastName, firstName, id);
                pwd = UsernameAndPassGen.passWordGen(lastName, firstName, id);
                hashValue = sg.hashValueGen(pwd, salt);
                rs.updateString("userName", userName);
                rs.updateString("salt", salt);
                rs.updateString("hashValue", hashValue);
                rs.updateRow();
            }
            rs.close();
            state.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * disconnects
     * @param conn
     */
    public void disconnect(Connection conn) throws SQLException {
        conn.close();
    }
}
