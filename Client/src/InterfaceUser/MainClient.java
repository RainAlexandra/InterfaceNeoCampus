/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InterfaceUser;

import java.util.Scanner;
import user.User;

/**
 *
 * @author mmadi
 */
public class MainClient {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        User u = new User(new UserInterface());
        u.runConnSocket(2345, "127.0.0.1"); // demarrage de la socket
        u.login("a1i1s1d149", "ai1008sd");
        boolean continuer = true;
        System.out.println("is connected : "+u.isConnected());
        if (u.isConnected()) {
            u.runRecvePassive(); //demarrage de l'atente 
            u.toString();
            while (continuer) {
                System.out.println("entrer une command :");
                if (u.isConnected()) {
                    switch (sc.nextLine()) {
                        case "sendm":
                            u.sendRequest(u.createMsgAnswer("2", "j'esssaie mes fonction", "INFORMATIQUE"));
                            break;
                        case "sendT":
                            u.sendRequest("");
                            break;
                        case "getG":
                            u.sendRequest("");
                            break;
                        case "getM":
                            u.sendRequest(u.requestGetMsg(u.getIdTicket(), "INFORMATIQUE"));
                            break;
                        case "getT":
                            u.sendRequest(u.requestGetTicket("INFORMATIQUE"));
                            break;
                        case "g":
                            u.afficherGroupe();
                            break;
                        case "gb":
                            u.afficherGroupeBox();
                            break;
                        case "m":
                            System.out.println("Groupe : "+u.getIdGroup());
                            System.out.println("Ticket : "+u.getIdTicket());
                            u.afficherListMsg();
                            break;
                        case "t":
                            System.out.println("Groupe : "+u.getIdGroup());
                            u.afficherLisTicket();
                            break;
                        case "c":
                            u.disconnect();
                            continuer = false;
                            break;
                    }
                }
            }
        }

    }
}
