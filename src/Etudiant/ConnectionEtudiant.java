package Etudiant;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionEtudiant {
        Connection cn;
        public ConnectionEtudiant() {
            try {

                Class.forName("com.mysql.jdbc.Driver");

                cn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/etudiant","root","");
                System.out.println("Connectio Etablie");

            }catch (Exception e){
                System.out.println("Erreur de connection");
                e.printStackTrace();
            }
        }
        public Connection maConnetion(){
            return  cn;
        }
}
