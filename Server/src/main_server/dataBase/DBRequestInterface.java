package main_server.dataBase;

import java.util.*;

public interface DBRequestInterface {
	public ArrayList<String> getGroups(String userName);
	public ArrayList<String> getGroupTickets(String userName, String groupName);
	public ArrayList<String> getTicketMessages(int idTicket);
}
