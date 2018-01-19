package main_server.dataBase;

import java.sql.*;
import java.util.*;

/**
 * 
 * @author RainAlex
 */
public class RequestDB {

	Statement state = null;
	Connection conn = null;

	public RequestDB(Connection conn) {
		this.conn = conn;
	}

	/**
	 * @param userName the userName of the user who's groups are to be returned
	 * @return the groupList that the user is a member of
	 */
	public ArrayList<String> getGroups(String userName) {
		ArrayList<String> groupList = new ArrayList<String>();
		try {
			String sql;
			ResultSet rs;
			String nameGrp = "";
			int idPerson;
			state = conn.createStatement();
			sql = "SELECT idPerson FROM PERSON WHERE userName = \"" + userName + "\"";
			rs = state.executeQuery(sql);
			rs.beforeFirst();
			rs.next();
			idPerson = rs.getInt("idPerson");
			state.close();
			state = conn.createStatement();
			sql = "SELECT nameGrp FROM MEMBER_OF WHERE idPerson = " + idPerson + " UNION SELECT nameGrp FROM TICKET WHERE idPerson = " + idPerson + ";";
			rs = state.executeQuery(sql);
			rs.beforeFirst();
			while (rs.next()) {
				nameGrp = rs.getString("nameGrp");
				groupList.add(nameGrp);
			}
			for (int i = 0; i < groupList.size(); i++) {
				String groupCur = groupList.get(i);
				groupCur += "/" + countUnreadForGroup(idPerson, groupCur) + "";
				groupList.set(i, groupCur);
			}
			rs.close();
			state.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error returning groups");
			return null;
		}
		return groupList;
	}

	/**
	 * @param idPerson the id of the person logged in
	 * @param groupName the group to whose unread messages are to be counted
	 * @return the number of unread messages concerning a group
	 */
	private int countUnreadForGroup(int idPerson, String groupName) {
		try {
			state = conn.createStatement();
			String sql = "SELECT COUNT(*) FROM MESSAGE M, RECEIVE R, TICKET T, GROUPE G WHERE R.idPerson = " + idPerson + " AND G.nameGrp = \"" + groupName + "\" AND M.idMsg = R.idMsg AND T.idTicket = M.idTicket AND G.nameGrp = T.nameGrp AND R.status = 'RECU';";
			ResultSet rs = state.executeQuery(sql);
			rs.next();
			return ((Number) rs.getObject(1)).intValue();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to count unread messages");
			return 00;
		}
	}

	/**
	 * @param userName the userName of the user who's groups are to be returned
	 * @param groupName the group whose tickets are to be returned
	 * @return the ticketList of tickets directed to a group
	 */
	public ArrayList<String> getGroupTickets(String userName, String groupName) {
		ArrayList<String> ticketList = new ArrayList<String>();
		try {
			String sql;
			ResultSet rs;
			int ticketID;
			String ticketTitle = "", ticketInfo = "";
			int idPerson = getUserIDNumber(userName);
			state = conn.createStatement();
			sql = "SELECT T.idTicket, T.title, T.creationDate FROM PERSON P, TICKET T, GROUPE G WHERE P.idPerson = " + idPerson + " AND G.nameGrp = \"" + groupName + "\" AND G.nameGrp = T.nameGrp ORDER BY creationDate ASC;";
			rs = state.executeQuery(sql);
			rs.beforeFirst();
			while (rs.next()) {
				ticketID = rs.getInt("T.idTicket");
				ticketTitle = rs.getString("T.title");
				ticketInfo = ticketID + "/" + ticketTitle;
				ticketList.add(ticketInfo);
			}
			for (int i = 0; i < ticketList.size(); i++) {
				String ticketArr[] = ticketList.get(i).split("/");
				String ticketCur = ticketList.get(i);
				ticketCur += "/" + countUnreadForTicket(idPerson, Integer.parseInt(ticketArr[0])) + "";
				ticketList.set(i, ticketCur);
			}
			rs.close();
			state.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error returning groups");
			return null;
		}
		return ticketList;
	}

