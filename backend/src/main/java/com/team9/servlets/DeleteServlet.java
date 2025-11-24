package com.team9.servlets;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;

@WebServlet("/api/delete")
public class DeleteServlet extends HttpServlet {

    private static final String URL = "jdbc:mysql://shortline.proxy.rlwy.net:54581/gmu";
    private static final String USER = "root";
    private static final String PASS = "xlLnDOFxroMxPrsYFLbhVVGvdXfOhBQy";

    // Map category → table name
    private static final HashMap<String, String> TABLES = new HashMap<>();
    static {
        TABLES.put("allergies", "allergies");
        TABLES.put("bloodwork", "bloodwork");
        TABLES.put("vaccines", "vaccines");
        TABLES.put("insurance", "insurance");
        TABLES.put("insurance_providers", "insurance_providers");
        TABLES.put("procedures", "procedures");
        TABLES.put("medications", "medications");
        TABLES.put("medical_history", "medical_history");
        TABLES.put("family_history", "family_history");
        TABLES.put("calendar", "calendar");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        String category = req.getParameter("category");
        String idStr = req.getParameter("id");
        String userStr = req.getParameter("userid");

        if (category == null || idStr == null || userStr == null) {
            resp.setStatus(400);
            out.println("Missing parameters.");
            return;
        }

        if (!TABLES.containsKey(category)) {
            resp.setStatus(400);
            out.println("Invalid category.");
            return;
        }

        int id, userid;
        try {
            id = Integer.parseInt(idStr);
            userid = Integer.parseInt(userStr);
        } catch (Exception e) {
            resp.setStatus(400);
            out.println("Bad ID format.");
            return;
        }

        String table = TABLES.get(category);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASS);

            String sql = "DELETE FROM " + table + " WHERE id=? AND userid=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setInt(2, userid);

            int affected = stmt.executeUpdate();
            conn.close();

            if (affected > 0) {
                resp.setStatus(200);
                out.println("Deleted.");
            } else {
                resp.setStatus(404);
                out.println("Record not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            out.println("Server error.");
        }
    }
}
