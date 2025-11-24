package com.team9.servlets;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    private static final String URL =
    "jdbc:mysql://shortline.proxy.rlwy.net:54581/gmu";

private static final String USER = "root";
private static final String PASS = "xlLnDOFxroMxPrsYFLbhVVGvdXfOhBQy";


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        String u = req.getParameter("username");
        String p = req.getParameter("password");

        if (u == null || p == null) {
            out.println("-1");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASS);

            PreparedStatement ps = conn.prepareStatement(
                "SELECT userid FROM user WHERE login_id=? AND password=?"
            );
            ps.setString(1, u);
            ps.setString(2, p);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userid = rs.getInt("userid");
                out.println("1," + userid);  
            } else {
                out.println("-1");
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("-1");
        }
    }
}
