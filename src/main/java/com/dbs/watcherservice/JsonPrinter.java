package com.dbs.watcherservice;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonPrinter {

    public static void printJson(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // Pretty print the JSON for readability
            String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            System.out.println(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}