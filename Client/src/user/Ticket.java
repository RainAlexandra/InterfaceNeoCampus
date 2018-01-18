package user;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Ticket {

    private final String idTicket;
    private final String title;
    private int nbUnreadMsg;

    public void setNbUnreadMsg(int nbUnreadMsg) {
        this.nbUnreadMsg = nbUnreadMsg;
    }

    private Set<Message> listOfMsg = new HashSet<>();

    public Ticket(String idTicket, String title, int nbMsgNonLu) {
        this.idTicket = idTicket;
        this.nbUnreadMsg = nbMsgNonLu;
        this.title = title;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.idTicket);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ticket other = (Ticket) obj;
        if (!Objects.equals(this.idTicket, other.idTicket)) {
            return false;
        }
        return true;
    }
    
    public boolean commparerIdTicket(String idTicket) {
        return this.idTicket.equals(idTicket);
    }

 

    @Override
    public String toString() {
        if (nbUnreadMsg != 0) {
            return "(id:" + idTicket + ")" + title + "(" + nbUnreadMsg + ')';
        } else {
            return "(id:" + idTicket + ")" + title;
        }
    }

    public void setListOfMsg(Set<Message> listOfMsg) {
        this.listOfMsg = listOfMsg;
    }

    public Set<Message> getListOfMsg() {
        return listOfMsg;
    }

    public String getIdTicket() {
        return idTicket;
    }

    public String getTitle() {
        return title;
    }

    public int getNbUnreadMsg() {
        return nbUnreadMsg;
    }

}
