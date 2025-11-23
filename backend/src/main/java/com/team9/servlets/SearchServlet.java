package com.team9.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

@WebServlet("/api/search")
public class SearchServlet extends HttpServlet {

    private static final String URL  = "jdbc:mysql://turntable.proxy.rlwy.net:44955/gmu";
    private static final String USER = "root";
    private static final String PASS = "xlLnDOFxroMxPrsYFLbhVVGvdXfOhBQy";

    private static final Gson gson = new Gson();

    /** Map categories → tables */
    private static final Map<String, String> TABLES = new HashMap<>();
    static {
        TABLES.put("allergies", "allergies");
        TABLES.put("vaccines", "vaccines");
        TABLES.put("procedures", "procedures");
        TABLES.put("insurance", "insurance");
        TABLES.put("injuries", "injuries");
        TABLES.put("medical_history", "medical_history");
        TABLES.put("family_history", "family_history");
        TABLES.put("calendar", "calendar");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        String q = req.getParameter("q");
        String uidStr = req.getParameter("userid");

        if (q == null || q.trim().isEmpty() || uidStr == null) {
            out.println("[]");
            return;
        }

        q = q.trim();
        int userid = Integer.parseInt(uidStr);

        JsonArray results = new JsonArray();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASS);

            for (String category : TABLES.keySet()) {
                String table = TABLES.get(category);

                String sql =
                    "SELECT id, name, date, notes " +
                    "FROM " + table + " " +
                    "WHERE userid = ? AND (name LIKE ? OR notes LIKE ?)";

                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, userid);
                ps.setString(2, "%" + q + "%");
                ps.setString(3, "%" + q + "%");

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("id", rs.getInt("id"));
                    obj.addProperty("name", rs.getString("name"));
                    obj.addProperty("date", rs.getString("date"));
                    obj.addProperty("notes", rs.getString("notes"));
                    obj.addProperty("category", category);
                    results.add(obj);
                }

                ps.close();
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("[]");
            return;
        }

        // return all results
        out.println(gson.toJson(results));
    }
}
