import com.google.gson.Gson;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

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

    /* 1)Creates an SQL SELECT query to which it passes the search parameters (fields,table,condition)
       2)Executes the query
       3)For each entry obtained from table:
            a.  creates a Patient/Bed/Ward object by passing the respective fields
                to the respective attributes (through the constructor)
            b.  codes Patient/Bed/Ward object using JSON and adds string to arraylist of JSON strings
       4)returns arraylist of JSON strings*/
    public ArrayList<String> executeSelect(String fields,String table,String condition) throws SQLException {
        ArrayList<String> jsonStrings = new ArrayList<>();
        Gson gson = new Gson();
        Statement s = conn.createStatement();
        ResultSet rset = null;
        try {
            if(table.equals("patients")) {
                String sqlQr = "SELECT " + fields + " FROM " + table + " WHERE " + condition + " ORDER BY id ASC;";
                rset = s.executeQuery(sqlQr);
                while (rset.next()) {
                    Patient p = new Patient(rset.getInt("id"), rset.getString("patientid"),rset.getDate("dateofbirth").toLocalDate(),
                            rset.getInt("currentwardid"), rset.getInt("currentbedid"), rset.getString("sex"),
                            rset.getTimestamp("arrivaldatetime", Calendar.getInstance()).toLocalDateTime(), rset.getString("initialdiagnosis"),
                            rset.getBoolean("needssideroom"), rset.getBoolean("acceptedbymedicine"),
                            rset.getString("nextdestination"), rset.getTimestamp("estimatedatetimeofnext", Calendar.getInstance()).toLocalDateTime(),
                            rset.getBoolean("ttasignedoff"), rset.getBoolean("suitablefordischargelounge"),
                            rset.getString("transferrequeststatus"), rset.getBoolean("deceased"));
                    jsonStrings.add(gson.toJson(p));
                }
            }

            else if(table.equals("beds")) {
                String sqlQr = "SELECT " + fields + " FROM " + table + " WHERE " + condition + " ORDER BY bedid ASC;";
                rset = s.executeQuery(sqlQr);
                while ((rset.next())) {
                    Bed b = new Bed(rset.getInt("bedid"),rset.getInt("wardid"),rset.getString("status"),
                            rset.getString("forsex"),rset.getBoolean("hassideroom"));
                    jsonStrings.add(gson.toJson(b));
                }
            }

            else if(table.equals("wards")){
                String sqlQr = "SELECT " + fields + " FROM " + table + " WHERE " + condition + " ORDER BY wardid ASC ;";
                rset = s.executeQuery(sqlQr);
                while(rset.next()){
                    Ward w = new Ward(rset.getInt("wardid"),rset.getString("wardname"),rset.getString("wardtype"));
                    jsonStrings.add(gson.toJson(w));
                }
            }

            rset.close();
            s.close();
        } catch (Exception e) {e.printStackTrace();}
        return jsonStrings;
    }

    /* 1)Creates an SQL INSERT INTO patients query which passes the Patient's object attributes to the respective fields
       2)Executes the query*/
    public void executeInsertPatient(Patient p) {
        try {
            String sqlQuery = "INSERT INTO patients (patientid,dateofbirth,sex,initialdiagnosis,needssideroom) VALUES ('"+p.getPatientId()+"','"+p.getDateOfBirth()+"','"+p.getSex()+"','"+p.getInitialDiagnosis()+"','"+p.getNeedsSideRoom()+"');";
            Statement s = conn.createStatement();
            s.execute(sqlQuery);
            s.close();
        } catch (Exception e) {e.printStackTrace(); }
    }

    /* 1)Creates an SQL UPDATE query to which it passes the parameters (table,change,condition)
       2)Executes the query*/
    public void executeEdit(String table, String change, String condition) {
        try {
            String sqlQuery = "UPDATE " + table + " SET " + change + " WHERE " + condition + ";";
            Statement s = conn.createStatement();
            s.execute(sqlQuery);
            s.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    /* 1)Creates an SQL DELETE query to which it passes the search parameters (table,condition)
       2)Executes the query*/
    public void executeDelete(String table, String condition) {
        try {
            String sqlQuery = "DELETE FROM " + table + " WHERE " + condition + ";";
            Statement s = conn.createStatement();
            s.execute(sqlQuery);
            s.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    /*closes the connection to the Heroku database*/
    public void disconnect() throws SQLException {
        conn.close();
    }

}

