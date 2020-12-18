import com.google.gson.Gson;
import java.sql.*;
import java.util.ArrayList;

public class DatabaseController {

    private Connection conn;

    public DatabaseController() { }

    /*establishes a connection to the Heroku database using the database credentials*/
    public void connect() throws SQLException {
        String dbURL = "jdbc:postgresql://ec2-54-247-94-127.eu-west-1.compute.amazonaws.com:5432/d5pbe2ejdb6rme";
        try {
            // Registers the driver
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {e.printStackTrace(); }
        conn = DriverManager.getConnection(dbURL, "xkvujomuqpzjpz", "9fc3f2d5f1941a2d2d41b6bac2cf21e30f30f77cfc23c958f18916fba11d9398");
    }

    public void executeInsert(String command) {
        try {
            Statement s = conn.createStatement();
            s.execute(command);
            s.close();
        } catch (Exception e) {e.printStackTrace(); }
    }

    public void executeDelete(String command) {
        try {
            Statement s = conn.createStatement();
            s.execute(command);
            s.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    /* 1)Creates an SQL SELECT query as a string to which it passes the search parameters (fields,table,where)
       2)Executes the query
       3)For each entry obtained from table:
            a.  creates a Patient object by passing the respective fields
                to the respective attributes (through the constructor)
            b.  codes Patient object using JSON and adds string to arraylist of JSON strings
       4)returns arraylist of JSON strings
     */

    public ArrayList<String> executeSelect(String fields, String table, String criteria) {
        ArrayList<String> jsonStrings = new ArrayList<>();
        Gson gson = new Gson();
        try {
            String sqlQr = "select " + fields + " from " + table + " where " + criteria + ";";
            Statement s = conn.createStatement();
            ResultSet rset = s.executeQuery(sqlQr);
            while (rset.next()) {
                 Patient p = new Patient(rset.getInt("id"),rset.getString("nameinitials"),
                        rset.getString("currentlocation"),rset.getString("sex"),
                        rset.getTime("arrivaltime"),rset.getString("initialdiagnosis"),
                        rset.getBoolean("needssideroom"),rset.getBoolean("acceptedbymedicine"),
                        rset.getString("nextdestination"),rset.getTime("estimatedtimeofnext"),
                        rset.getBoolean("ttasignedoff"), rset.getBoolean("suitablefordischargelounge"),
                        rset.getString("transferrequeststatus"),rset.getBoolean("deceased"));
                jsonStrings.add(gson.toJson(p));
            }
            rset.close();
            s.close();
        } catch (Exception e) {e.printStackTrace();}
        return jsonStrings;
    }

    /*closes the connection to the Heroku database*/
    public void disconnect() throws SQLException {
        conn.close();
    }


}

