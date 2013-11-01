package com.trackerboik.bdd;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
	 
    public static void main(String[] args) {
        Connexion connexion = new Connexion("C:\\Users\\Gaetan\\Documents\\Developpement\\TrackerBoik\\BDDAT\\trackerboikbddat.sqlite");
        connexion.connect();
        ResultSet resultSet = connexion.query("SELECT * FROM PlayerPS");
        try {
            while (resultSet.next()) {
                System.out.println("Player : "+resultSet.getString("PlayerID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
 
        connexion.close();
    }
 
}
