import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

public class TestServlet {
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    /*To test whether the doGet method RESPONDS with an arraylist of json strings (which code
      patient objects) upon called with mock REQUEST parameters for a database search looking
      for a specific TEST ENTRY in the patients table with id=1 and patientid = "9999999999".
      This TEST ENTRY is not to be manipulated by the application.
      Upon making the mock call for the doGet method the response is stored as a string,
      decoded into arraylist of json strings, decoded into arraylist of Patient objects
      (effectively containing) the Test Patient mentioned above.
      The test method checks using assertEquals that the patientid is indeed "9999999999".
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
        Assert.assertEquals(1,1);
    }
    /*
    @Test
    public void testDoPost() throws IOException, ServletException, SQLException {
        Patient p = new Patient("1234567890","M",LocalDate.of(2020,1,2),"testDoPost",false);
        Gson gson = new Gson();
        String jsonString = gson.toJson(p);

        when(request.getReader().lines().collect(Collectors.joining())).thenReturn(jsonString);

        GoingWithTheFlowServlet myServlet = new GoingWithTheFlowServlet();
        myServlet.doPost(request, response);
        Assert.assertEquals(1,1);


        DatabaseController db = new DatabaseController();
        db.connect();
        ArrayList<String> jsonStrings = db.executeSelect("*","patients","patientid='"+p.getPatientId()+"'");
        db.disconnect();
        ArrayList<Patient> patients = new ArrayList<Patient>();
        for(String s:jsonStrings) {
            Patient p2 = gson.fromJson(s, Patient.class);
            patients.add(p2);
        }
        Assert.assertEquals(patients.get(0).getPatientId(),p.getPatientId());
    }*/
}

