package com.team9.servlets;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/api/records/delete")
public class DeleteRecordServlet extends HttpServlet {

    private static final String URL = "jdbc:mysql://shortline.proxy.rlwy.net:54581/gmu";
    private static final String USER = "root";
    private static final String PASS = "xlLnDOFxroMxPrsYFLbhVVGvdXfOhBQy";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        String idStr = req.getParameter("recordid");

        if (idStr == null || idStr.isEmpty()) {
            out.print("0");
            return;
        }

        try {
            int recordid = Integer.parseInt(idStr);

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASS);

            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM records WHERE recordid = ?");
            ps.setInt(1, recordid);

            int rows = ps.executeUpdate();
            conn.close();

            out.print(rows > 0 ? "1" : "0");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("0");
        }
    }
}
