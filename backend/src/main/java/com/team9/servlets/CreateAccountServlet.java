package com.team9.servlets;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import org.json.JSONObject;

@WebServlet("/api/accounts")
public class CreateAccountServlet extends HttpServlet {

    private static final String URL =
    "jdbc:mysql://shortline.proxy.rlwy.net:54581/gmu";

private static final String USER = "root";
private static final String PASS = "xlLnDOFxroMxPrsYFLbhVVGvdXfOhBQy";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        // Read JSON body
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = req.getReader()) {
            String line;
            while ((line = r.readLine()) != null) sb.append(line);
        }

        try {
            JSONObject json = new JSONObject(sb.toString());

            String displayname = json.getString("displayname").trim();
            String username    = json.getString("username").trim();
            String password    = json.getString("password").trim();
            String dob         = json.getString("dob").trim(); // YYYY-MM-DD

            // Backend-side validation (NEVER rely only on frontend)
            if (displayname.isEmpty() || username.isEmpty() || password.isEmpty() || dob.isEmpty()) {
                out.println("-1");
                return;
            }

            // Split display name
            String fname, lname = null;
            if (displayname.contains(" ")) {
                String[] parts = displayname.split(" ", 2);
                fname = parts[0];
                lname = parts[1];
            } else {
                fname = displayname;
            }

            // Connect to DB
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASS);

            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO user (fname, lname, login_id, dob, password) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, fname);
            ps.setString(2, lname);
            ps.setString(3, username);
            ps.setString(4, dob);
            ps.setString(5, password);

            try {
                int rows = ps.executeUpdate();

                if (rows == 1) {
                    ResultSet keys = ps.getGeneratedKeys();
                    if (keys.next()) {
                        int newId = keys.getInt(1);
                        System.out.println("Created user with userid = " + newId);
                    }
                    out.println("1"); // success
                } else {
                    out.println("-1"); // unexpected
                }
            }
            catch (SQLIntegrityConstraintViolationException dup) {
                // Username already exists (login_id UNIQUE constraint)
                out.println("-2");
            }

            conn.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            out.println("-1"); // server error
        }
    }
}
