package com.team9.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/records")
public class RecordsServlet extends HttpServlet {

    private static final String URL  = "jdbc:mysql://turntable.proxy.rlwy.net:44955/gmu";
    private static final String USER = "root";
    private static final String PASS = "xlLnDOFxroMxPrsYFLbhVVGvdXfOhBQy";

    private static final Gson gson = new Gson();

    /** Mapping category → SQL table name **/
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try (BufferedReader reader = req.getReader()) {

            JsonObject body = gson.fromJson(reader, JsonObject.class);

            String category   = body.get("category").getAsString();
            String entryName  = body.get("entry_name").getAsString(); // UI still sends entry_name
            String date       = body.get("date").getAsString();
            String notes      = body.get("notes").getAsString();
            int userid        = body.get("userid").getAsInt();

            if (!TABLES.containsKey(category)) {
                resp.setStatus(400);
                out.println("{\"error\":\"Invalid category\"}");
                return;
            }

            String table = TABLES.get(category);

            // MATCHES your actual schema: (userid, name, date, notes)
            String sql = "INSERT INTO " + table + " (userid, name, date, notes) VALUES (?, ?, ?, ?)";

            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, userid);
                ps.setString(2, entryName);        // Goes into `name`
                ps.setString(3, date);             // YYYY-MM-DD
                ps.setString(4, notes);

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    resp.setStatus(500);
                    out.println("{\"error\":\"Insert failed\"}");
                    return;
                }

                ResultSet keys = ps.getGeneratedKeys();
                int id = keys.next() ? keys.getInt(1) : -1;

                JsonObject result = new JsonObject();
                result.addProperty("id", id);
                result.addProperty("category", category);
                result.addProperty("name", entryName);
                result.addProperty("date", date);

                out.println(gson.toJson(result));
            }

        } catch (Exception e) {
            resp.setStatus(500);
            out.println("{\"error\":\"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

    resp.setContentType("application/json");
    PrintWriter out = resp.getWriter();

    String category = req.getParameter("category");
    String uidStr   = req.getParameter("userid");

    if (uidStr == null) {
        resp.setStatus(400);
        out.println("{\"error\":\"Missing userid\"}");
        return;
    }

    int userid = Integer.parseInt(uidStr);

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(URL, USER, PASS);

        // ----------------------------------------------------------
        // CASE 1 — NO CATEGORY → RETURN ALL RECORDS FROM ALL TABLES
        // ----------------------------------------------------------
        if (category == null || category.isEmpty()) {
            StringBuilder json = new StringBuilder();
            json.append("[");

            boolean firstTableRecord = true;

            for (String cat : TABLES.keySet()) {

                String table = TABLES.get(cat);
                String sql = "SELECT id, name, date, notes FROM " + table +
                             " WHERE userid=? ORDER BY date DESC";

                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, userid);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    if (!firstTableRecord) json.append(",");
                    firstTableRecord = false;

                    json.append("{");
                    json.append("\"id\":").append(rs.getInt("id")).append(",");
                    json.append("\"category\":\"").append(cat).append("\",");
                    json.append("\"name\":\"").append(rs.getString("name")).append("\",");
                    json.append("\"date\":\"").append(rs.getString("date")).append("\",");
                    json.append("\"notes\":\"")
                            .append(rs.getString("notes") == null ? "" : rs.getString("notes").replace("\"","'"))
                            .append("\"");
                    json.append("}");
                }

                ps.close();
            }

            json.append("]");
            out.println(json.toString());
            conn.close();
            return;
        }

        // ----------------------------------------------------------
        // CASE 2 — SPECIFIC CATEGORY REQUESTED
        // ----------------------------------------------------------
        if (!TABLES.containsKey(category)) {
            resp.setStatus(400);
            out.println("{\"error\":\"Invalid category\"}");
            return;
        }

        String table = TABLES.get(category);
        String sql = "SELECT id, name, date, notes FROM " + table +
                     " WHERE userid=? ORDER BY date DESC";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userid);

        ResultSet rs = ps.executeQuery();

        StringBuilder json = new StringBuilder();
        json.append("[");

        boolean first = true;
        while (rs.next()) {
            if (!first) json.append(",");
            first = false;

            json.append("{");
            json.append("\"id\":").append(rs.getInt("id")).append(",");
            json.append("\"category\":\"").append(category).append("\",");
            json.append("\"name\":\"").append(rs.getString("name")).append("\",");
            json.append("\"date\":\"").append(rs.getString("date")).append("\",");
            json.append("\"notes\":\"")
                .append(rs.getString("notes") == null ? "" : rs.getString("notes").replace("\"","'"))
                .append("\"");
            json.append("}");
        }

        json.append("]");
        out.println(json.toString());
        conn.close();

    } catch (Exception e) {
        e.printStackTrace();
        resp.setStatus(500);
        out.println("{\"error\":\"" + e.getMessage() + "\"}");
    }
}
@Override
protected void doPut(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

    resp.setContentType("application/json");
    PrintWriter out = resp.getWriter();

    String uidStr = req.getParameter("userid");
    if (uidStr == null) {
        resp.setStatus(400);
        out.println("{\"error\":\"Missing userid\"}");
        return;
    }
    int userid = Integer.parseInt(uidStr);

    // BUILD A COMBINED LIST FROM ALL TABLES
    StringBuilder json = new StringBuilder();
    json.append("[");

    boolean first = true;

    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(URL, USER, PASS);

        for (String category : TABLES.keySet()) {
            String table = TABLES.get(category);

            String sql = "SELECT id, name, date, notes FROM " + table +
                         " WHERE userid = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userid);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (!first) json.append(",");
                first = false;

                json.append("{");
                json.append("\"id\":").append(rs.getInt("id")).append(",");
                json.append("\"name\":\"").append(rs.getString("name").replace("\"","'")).append("\",");
                json.append("\"date\":\"").append(rs.getString("date")).append("\",");
                json.append("\"notes\":\"").append(rs.getString("notes") == null ? "" : rs.getString("notes").replace("\"","'")).append("\",");
                json.append("\"category\":\"").append(category).append("\"");
                json.append("}");
            }
        }

        json.append("]");
        out.println(json.toString());
        conn.close();

    } catch (Exception e) {
        resp.setStatus(500);
        out.println("{\"error\":\"" + e.getMessage() + "\"}");
    }
}

}
