package com.team9;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.*;
import org.mockito.*;
import static org.junit.Assert.assertEquals;
import com.team9.servlets.CreateAccountServlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ProjectAutomaticTesting {
    
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
}
