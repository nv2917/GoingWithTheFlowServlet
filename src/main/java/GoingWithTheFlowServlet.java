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

    /*Upon receiving a doGet request for the path "/home" from the client app it uses the methods of the Databases controller object db to:
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
                log.info(" doGet requested: fields: "+req.getParameter("fields")+", table: "+req.getParameter("table")+", condition: "+req.getParameter("condition")+".");
                db.disconnect();
                resp.setContentType("application/json");
                resp.getWriter().println(gson.toJson(jsonStrings));
                log.info(" doGet responded with "+jsonStrings.size()+" entry/ies from "+req.getParameter("table")+" table.");
            } catch (SQLException throwables) {
                log.warning("Exception thrown:"+throwables.getStackTrace().toString());
            }
        }
    /*Upon receiving a doGet request for the path "/log" i.e. by browsing to that URL, the contents
      of the Log file are read in and printed on the page for diagnostic purposes */
        else if(req.getServletPath().equals("/log")) {
            resp.setContentType("text/html");
            FileInputStream fstream = new FileInputStream("GoingWithTheFlowServletLogFile.log");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fstream));
            String inputLine;
            while((inputLine = bufferedReader.readLine()) != null) {
                if(inputLine.contains("INFO")) {resp.getWriter().write("<p style='color:green'>"+inputLine+"</p>");}
                else if(inputLine.contains("WARNING")) {resp.getWriter().write("<p style='color:orange'>"+inputLine+"</p>");}
                else if(inputLine.contains("SEVERE")) {resp.getWriter().write("<p style='color:red'>"+inputLine+"</p>");}
                else{resp.getWriter().write(inputLine);}
            }
            bufferedReader.close();
            fstream.close();
        }
    }

    /*Upon receiving a doPost request from the client app it uses the methods of the Databases controller object db to:
       1)connect to the database
       2)decode the body of the request from JSON string to the patient object.
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
            log.info("doPost requested: Added patient with patient ID: "+p.getPatientId()+".");
            db.disconnect();
        } catch (SQLException throwables) {
            log.warning("Exception thrown:"+throwables.getStackTrace().toString());
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
            log.info("doPut requested: edited table "+req.getParameter("table")+" entry/ies where "+req.getParameter("condition")+" to "+req.getParameter("change")+".");
            db.disconnect();
        } catch (SQLException throwables) {
            log.warning("Exception thrown:"+throwables.getStackTrace().toString());
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
            log.info("doDelete requested: deleted table "+req.getParameter("table")+" entry where "+req.getParameter("condition")+".");
            db.disconnect();
        } catch (SQLException throwables) {
            log.warning("Exception thrown:"+throwables.getStackTrace().toString());
        }
    }

}
