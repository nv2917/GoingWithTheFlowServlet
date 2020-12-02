import javax.print.DocFlavor;
import java.sql.*;

public class Database {

    Connection conn;
    private String result;

    public Database() { }

    public void connect() throws SQLException {
        String dbURL = "jdbc:postgresql://ec2-54-247-94-127.eu-west-1.compute.amazonaws.com:5432/d5pbe2ejdb6rme";
        try {
            // Registers the driver
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {
        }
        conn = DriverManager.getConnection(dbURL, "xkvujomuqpzjpz", "9fc3f2d5f1941a2d2d41b6bac2cf21e30f30f77cfc23c958f18916fba11d9398");
    }

    public void executeInsert(String command) {
        try {
            Statement s = conn.createStatement();
            s.execute(command);
            s.close();
        }
        catch (Exception e) {
        }
    }

    public void executeDelete(String command) {
        try {
            Statement s = conn.createStatement();
            s.execute(command);
            s.close();
        }
        catch (Exception e) {
        }
    }

    public void executeSelect(String command) {
        try {
            Statement s = conn.createStatement();
            ResultSet rset = s.executeQuery(command);
            while(rset.next()) {
                result = rset.getInt("id")+" "+ rset.getString("familyname")+" "+ rset.getString("givenname");
            }
            rset.close();
            s.close();
        }
        catch (Exception e) {
        }
    }

    public void close() throws SQLException {
        conn.close();
    }


    public String getResult() {
        return result;
    }
}