	/**
	 * @param idPerson the id of the person logged in
	 * @param idTicket the ticket whose unread messages are to be counted
	 * @return the number of unread messages concerning a ticket
	 */
	private int countUnreadForTicket(int idPerson, int idTicket) {
		try {
			state = conn.createStatement();
			String sql = "SELECT COUNT(*) FROM MESSAGE M, RECEIVE R, TICKET T, GROUPE G WHERE R.idPerson = " + idPerson + " AND T.idTicket = " + idTicket + " AND M.idMsg = R.idMsg AND T.idTicket = M.idTicket AND G.nameGrp = T.nameGrp AND R.status = 'RECU';";
			ResultSet rs = state.executeQuery(sql);
			rs.next();
			return ((Number) rs.getObject(1)).intValue();
		} catch (Exception e) {
			System.err.println("Unable to count unread messages");
			return 00;
		}
	}

	/**
	 * @param userName the user name to be checked
	 * @param pwd the password to be checked
	 * @return true if the user exists in the database
	 */
	public boolean userExists(String userName, String pwd) {
		SaltGen sg = new SaltGen();
		boolean userExists = false;
		try {
			String sql;
			ResultSet rs;
			String salt, hashValue;
			state = conn.createStatement();
			if (countSaltValues(userName) == 1) {
				sql = "SELECT salt FROM PERSON WHERE userName = \"" + userName + "\"";
				rs = state.executeQuery(sql);
				rs.beforeFirst();
				rs.next();
				salt = rs.getString("salt");
				if (countHashValues(salt) == 1) {
					state.close();
					state = conn.createStatement();
					sql = "SELECT hashValue FROM PERSON WHERE salt = \"" + salt + "\"";
					rs = state.executeQuery(sql);
					rs.beforeFirst();
					rs.next();
					hashValue = rs.getString("hashValue");
					userExists = sg.verifPwd(pwd, salt, hashValue);
				}
				rs.close();
			}
			state.close();
		} catch (Exception e) {
			System.out.println("Unable to verify user existance");
		}
		return userExists;
	}

	/**
	 * @param userName the user name to be checked
	 * @return the number of salt values associated with the userName (should equal 1)
	 */
	private int countSaltValues(String userName) {
		try {
			state = conn.createStatement();
			String sql = "SELECT COUNT(salt) FROM PERSON WHERE userName = \"" + userName + "\"";
			ResultSet rs = state.executeQuery(sql);
			rs.next();
			return ((Number) rs.getObject(1)).intValue();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to count unread messages");
			return -1;
		}
	}

	/**
	 * @param userName the user name to be checked
	 * @return the number of hash values associated with the userName (should equal 1)
	 */
	private int countHashValues(String salt) {
		try {
			state = conn.createStatement();
			String sql = "SELECT COUNT(hashValue) FROM PERSON WHERE salt = \"" + salt + "\"";
			ResultSet rs = state.executeQuery(sql);
			rs.next();
			return ((Number) rs.getObject(1)).intValue();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to count unread messages");
			return -1;
		}
	}

	/**
	 * @param idTicket the id of the ticket whose messages are to be viewed
	 * @return the messageList list of messages of a gicven ticket
	 */
	public ArrayList<String> getTicketMessages(int idTicket) {
		ArrayList<String> messageList = new ArrayList<String>();
		try {
			String sql;
			ResultSet rs;
			String lastName = "", firstName = "", msgText = "", color = "", writeDate = "";
			String msgAuthor = "";
			int idMsg;
			state = conn.createStatement();
			sql = "SELECT P.lastName, P.firstName, M.msgText, M.color, M.writeDate, M.idMsg FROM PERSON P, MESSAGE M, TICKET T WHERE T.idTicket = " + idTicket + " AND T.idTicket = M.idTicket AND P.idPerson = M.idPerson ORDER BY writeDate ASC";
			rs = state.executeQuery(sql);
			rs.beforeFirst();
			while (rs.next()) {
				lastName = rs.getString("P.lastName");
				firstName = rs.getString("P.firstName");
				msgText = rs.getString("M.msgText");
				color = rs.getString("M.color");
				writeDate = rs.getString("M.writeDate");
				idMsg = rs.getInt("M.idMsg");
				msgAuthor = lastName + "/" + firstName + "/" + msgText + "/" + color + "/" + writeDate + "/" + getStatuses(idMsg);
				messageList.add(msgAuthor);
			}
			rs.close();
			state.close();
		} catch (Exception e) {
			System.err.println("Unable to get ticket messages");
			return null;
		}
		return messageList;
	}

