import com.google.gson.Gson;
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

public class DatabaseController {

    Connection conn;


    public DatabaseController() {
    }

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
        } catch (Exception e) {
        }
    }

    public void executeDelete(String command) {
        try {
            Statement s = conn.createStatement();
            s.execute(command);
            s.close();
        } catch (Exception e) {
        }
    }

    public ArrayList<String> executeSelect(String fields, String table, String criteria) {
        ArrayList<String> jsonStrings = new ArrayList<String>();
        Gson gson = new Gson();
        //Patient p = null;
        try {
            Statement s = conn.createStatement();
            String sqlCmd = "SELECT " + fields + " from " + table + " where " + criteria + ";";
            ResultSet rset = s.executeQuery(sqlCmd);
            while (rset.next()) {
                 Patient p = new Patient(rset.getInt("id"), rset.getString("nameinitials"), rset.getString("currentlocation"),
                        rset.getString("sex"), rset.getTime("arrivaltime"), rset.getString("initialdiagnosis"),
                        rset.getBoolean("needssideroom"), rset.getBoolean("acceptedbymedicine"), rset.getString("nextdestination"),
                        rset.getTime("estimatedtimeofnext"), rset.getBoolean("ttasignedoff"), rset.getBoolean("suitablefordischargelounge"), rset.getString("transferrequeststatus"),
                        rset.getBoolean("deceased"));
                jsonStrings.add(gson.toJson(p));
            }
            rset.close();
            s.close();
        } catch (Exception e) {
        }
        return jsonStrings;
    }

    public void disconnect() throws SQLException {
        conn.close();
    }


}

