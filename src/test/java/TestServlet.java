import com.google.gson.Gson;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;


import static org.mockito.Mockito.when;

public class TestServlet {

    /*No tests included for the void Database Controller methods*/
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    /*To test whether the doGet method RESPONDS with an arraylist of json strings (which code
      patient objects) upon called with mock REQUEST parameters for a database search (SELECT)
      looking for a specific TEST Patient ENTRY in the patients table with UNIQUE patientId =
      "9999999999" and id=1. This TEST ENTRY is not to be manipulated by the app.
      Upon making the mock call for the doGet method the response is stored as a string,
      decoded into arraylist of json strings, decoded into arraylist of Patient objects
      (effectively containing) the Test Patient mentioned above.
      The test method checks using assertEquals that the UNIQUE patientId is indeed "9999999999".
      Since this test method also calls the DatabaseController methods connect,disconnect and
      executeSelect (through doGet), it effectively tests these methods as well.*/
    @Test
    public void testDoGet() throws IOException, ServletException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        when(request.getServletPath()).thenReturn("/home");
        when(request.getParameter("fields")).thenReturn("*");
        when(request.getParameter("table")).thenReturn("patients");
        when(request.getParameter("condition")).thenReturn("id="+1);

        GoingWithTheFlowServlet myServlet = new GoingWithTheFlowServlet();
        myServlet.doGet(request, response);
        String output = stringWriter.getBuffer().toString();

        Gson gson = new Gson();
        ArrayList<String> jsonStrings = gson.fromJson(output,ArrayList.class);
        ArrayList<Patient> patients = new ArrayList<Patient>();
        for(String s:jsonStrings) {
            Patient p = gson.fromJson(s, Patient.class);
            patients.add(p);
        }
        Assert.assertEquals(patients.get(0).getPatientId(),"9999999999");
    }

    /*To test whether the doPost method correctly adds a patient entry to the database, at first
      a patient object is created (with UNIQUE patientId="9999999998" and initialDiagnosis = "testDoPost")
      and coded into a Json string, which is in turn used to create a StringReader and successively
      a BufferedReader which acts as a MOCK when the request's getReader method is called (upon calling doPost).
      To confirm that the patient row has been added a connection is established to the database and an SQL
      SELECT query is executed with the mock Patient's UNIQUE patientId.In this way the test method checks
      that indeed such an entry is found and using assertEquals that the entry's initialDiagnosis is the same
      as the mock Patient's one.
      Since this test method also calls the DatabaseController methods connect,disconnect and
      executeInsertPatient (through doPost), it effectively tests these methods as well.*/
    @Test
    public void testDoPost() throws IOException, ServletException, SQLException {
        Patient p = new Patient("9999999998","M",LocalDate.of(2020,1,2),"testDoPost",false);
        Gson gson = new Gson();
        String jsonString = gson.toJson(p);
        Reader jsonStringReader = new StringReader(jsonString);
        BufferedReader jsonStringBufferedReader = new BufferedReader(jsonStringReader);
        when(request.getReader()).thenReturn(jsonStringBufferedReader);

        GoingWithTheFlowServlet myServlet = new GoingWithTheFlowServlet();
        myServlet.doPost(request, response);

        String dbURL = "jdbc:postgresql://ec2-54-247-94-127.eu-west-1.compute.amazonaws.com:5432/d5pbe2ejdb6rme";
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {e.printStackTrace(); }
        Connection conn = DriverManager.getConnection(dbURL, "xkvujomuqpzjpz", "9fc3f2d5f1941a2d2d41b6bac2cf21e30f30f77cfc23c958f18916fba11d9398");
        Statement s = conn.createStatement();
        String sqlQr = "SELECT initialdiagnosis FROM patients WHERE patientid='" +p.getPatientId() + "';";
        ResultSet rset = s.executeQuery(sqlQr);
        String test = null;
        while(rset.next()) {test =  rset.getString("initialdiagnosis");}
        Assert.assertEquals(test,p.getInitialDiagnosis());

        sqlQr = "DELETE FROM patients WHERE patientid='" +p.getPatientId() + "';";
        s.execute(sqlQr);
        rset.close();
        s.close();
        conn.close();
    }

    /*To test whether the doPut method works correctly upon called with mock REQUEST parameters
     for a database UPDATE of a specific Patient TEST ENTRY's (with UNIQUE patientId = "9999999997"
     and UNIQUE id =2) date of birth to the current date. Similarly as in testDoPost an SQL SELECT
     query is executed to confirm the change in the database entry has indeed taken place.
     Since this test method also calls the DatabaseController methods connect,disconnect and
     executeEdit (through doPut), it effectively tests these methods as well.*/
    @Test
    public void testDoPut() throws IOException, ServletException, SQLException {
        when(request.getParameter("table")).thenReturn("patients");
        when(request.getParameter("change")).thenReturn("dateofbirth='"+LocalDate.now().toString()+"'");
        when(request.getParameter("condition")).thenReturn("id="+2);

        GoingWithTheFlowServlet myServlet = new GoingWithTheFlowServlet();
        myServlet.doPut(request, response);

        String dbURL = "jdbc:postgresql://ec2-54-247-94-127.eu-west-1.compute.amazonaws.com:5432/d5pbe2ejdb6rme";
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {e.printStackTrace(); }
        Connection conn = DriverManager.getConnection(dbURL, "xkvujomuqpzjpz", "9fc3f2d5f1941a2d2d41b6bac2cf21e30f30f77cfc23c958f18916fba11d9398");
        Statement s = conn.createStatement();

        String sqlQr = "SELECT dateofbirth FROM patients WHERE id='" + 2 + "';";
        ResultSet rset = s.executeQuery(sqlQr);
        LocalDate test = null;
        while(rset.next()) {test = rset.getDate("dateofbirth").toLocalDate();}
        Assert.assertEquals(test,LocalDate.now());
        rset.close();
        s.close();
        conn.close();
    }

    /*The doDelete method is tested similarly and is set to delete a TEST entry created
      with the execution of an SQL INSERT query. In this case executing the test asserts
      that executing the SQL SELECTS query for that entry does not return anything.
      Since this test method also calls the DatabaseController methods connect,disconnect and
      executeDelete (through doDelete), it effectively tests these methods as well.*/
    @Test
    public void testDoDelete() throws IOException, ServletException, SQLException {
        String dbURL = "jdbc:postgresql://ec2-54-247-94-127.eu-west-1.compute.amazonaws.com:5432/d5pbe2ejdb6rme";
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception e) {e.printStackTrace(); }
        Connection conn = DriverManager.getConnection(dbURL, "xkvujomuqpzjpz", "9fc3f2d5f1941a2d2d41b6bac2cf21e30f30f77cfc23c958f18916fba11d9398");
        Statement s = conn.createStatement();
        String sqlQr = "INSERT INTO patients (patientid,dateofbirth,sex,initialdiagnosis,needssideroom) VALUES ('9999999996','2020-01-01','Male','testDoDelete','false');";
        s.execute(sqlQr);

        when(request.getParameter("table")).thenReturn("patients");
        when(request.getParameter("condition")).thenReturn("patientid='9999999996'");

        GoingWithTheFlowServlet myServlet = new GoingWithTheFlowServlet();
        myServlet.doDelete(request, response);

        sqlQr = "SELECT patientid FROM patients WHERE patientid='9999999996';";
        ResultSet rset = s.executeQuery(sqlQr);
        Assert.assertEquals(rset.next(),false);
        rset.close();
        s.close();
        conn.close();
    }
}

