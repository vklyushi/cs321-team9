package com.team9.servlets;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import org.json.JSONObject;

@WebServlet("/api/user/info")
public class GetUserInfoServlet extends HttpServlet {

    private static final String URL = "jdbc:mysql://turntable.proxy.rlwy.net:44955/gmu";
    private static final String USER = "root";
    private static final String PASS = "xlLnDOFxroMxPrsYFLbhVVGvdXfOhBQy";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        String uidStr = req.getParameter("userid");
        if (uidStr == null) {
            out.println("{}");
            return;
        }

        try {
            int userid = Integer.parseInt(uidStr);

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASS);

            PreparedStatement ps = conn.prepareStatement(
                "SELECT fname, lname, dob, height, weight, bloodpressure, insurance " +
                "FROM user WHERE userid = ?"
            );
            ps.setInt(1, userid);

            ResultSet rs = ps.executeQuery();

            JSONObject json = new JSONObject();

            if (rs.next()) {
                json.put("fname", rs.getString("fname"));
                json.put("lname", rs.getString("lname"));
                json.put("dob", rs.getString("dob"));
                json.put("height", rs.getObject("height"));
                json.put("weight", rs.getObject("weight"));
                json.put("bloodpressure", rs.getObject("bloodpressure"));
                json.put("insurance", rs.getString("insurance"));
            }

            out.println(json.toString());
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("{}");
        }
    }
}
