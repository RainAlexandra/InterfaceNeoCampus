package user;

import java.util.Set;
import java.util.HashSet;
import java.util.Objects;

public class Group implements Comparable<Group> {

    private int nbUnreadMsg;

    public void setNbUnreadMsg(int nbUnreadMsg) {
        this.nbUnreadMsg = nbUnreadMsg;
    }
    private String groupName;

    private Set<Ticket> groupTickets = new HashSet<>();

    public Group(String groupName, int nbMsgNonLu) {
        this.groupName = groupName;
        this.nbUnreadMsg = nbMsgNonLu;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public int getNbUnreadMsg() {
        return this.nbUnreadMsg;
    }

    public boolean compare(String g) {
        return g.equals(groupName);
    }

    /**
     *
     * @param obj
     * @return whether or not two groups have the same title
     */
    @Override
    public int compareTo(Group o) {
        return this.groupName.compareTo(o.groupName);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.groupName);
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
        final Group other = (Group) obj;
        if (!Objects.equals(this.groupName, other.groupName)) {
            return false;
        }
        return true;
    }

    

    public Set<Ticket> getGroupTicket() {
        return this.groupTickets;
    }

    public void setGroupTickets(Set<Ticket> grpTicket) {
        this.groupTickets = grpTicket;
    }

    @Override
    public String toString() {
        if (nbUnreadMsg != 0) {
            return groupName + "(" + nbUnreadMsg + ')';
        } else {
            return groupName;
        }
    }
}