	/**
	 * @param idMessage
	 * @return the statusList the list of status of each user on the receiving end of a given message
	 */
	private String getStatuses(int idMessage) {
		String statusList = "";
		try {
			String sql;
			ResultSet rs;
			state = conn.createStatement();
			sql = "SELECT P.lastName, P.firstName, R.status FROM PERSON P, MESSAGE M, RECEIVE R WHERE M.idMsg = " + idMessage + " AND R.idPerson = P.idPerson AND R.idMsg = M.idMsg ORDER BY P.lastName ASC;";
			rs = state.executeQuery(sql);
			rs.beforeFirst();
			while (rs.next()) {
				statusList += rs.getString("P.lastName") + "_" + rs.getString("P.firstName") + "_" + rs.getString("R.status") + "#";
			}
			rs.close();
			state.close();
		} catch (Exception e) {
			System.err.println("Error getting statuses");
		}
		return statusList;
	}

	/**
	 * @param userName the userName of the user whose id is to be returbed
	 * @return the groupList that the user is a member of
	 */
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

	public String getUserLastFirstName(String userName) {
		try {
			String name = "";
			state = conn.createStatement();
			String sql = "SELECT lastName, firstName FROM PERSON WHERE userName = \"" + userName + "\"";
			ResultSet rs = state.executeQuery(sql);
			rs.beforeFirst();
			rs.next();
			name += rs.getString("lastName") + "/";
			name += rs.getString("firstName");
			state.close();
			return name;
		} catch (Exception e) {
			System.err.println("Unable to retrieve " + userName + "'s ID");
			e.printStackTrace();
			return "ERR";
		}
	}

	public Set<String> getPossDestGroups(String userName) {
		Set<String> destGroups = new TreeSet<String>();
		try {
			String sql;
			ResultSet rs;
			String nameGrp = "";
			String userType = getUserType(userName);
			state = conn.createStatement();
			sql = "SELECT nameGrp FROM GROUPE WHERE type <> \'" + userType + "\';";
			rs = state.executeQuery(sql);
			rs.beforeFirst();
			while (rs.next()) {
				nameGrp = rs.getString("nameGrp");
				if (countGroupMembers(nameGrp) != 0) {
					destGroups.add(nameGrp);
				}
			}
			rs.close();
			state.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to retrieve possible destination groups");
		}
		return destGroups;
	}

	private int countGroupMembers(String groupName) {
		try {
			state = conn.createStatement();
			String sql = "SELECT COUNT(*) FROM MEMBER_OF M, GROUPE G WHERE G.nameGrp = \"" + groupName + "\" AND M.nameGrp = G.nameGrp;";
			ResultSet rs = state.executeQuery(sql);
			rs.next();
			return ((Number) rs.getObject(1)).intValue();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unable to count group members");
			return -1;
		}
	}

	public String getUserType(String userName) {
		try {
			String userType;
			state = conn.createStatement();
			String sql = "SELECT type FROM PERSON WHERE userName = \"" + userName + "\";";
			ResultSet rs = state.executeQuery(sql);
			rs.beforeFirst();
			rs.next();
			userType = rs.getString("type");
			state.close();
			return userType;
		} catch (Exception e) {
			System.err.println("Unable to retrieve " + userName + "'s type");
			e.printStackTrace();
			return "null";
		}
	}
	// public String[] getTicketMessages(int idTicket){
	// 	// int i = 0;
	// 	// String[] msgList;
	// 	// try {
	// 	// 	state = conn.createStatement();
	// 	// 	String sql = "SELECT * FROM TICKET T, MESSAGE M, RECEIVE R"
	// 	// }
	// }
}
