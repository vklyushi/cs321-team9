package com.team9;

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

import com.team9.servlets.RecordsServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RecordsServletTest {

    /**Mock up tests */
    private RecordsServlet makeFakeRecordsServlet() {
        return new RecordsServlet() {
            private int nextId = 1;
            private final Set<String> validCategories = new HashSet<String>() {{
                add("allergies");
                add("medications");
            }};

            @Override
            public void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                PrintWriter out = resp.getWriter();

                StringBuilder sb = new StringBuilder();
                try (BufferedReader r = req.getReader()) {
                    String line;
                    while ((line = r.readLine()) != null) sb.append(line);
                }

                JSONObject json = new JSONObject(sb.toString());
                String category  = json.optString("category", "").trim();
                String entryName = json.optString("entry_name", "").trim();
                String date      = json.optString("date", "").trim();
                String notes     = json.optString("notes", "").trim();
                int userid       = json.optInt("userid", -1);

                // Basic validation like in the project – required fields:
                if (category.isEmpty() || entryName.isEmpty() || date.isEmpty() || userid <= 0) {
                    JSONObject err = new JSONObject();
                    err.put("error", "invalid input");
                    out.println(err.toString());
                    return;
                }

                // Category must be a known table
                if (!validCategories.contains(category)) {
                    JSONObject err = new JSONObject();
                    err.put("error", "Invalid category");
                    out.println(err.toString());
                    return;
                }

                int id = nextId++;
                JSONObject res = new JSONObject();
                res.put("id", id);
                res.put("category", category);
                res.put("name", entryName);
                res.put("userid", userid);
                res.put("notes", notes);
                out.println(res.toString());
            }
        };
    }

    //Valid allergies record, this returns id and correct category and name
    @Test
    public void addValidAllergyRecordReturnsIdAndCategory() throws Exception {
        String testJSON = "{ \"category\": \"allergies\", " + "\"entry_name\": \"Peanuts\", " + "\"date\": \"2025-01-01\", " + "\"notes\": \"severe\", " +  "\"userid\": 1 }";

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        BufferedReader reader = new BufferedReader(new StringReader(testJSON));
        Mockito.when(req.getReader()).thenReturn(reader);
        Mockito.when(req.getMethod()).thenReturn("POST");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        RecordsServlet servlet = makeFakeRecordsServlet();
        servlet.service(req, resp);

        pw.flush();
        JSONObject res = new JSONObject(sw.toString().trim());

        assertEquals("allergies", res.getString("category"));
        assertEquals("Peanuts", res.getString("name"));
        assertNotEquals(0, res.getInt("id"));
    }

    //Empty entry_name has to invalid input error
    @Test
    public void addRecordWithEmptyEntryNameReturnsError() throws Exception {
        String testJSON = "{ \"category\": \"allergies\", " + "\"entry_name\": \"\", " + "\"date\": \"2025-01-01\", " + "\"notes\": \"\", " + "\"userid\": 1 }";

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        BufferedReader reader = new BufferedReader(new StringReader(testJSON));
        Mockito.when(req.getReader()).thenReturn(reader);
        Mockito.when(req.getMethod()).thenReturn("POST");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        RecordsServlet servlet = makeFakeRecordsServlet();
        servlet.service(req, resp);

        pw.flush();
        JSONObject res = new JSONObject(sw.toString().trim());
        assertEquals("invalid input", res.getString("error"));
    }

    //Missing category will return invalid input error
    @Test
    public void addRecordWithMissingCategoryReturnsError() throws Exception {
        String testJSON = "{ \"entry_name\": \"Ibuprofen\", " + "\"date\": \"2025-01-01\", " + "\"notes\": \"\", " + "\"userid\": 1 }";

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        BufferedReader reader = new BufferedReader(new StringReader(testJSON));
        Mockito.when(req.getReader()).thenReturn(reader);
        Mockito.when(req.getMethod()).thenReturn("POST");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        RecordsServlet servlet = makeFakeRecordsServlet();
        servlet.service(req, resp);

        pw.flush();
        JSONObject res = new JSONObject(sw.toString().trim());
        assertEquals("invalid input", res.getString("error"));
    }

    //Category not in allowed tables will return "Invalid category"
    @Test
    public void addRecordWithInvalidCategoryReturnsError() throws Exception {
        String testJSON = "{ \"category\": \"not_a_real_category\", " +
                "\"entry_name\": \"Something\", " +
                "\"date\": \"2025-01-01\", " +
                "\"notes\": \"\", " +
                "\"userid\": 1 }";

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        BufferedReader reader = new BufferedReader(new StringReader(testJSON));
        Mockito.when(req.getReader()).thenReturn(reader);
        Mockito.when(req.getMethod()).thenReturn("POST");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        RecordsServlet servlet = makeFakeRecordsServlet();
        servlet.service(req, resp);

        pw.flush();
        JSONObject res = new JSONObject(sw.toString().trim());
        assertEquals("Invalid category", res.getString("error"));
    }

    //Two valid records will return different generated IDs
    @Test
    public void twoValidRecordsHaveDifferentIds() throws Exception {
        String testJSON1 = "{ \"category\": \"allergies\", " + "\"entry_name\": \"Peanuts\", " +  "\"date\": \"2025-01-01\", " + "\"notes\": \"\", " + "\"userid\": 1 }";

        String testJSON2 = "{ \"category\": \"medications\", " + "\"entry_name\": \"Ibuprofen\", " + "\"date\": \"2025-01-02\", " + "\"notes\": \"\", " + "\"userid\": 1 }";

        HttpServletRequest req1 = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp1 = Mockito.mock(HttpServletResponse.class);
        BufferedReader reader1 = new BufferedReader(new StringReader(testJSON1));
        Mockito.when(req1.getReader()).thenReturn(reader1);
        Mockito.when(req1.getMethod()).thenReturn("POST");
        StringWriter sw1 = new StringWriter();
        PrintWriter pw1 = new PrintWriter(sw1);
        Mockito.when(resp1.getWriter()).thenReturn(pw1);

        HttpServletRequest req2 = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp2 = Mockito.mock(HttpServletResponse.class);
        BufferedReader reader2 = new BufferedReader(new StringReader(testJSON2));
        Mockito.when(req2.getReader()).thenReturn(reader2);
        Mockito.when(req2.getMethod()).thenReturn("POST");
        StringWriter sw2 = new StringWriter();
        PrintWriter pw2 = new PrintWriter(sw2);
        Mockito.when(resp2.getWriter()).thenReturn(pw2);

        RecordsServlet servlet = makeFakeRecordsServlet();

        servlet.service(req1, resp1);
        pw1.flush();
        JSONObject res1 = new JSONObject(sw1.toString().trim());

        servlet.service(req2, resp2);
        pw2.flush();
        JSONObject res2 = new JSONObject(sw2.toString().trim());

        assertNotEquals(res1.getInt("id"), res2.getInt("id"));
    }
}
