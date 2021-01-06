import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.basic.BasicButtonUI;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/home","/log"},loadOnStartup = 1)
public class GoingWithTheFlowServlet extends HttpServlet {

    DatabaseController db;
    private static final Logger log= Logger.getLogger(Patient.class.getName());

    /*Instantiates a DatabaseController object when servlet is active*/
    public GoingWithTheFlowServlet() throws IOException {
        db =  new DatabaseController();
        LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));
    }

    /*Upon receiving a doGet request from the client app it uses the methods of the Databases controller object db to:
        1)connect to the database
        2)execute a database search based on the parameters provided as key-value pairs at the end of the URL
        3)create an arraylist of strings containing JSON strings (coding Patient objects)
        4)disconnect from database
        5)code the arraylist of strings using JSON and responds to the client by writing the JSON string in the response body*/
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getServletPath().equals("/home")) {
            Gson gson = new Gson();
            try {
                db.connect();
                ArrayList<String> jsonStrings = db.executeSelect(req.getParameter("fields"),req.getParameter("table"),req.getParameter("condition"));
                db.disconnect();
                resp.setContentType("application/json");
                resp.getWriter().println(gson.toJson(jsonStrings));

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        else if(req.getServletPath().equals("/log")) {
            FileInputStream fstream = new FileInputStream("GoingWithTheFlowServletLogFile.log");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fstream));
            String inputLine;
            while((inputLine = bufferedReader.readLine()) != null) {
                resp.getWriter().write(inputLine+"\n");
            }
            bufferedReader.close();
            fstream.close();
        }
    }

    /*Upon receiving a doPost request from the client app it uses the methods of the Databases controller object db to:
       1)connect to the database
       2)decode the body of the request from json string to the patient object.
       3)add the patient to the database by passing its attributes to the respective fields
       4)disconnect from database*/
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String reqBody=req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Gson gson = new Gson();
        try {
            db.connect();
            Patient p = gson.fromJson(reqBody,Patient.class);
            db.executeInsertPatient(p);
            db.disconnect();
            log.info("doPost requested: Added patient "+p.getPatientId()+".");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /*Upon receiving a doDelete request from the client app it uses the methods of the Databases controller object db to:
        1)connect to the database
        2)execute a database entry edit(update) based on the parameters provided as key-value pairs at the end of the URL
        3)disconnect from database*/
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try{
            db.connect();
            db.executeEdit(req.getParameter("table"),req.getParameter("change"),req.getParameter("condition"));
            db.disconnect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /*Upon receiving a doDelete request from the client app it uses the methods of the Databases controller object db to:
        1)connect to the database
        2)execute a database entry delete(drop) based on the parameters provided as key-value pairs at the end of the URL
        3)disconnect from database*/
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try{
            db.connect();
            db.executeDelete(req.getParameter("table"),req.getParameter("condition"));
            db.disconnect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
