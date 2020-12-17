import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/home"},loadOnStartup = 1)
public class GoingWithTheFlowServlet extends HttpServlet {

    DatabaseController db =  new DatabaseController();


    public GoingWithTheFlowServlet() {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        resp.setContentType("application/html");
        resp.getWriter().write(req.getParameter("fields")+req.getParameter("table")+req.getParameter("where"));
        /*try {
            db.connect();
            ArrayList<String> jsonStrings = db.executeSelect(req.getParameter("fields"),req.getParameter("table"),req.getParameter("where"));
            resp.setContentType("application/json");
            resp.getWriter().println(gson.toJson(jsonStrings));
            db.disconnect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();*/
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reqBody=req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        resp.setContentType("application/html");
        try {
            db.connect();
            db.executeInsert(reqBody);
            resp.getWriter().write("Thank you client!! \nYour POST request:\n"+reqBody+"\nhas been executed successfully!");
            db.disconnect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reqBody=req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        resp.setContentType("application/html");
        try{
            db.connect();
            db.executeDelete(reqBody);
            resp.getWriter().write("Thank you client!! \nYour request:\n"+reqBody+"\nhas been executed successfully!");
            db.disconnect();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
