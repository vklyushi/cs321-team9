package com.team9.servlets;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public class EmbeddedServerTest {

    @Test
    public void mainMethodExistsAndIsPublicStatic() throws Exception {
        Method main = EmbeddedServer.class.getMethod("main", String[].class);
        int mods = main.getModifiers();

        assertTrue(Modifier.isPublic(mods), "main method should be public");
        assertTrue(Modifier.isStatic(mods), "main method should be static");
        assertEquals(void.class, main.getReturnType(), "main should return void");
    }
}
