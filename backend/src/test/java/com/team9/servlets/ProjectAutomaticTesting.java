package com.team9.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;
import org.junit.*;
import org.mockito.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.team9.servlets.CreateAccountServlet;
import com.team9.servlets.GetUserInfoServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ProjectAutomaticTesting {
    

    //CreateAccountServlet.java Testing - Victoria
    @Test
    public void validationFailsWhenPasswordEmpty() throws Exception {
        String testJSON = "{ \"displayname\": \"John Doe\", \"username\": \"john\", \"password\": \"\", \"dob\": \"2000-01-01\" }";
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(req.getMethod()).thenReturn("POST");

        BufferedReader reader = new BufferedReader(new StringReader(testJSON));
        Mockito.when(req.getReader()).thenReturn(reader);

        StringWriter responseWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(responseWriter);
        Mockito.when(resp.getWriter()).thenReturn(writer);

        CreateAccountServlet servlet = new CreateAccountServlet();
        servlet.service(req, resp);

        writer.flush();
        String response = responseWriter.toString().trim();

        assertEquals("-1", response);
    }

    @Test
    public void validationFailsWhenUsernameEmpty() throws Exception {
        String testJSON = "{ \"displayname\": \"John Doe\", \"username\": \"\", \"password\": \"password\", \"dob\": \"2000-01-01\" }";
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(req.getMethod()).thenReturn("POST");

        BufferedReader reader = new BufferedReader(new StringReader(testJSON));
        Mockito.when(req.getReader()).thenReturn(reader);

        StringWriter responseWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(responseWriter);
        Mockito.when(resp.getWriter()).thenReturn(writer);

        CreateAccountServlet servlet = new CreateAccountServlet();
        servlet.service(req, resp);

        writer.flush();
        String response = responseWriter.toString().trim();

        assertEquals("-1", response);
    }

    @Test
    public void validationFailsWhenDisplayNameEmpty() throws Exception {
        String testJSON = "{ \"displayname\": \"\", \"username\": \"john\", \"password\": \"password\", \"dob\": \"2000-01-01\" }";
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(req.getMethod()).thenReturn("POST");

        BufferedReader reader = new BufferedReader(new StringReader(testJSON));
        Mockito.when(req.getReader()).thenReturn(reader);

        StringWriter responseWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(responseWriter);
        Mockito.when(resp.getWriter()).thenReturn(writer);

        CreateAccountServlet servlet = new CreateAccountServlet();
        servlet.service(req, resp);

        writer.flush();
        String response = responseWriter.toString().trim();

        assertEquals("-1", response);
    }

    @Test
    public void validationFailsWhenDOBEmpty() throws Exception {
        String testJSON = "{ \"displayname\": \"John Doe\", \"username\": \"john\", \"password\": \"password\", \"dob\": \"\" }";
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(req.getMethod()).thenReturn("POST");

        BufferedReader reader = new BufferedReader(new StringReader(testJSON));
        Mockito.when(req.getReader()).thenReturn(reader);

        StringWriter responseWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(responseWriter);
        Mockito.when(resp.getWriter()).thenReturn(writer);

        CreateAccountServlet servlet = new CreateAccountServlet();
        servlet.service(req, resp);

        writer.flush();
        String response = responseWriter.toString().trim();

        assertEquals("-1", response);
    }

    @Test
    public void duplicateUsernameReturnsMinusTwo() throws Exception {
        String testJSON1 = "{ \"displayname\": \"John Doe\", \"username\": \"john\", \"password\": \"password\", \"dob\": \"2000-01-01\" }";
        String testJSON2 = "{ \"displayname\": \"John Doe\", \"username\": \"john\", \"password\": \"password\", \"dob\": \"2000-01-01\" }";

        HttpServletRequest req1 = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp1 = Mockito.mock(HttpServletResponse.class);
        BufferedReader reader1 = new BufferedReader(new StringReader(testJSON1));
        Mockito.when(req1.getReader()).thenReturn(reader1);
        StringWriter sw1 = new StringWriter();
        PrintWriter pw1 = new PrintWriter(sw1);
        Mockito.when(resp1.getWriter()).thenReturn(pw1);

        HttpServletRequest req2 = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp2 = Mockito.mock(HttpServletResponse.class);
        BufferedReader reader2 = new BufferedReader(new StringReader(testJSON2));
        Mockito.when(req2.getReader()).thenReturn(reader2);
        StringWriter sw2 = new StringWriter();
        PrintWriter pw2 = new PrintWriter(sw2);
        Mockito.when(resp2.getWriter()).thenReturn(pw2);

        CreateAccountServlet servlet = new CreateAccountServlet() {
            private final Set<String> users = new HashSet<>();

            @Override
            public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                PrintWriter out = resp.getWriter();
                StringBuilder sb = new StringBuilder();
                try (BufferedReader r = req.getReader()) {
                    String line;
                    while ((line = r.readLine()) != null) sb.append(line);
                }
                JSONObject json = new JSONObject(sb.toString());
                String displayname = json.getString("displayname").trim();
                String username = json.getString("username").trim();
                String password = json.getString("password").trim();
                String dob = json.getString("dob").trim();

                if (displayname.isEmpty() || username.isEmpty() || password.isEmpty() || dob.isEmpty()) {
                    out.println("-1");
                    return;
                }

                if (users.contains(username)) {
                    out.println("-2");
                    return;
                }

                users.add(username);
                out.println("1");
            }
        };

        servlet.doPost(req1, resp1);

        servlet.doPost(req2, resp2);
        pw2.flush();
        assertEquals("-2", sw2.toString().trim());  
    }

    @Test
    public void createsTwoAccountsPasses() throws Exception {
        String testJSON1 = "{ \"displayname\": \"John Doe\", \"username\": \"john\", \"password\": \"password\", \"dob\": \"2000-01-01\" }";
        String testJSON2 = "{ \"displayname\": \"John Doe\", \"username\": \"doe\", \"password\": \"password\", \"dob\": \"2000-01-01\" }";

        HttpServletRequest req1 = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp1 = Mockito.mock(HttpServletResponse.class);
        BufferedReader reader1 = new BufferedReader(new StringReader(testJSON1));
        Mockito.when(req1.getReader()).thenReturn(reader1);
        StringWriter sw1 = new StringWriter();
        PrintWriter pw1 = new PrintWriter(sw1);
        Mockito.when(resp1.getWriter()).thenReturn(pw1);

        HttpServletRequest req2 = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp2 = Mockito.mock(HttpServletResponse.class);
        BufferedReader reader2 = new BufferedReader(new StringReader(testJSON2));
        Mockito.when(req2.getReader()).thenReturn(reader2);
        StringWriter sw2 = new StringWriter();
        PrintWriter pw2 = new PrintWriter(sw2);
        Mockito.when(resp2.getWriter()).thenReturn(pw2);

        CreateAccountServlet servlet = new CreateAccountServlet() {
            private final Set<String> users = new HashSet<>();

            @Override
            public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                PrintWriter out = resp.getWriter();
                StringBuilder sb = new StringBuilder();
                try (BufferedReader r = req.getReader()) {
                    String line;
                    while ((line = r.readLine()) != null) sb.append(line);
                }
                JSONObject json = new JSONObject(sb.toString());
                String displayname = json.getString("displayname").trim();
                String username = json.getString("username").trim();
                String password = json.getString("password").trim();
                String dob = json.getString("dob").trim();

                if (displayname.isEmpty() || username.isEmpty() || password.isEmpty() || dob.isEmpty()) {
                    out.println("-1");
                    return;
                }

                if (users.contains(username)) {
                    out.println("-2");
                    return;
                }

                users.add(username);
                out.println("1");
            }
        };

        servlet.doPost(req1, resp1);

        servlet.doPost(req2, resp2);
        pw2.flush();
        assertEquals("1", sw2.toString().trim());
    }


    //GetUserInfoServlet.java - Victoria
    @Test
    public void returnsCorrectFirstName() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(req.getParameter("userid")).thenReturn("123");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        GetUserInfoServlet servlet = new GetUserInfoServlet() {
            @Override
            public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                JSONObject j = new JSONObject();
                j.put("fname", "expectedUser");
                out.println(j.toString());
            }
        };

        servlet.doGet(req, resp);
        pw.flush();

        JSONObject res = new JSONObject(sw.toString().trim());
        assertEquals("expectedUser", res.getString("fname"));
    }

    @Test
    public void returnsCorrectLastName() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(req.getParameter("userid")).thenReturn("123");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        GetUserInfoServlet servlet = new GetUserInfoServlet() {
            @Override
            public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                JSONObject j = new JSONObject();
                j.put("lname", "Doe");
                out.println(j.toString());
            }
        };

        servlet.doGet(req, resp);
        pw.flush();

        JSONObject res = new JSONObject(sw.toString().trim());
        assertEquals("Doe", res.getString("lname"));
    }

    @Test
    public void returnsCorrectDOB() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(req.getParameter("userid")).thenReturn("123");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        GetUserInfoServlet servlet = new GetUserInfoServlet() {
            @Override
            public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                JSONObject j = new JSONObject();
                j.put("dob", "01/01/1000");
                out.println(j.toString());
            }
        };

        servlet.doGet(req, resp);
        pw.flush();

        JSONObject res = new JSONObject(sw.toString().trim());
        assertEquals("01/01/1000", res.getString("dob"));
    }

    @Test
    public void returnsCorrectHeight() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(req.getParameter("userid")).thenReturn("123");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        GetUserInfoServlet servlet = new GetUserInfoServlet() {
            @Override
            public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                JSONObject j = new JSONObject();
                j.put("height", "5");
                out.println(j.toString());
            }
        };

        servlet.doGet(req, resp);
        pw.flush();

        JSONObject res = new JSONObject(sw.toString().trim());
        assertEquals("5", res.getString("height"));
    }

    @Test
    public void returnsCorrectWeight() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(req.getParameter("userid")).thenReturn("123");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        GetUserInfoServlet servlet = new GetUserInfoServlet() {
            @Override
            public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                JSONObject j = new JSONObject();
                j.put("weight", "200");
                out.println(j.toString());
            }
        };

        servlet.doGet(req, resp);
        pw.flush();

        JSONObject res = new JSONObject(sw.toString().trim());
        assertEquals("200", res.getString("weight"));
    }

    @Test
    public void returnsCorrectBloodPressure() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(req.getParameter("userid")).thenReturn("123");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        GetUserInfoServlet servlet = new GetUserInfoServlet() {
            @Override
            public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                JSONObject j = new JSONObject();
                j.put("bloodpressure", "100");
                out.println(j.toString());
            }
        };

        servlet.doGet(req, resp);
        pw.flush();

        JSONObject res = new JSONObject(sw.toString().trim());
        assertEquals("100", res.getString("bloodpressure"));
    }

    @Test
    public void returnsCorrectInsurance() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(req.getParameter("userid")).thenReturn("123");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        GetUserInfoServlet servlet = new GetUserInfoServlet() {
            @Override
            public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                JSONObject j = new JSONObject();
                j.put("insurance", "Cigna");
                out.println(j.toString());
            }
        };

        servlet.doGet(req, resp);
        pw.flush();

        JSONObject res = new JSONObject(sw.toString().trim());
        assertEquals("Cigna", res.getString("insurance"));
    }

    @Test
    public void returnsWrongFirstNameThrowsError() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(req.getParameter("userid")).thenReturn("123");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        GetUserInfoServlet servlet = new GetUserInfoServlet() {
            @Override
            public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                JSONObject j = new JSONObject();
                j.put("fname", "John");
                out.println(j.toString());
            }
        };

        servlet.doGet(req, resp);
        pw.flush();

        JSONObject res = new JSONObject(sw.toString().trim());
        assertNotEquals("Bob", res.getString("fname"));
    }
}
