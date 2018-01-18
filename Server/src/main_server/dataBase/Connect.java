package main_server.dataBase;

import java.sql.*;

public class Connect {

    Statement state = null;
    String portDB="3306";
    public Connection connectToDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            String url = "jdbc:mysql://localhost:"+portDB+"/neocampus";
            String user = "root";
            String pwd = "Angelodelagx97";

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
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

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

    public void disconnect(Connection conn) throws SQLException {
        conn.close();
    }
}
