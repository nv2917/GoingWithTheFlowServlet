import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/home"},loadOnStartup = 1)
public class GoingWithTheFlowServlet extends HttpServlet {

    Database db;

    public GoingWithTheFlowServlet() {
        db = new Database();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.getWriter().write("GOING WITH THE FLOW!!");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String reqBody=req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        resp.setContentType("application/html");
        try {
            db.connect();
            if(reqBody.contains("INSERT")) {
                db.executeInsert(reqBody);
                resp.getWriter().write("Thank you client!! \nYour request:\n"+reqBody+"\nhas been executed successfully!");
            }
            else if(reqBody.contains("SELECT")) {
                db.executeSelect(reqBody);
                resp.getWriter().write("Thank you client!! \nThe results of your request is:\n"+db.getResult());
            }
            else {
                db.executeInsert(reqBody);
                resp.getWriter().write("Thank you client!! \nYour request:\n"+reqBody+"\nhas been executed successfully!");
            }
            db.close();
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
            db.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
