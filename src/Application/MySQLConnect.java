
package Application;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class MySQLConnect{
    Connection conn = null;
    public static Connection connectDB(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://sql7.freesqldatabase.com:3306/sql7582673", "sql7582673", "HPQ4BwASuc");
            //JOptionPane.showMessageDialog(null,"Connection Established");
            return conn;
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Couldn't Connect to The Data Base!");
            return null;
        }            
    }
}