package main_server.socketServer.server_fonction;

import javax.xml.crypto.Data;

public class Msg {
    // balise nous permetant d'identifier la demande (type de msg

    private String msg;
    private int indice = 4;
    private String[] infos = null;

    public Msg(String msg) {
        this.msg = msg;
        infos = msg.split("/");
    }

    public String getMsg() {
        return msg;
    }
    //pour l'instant on se contente de ça pour l'exemple 

    /**
     * @return type de demande que le client à envoyer
     */
    public TypeOfRequest getTypeOfmsg() {
        int c = msg.charAt(0) - 48;
        switch (c) {
            case 1:
                return TypeOfRequest.AUTHENTIFICATION;
            case 2:
                return TypeOfRequest.CREATE_TICKET;
            case 3:
                return TypeOfRequest.ANSWAR;
            case 4:
                return TypeOfRequest.GET_TICKET;
            case 5:
                return TypeOfRequest.GET_MSG;
            case 6:
                return TypeOfRequest.GET_GOUPE;
            case 0:
                return TypeOfRequest.CLOSE_CONN;
        }
        return null;
    }

    /**
     * @return
     */
    public String getIdGroup() {
        return infos[1]; //à voir
    }

    /**
     * @return
     */
    public String getTitle() {
        return infos[2];
    }

    public String getDateOfMsg() {
        return infos[3];
    }

    /**
     * @return
     */
    public String getIdTicket() {
        return infos[1];
    }

    /**
     * @return
     */
    public String getContenue() {
        return infos[2];
    }

    /**
     * @return
     */
    public String getLogin() {
        return infos[1];
    }

    /**
     * @return
     */
    public String getPwd() {
        return infos[2];
    }

    public String getContenuDuTcket() {
        return infos[3];
    }
    
    public String getGroupFromAnswar() {
        return infos[4];
    }
    // si c'est le moment de la demande de connxion on va devoir extraire le mot de
    // passe
    // et l'identifiant une fois justifier il va falloire les mettre à jour
}
