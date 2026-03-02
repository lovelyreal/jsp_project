package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Field field = extractField(session);
        int index = getSelectedIndex(req);
        if(field.getField().get(index) != Sign.EMPTY){
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, resp);
            return;
        }
        field.getField().put(index, Sign.CROSS);
        if (checkWin(session, resp, field)) {
            return;
        }
        int emptyFieldIndex = field.getEmptyFieldIndex();
        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            if (checkWin(session,resp, field)) {
                return;
            }
        } else {
            session.setAttribute("draw", true);
        }

        List<Sign> data = field.getFieldData();
        session.setAttribute("data", data);
        session.setAttribute("index", index);
        resp.sendRedirect("/index.jsp");

    }


    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }
    private Field extractField(HttpSession currentSession) {
        Object fieldAttribute = currentSession.getAttribute("field");
        if (Field.class != fieldAttribute.getClass()) {
            currentSession.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) fieldAttribute;
    }
    private boolean checkWin(HttpSession currentSession, HttpServletResponse resp, Field field) {
        Sign winnerSign = field.checkWin();
        if (winnerSign == Sign.CROSS ||  winnerSign == Sign.NOUGHT) {
            currentSession.setAttribute("winner", winnerSign);
            List<Sign> data = field.getFieldData();
            currentSession.setAttribute("data", data);
            try {
                resp.sendRedirect("/index.jsp");
            } catch (IOException ignored) {}
            return true;
        }
        return false;
    }

}
