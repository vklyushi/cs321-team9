package com.team9.servlets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.*;
import java.sql.*;

public class SearchServletTest {

    /**
     * Utility to capture servlet output.
     */
    private static class ResponseCapture {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
    }

    @Test
    public void testSearchServletReturnsJson() throws Exception {

        // ---------------------------
        // Mock HTTP Request/Response
        // ---------------------------
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getParameter("q")).thenReturn("test");
        when(req.getParameter("userid")).thenReturn("5");

        ResponseCapture capture = new ResponseCapture();
        when(resp.getWriter()).thenReturn(capture.writer);

        // ---------------------------
        // Mock JDBC objects
        // ---------------------------
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPS = mock(PreparedStatement.class);
        ResultSet mockRS = mock(ResultSet.class);

        when(mockConn.prepareStatement(anyString())).thenReturn(mockPS);
        when(mockPS.executeQuery()).thenReturn(mockRS);

        // No rows in ResultSet → return false for next()
        when(mockRS.next()).thenReturn(false);

        // Mock DriverManager.getConnection(...)
        try (MockedStatic<DriverManager> mocked = mockStatic(DriverManager.class)) {

            mocked.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConn);

            // ---------------------------
            // Run the servlet
            // ---------------------------
            SearchServlet servlet = new SearchServlet();
            servlet.doGet(req, resp);
        }

        capture.writer.flush();
        String jsonOutput = capture.sw.toString().trim();

        // ---------------------------
        // Assertions
        // ---------------------------

        // Should be valid JSON
        assertDoesNotThrow(() -> JsonParser.parseString(jsonOutput));

        JsonElement element = JsonParser.parseString(jsonOutput);
        assertTrue(element.isJsonArray(), "Output should be a JSON array");

        JsonArray arr = element.getAsJsonArray();
        assertEquals(0, arr.size(), "Empty result set should return empty JSON array");
    }

    @Test
    public void testMissingParameters() throws Exception {

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        // Missing q and userid → should return []
        when(req.getParameter("q")).thenReturn(null);
        when(req.getParameter("userid")).thenReturn(null);

        ResponseCapture capture = new ResponseCapture();
        when(resp.getWriter()).thenReturn(capture.writer);

        SearchServlet servlet = new SearchServlet();
        servlet.doGet(req, resp);

        capture.writer.flush();
        String output = capture.sw.toString().trim();

        assertEquals("[]", output);
    }
}
