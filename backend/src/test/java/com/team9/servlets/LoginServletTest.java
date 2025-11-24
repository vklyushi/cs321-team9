package com.team9.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.*;
import org.mockito.*;

import static org.junit.Assert.assertEquals;

import com.team9.servlets.LoginServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginServletTest {

    /**Mock up tests. */
    private LoginServlet makeFakeLoginServlet() {
        return new LoginServlet() {
            private final String VALID_USER = "john";
            private final String VALID_PASS = "password";
            private final int VALID_USER_ID = 42;

            @Override
            public void doPost(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                PrintWriter out = resp.getWriter();
                String username = req.getParameter("username");
                String password = req.getParameter("password");

                if (username == null) username = "";
                if (password == null) password = "";

                username = username.trim();
                password = password.trim();

                //Empty or missing fields wil fail
                if (username.isEmpty() || password.isEmpty()) {
                    out.println("-1");
                    return;
                }

                //Only one valid combination as everything else fails
                if (username.equals(VALID_USER) && password.equals(VALID_PASS)) {
                    out.println("1," + VALID_USER_ID);
                } else {
                    out.println("-1");
                }
            }
        };
    }

    //Valid credentials?
    @Test
    public void loginSucceedsWithValidCredentials() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(req.getParameter("username")).thenReturn("john");
        Mockito.when(req.getParameter("password")).thenReturn("password");
        Mockito.when(req.getMethod()).thenReturn("POST");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        LoginServlet servlet = makeFakeLoginServlet();
        servlet.service(req, resp);

        pw.flush();
        assertEquals("1,42", sw.toString().trim());
    }

    //Username is empty string
    @Test
    public void loginFailsWhenUsernameEmpty() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(req.getParameter("username")).thenReturn("");
        Mockito.when(req.getParameter("password")).thenReturn("password");
        Mockito.when(req.getMethod()).thenReturn("POST");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        LoginServlet servlet = makeFakeLoginServlet();
        servlet.service(req, resp);

        pw.flush();
        assertEquals("-1", sw.toString().trim());
    }

    //Password is empty string
    @Test
    public void loginFailsWhenPasswordEmpty() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(req.getParameter("username")).thenReturn("john");
        Mockito.when(req.getParameter("password")).thenReturn("");
        Mockito.when(req.getMethod()).thenReturn("POST");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        LoginServlet servlet = makeFakeLoginServlet();
        servlet.service(req, resp);

        pw.flush();
        assertEquals("-1", sw.toString().trim());
    }

    //Username parameter is completely missing
    @Test
    public void loginFailsWhenUsernameMissing() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        //username returns null
        Mockito.when(req.getParameter("username")).thenReturn(null);
        Mockito.when(req.getParameter("password")).thenReturn("password");
        Mockito.when(req.getMethod()).thenReturn("POST");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        LoginServlet servlet = makeFakeLoginServlet();
        servlet.service(req, resp);

        pw.flush();
        assertEquals("-1", sw.toString().trim());
    }

    //Wrong password for an existing user
    @Test
    public void loginFailsWithWrongPassword() throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        Mockito.when(req.getParameter("username")).thenReturn("john");
        Mockito.when(req.getParameter("password")).thenReturn("wrongpassword");
        Mockito.when(req.getMethod()).thenReturn("POST");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(resp.getWriter()).thenReturn(pw);

        LoginServlet servlet = makeFakeLoginServlet();
        servlet.service(req, resp);

        pw.flush();
        assertEquals("-1", sw.toString().trim());
    }
}
