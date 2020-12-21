import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/home"},loadOnStartup = 1)
public class GoingWithTheFlowServlet extends HttpServlet {

    DatabaseController db;
    /*Instantiates a DatabaseController object when servlet is active*/
    public GoingWithTheFlowServlet() {db =  new DatabaseController();}

    /*Upon receiving a doGet request from the client app it uses the methods of the Databases controller object db to:
        1)connects to the database
        2)executes a database search based on the parameters provided as key-value pairs at the end of the URL
        3)creates an arraylist of strings containing JSON strings (coding Patient objects)
        4)disconnects from database
        5)codes the arraylist of strings using JSON and responds to the client by writing the JSON string in the response body*/
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson gson = new Gson();
        try {
            db.connect();
            ArrayList<String> jsonStrings = db.executeSelect(req.getParameter("fields"),req.getParameter("table"),req.getParameter("where"));
            db.disconnect();
            resp.setContentType("application/json");
            resp.getWriter().println(gson.toJson(jsonStrings));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String reqBody=req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
         Gson gson = new Gson();
        try {
            db.connect();
            db.executeInsertPatient(gson.fromJson(reqBody,Patient.class));
            db.disconnect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        resp.setContentType("application/html");
        resp.getWriter().write("Executed POST request successfully");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try{
            db.connect();
            db.executeDelete(req.getParameter("table"),req.getParameter("where"));
            db.disconnect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        resp.setContentType("application/html");
        resp.getWriter().write("Executed DELETE request successfully");
    }

}
