package user;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Message {

    private String nom;
    private String prenom;
    private String msg;
    private String date;
    private String Color;
    private List<String> nomETstatus = new ArrayList<>();
    private String idMessage;

    public Message(String nom, String prenom, String msg, String color, String date, String[] nomETstatus) {
        this.nom = nom;
        this.prenom = prenom;
        this.msg = msg;
        this.Color = color;
        this.date = date;
        this.idMessage = nom + "/" + prenom + "/" + msg + "/" + date; //on a besoin du nom prenon msg et date
        for (String nomETstatu : nomETstatus) {
            this.nomETstatus.add(nomETstatu);
        }

    }

    @Override
    public String toString() {
        return msg;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.Color);
        hash = 41 * hash + Objects.hashCode(this.nomETstatus);
        hash = 41 * hash + Objects.hashCode(this.idMessage);
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
        final Message other = (Message) obj;
        if (!Objects.equals(this.idMessage, other.idMessage)) {
            return false;
        }
        return true;
    }
/**
         * @return
         */
    public String getNom() {
        return nom;
    }

    /**
     * @return
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * @return
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @return
     */
    public String getDate() {
        return date;
    }

    /**
     * @return
     */
    public String getColor() {
        return Color;
    }

    /**
     * @return
     */
    public List<String> getNomETstatus() {
        return nomETstatus;
    }
}
