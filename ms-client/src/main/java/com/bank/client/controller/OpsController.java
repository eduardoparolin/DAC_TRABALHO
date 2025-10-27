package com.bank.client.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.*;

@RestController
@RequestMapping("/ops")
public class OpsController {

    private final Environment env;
    private final DataSource dataSource;

    public OpsController(Environment env, DataSource dataSource) {
        this.env = env;
        this.dataSource = dataSource;
    }

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("service", env.getProperty("spring.application.name", "ms-client"));
        out.put("status", "ok");
        out.put("port", env.getProperty("server.port", "8086"));
        out.put("profiles", Arrays.asList(env.getActiveProfiles()));
        return out;
    }

    @GetMapping("/headers")
    public Map<String, Object> headers(HttpServletRequest req) {
        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> names = req.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            headers.put(name, req.getHeader(name));
        }
        return Map.of("headers", headers);
    }

    @GetMapping("/dbinfo")
    public Map<String, Object> dbinfo() {
        Map<String, Object> out = new LinkedHashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData md = conn.getMetaData();
            out.put("productName", md.getDatabaseProductName());
            out.put("productVersion", md.getDatabaseProductVersion());
            out.put("driverName", md.getDriverName());
            out.put("url", md.getURL());
            out.put("user", md.getUserName());
            String schema = conn.getSchema();
            out.put("schema", schema == null ? "" : schema);
        } catch (Exception e) {
            out.put("error", e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        return out;
    }
}
