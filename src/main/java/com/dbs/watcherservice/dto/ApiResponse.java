package com.dbs.watcherservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse {

    private Results results;

    // Getters and Setters
    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }

    public static class Results {
        @JsonProperty("A")
        private FrameData A;

        // Getters and Setters
        public FrameData getA() {
            return A;
        }

        public void setA(FrameData a) {
            A = a;
        }
    }

    public static class FrameData {
        private String status;
        private Frames frames;

        // Getters and Setters
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Frames getFrames() {
            return frames;
        }

        public void setFrames(Frames frames) {
            this.frames = frames;
        }
    }

    public static class Frames {
        private Schema schema;
        private Data data;

        // Getters and Setters
        public Schema getSchema() {
            return schema;
        }

        public void setSchema(Schema schema) {
            this.schema = schema;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }

    public static class Schema {
        private String refId;
        private Map<String, Object> meta; // Generic Map for metadata
        private List<Field> fields;

        // Getters and Setters
        public String getRefId() {
            return refId;
        }

        public void setRefId(String refId) {
            this.refId = refId;
        }

        public Map<String, Object> getMeta() {
            return meta;
        }

        public void setMeta(Map<String, Object> meta) {
            this.meta = meta;
        }

        public List<Field> getFields() {
            return fields;
        }

        public void setFields(List<Field> fields) {
            this.fields = fields;
        }
    }

    public static class Field {
        private String name;
        private Map<String, Object> type; // Generic Map for type details

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, Object> getType() {
            return type;
        }

        public void setType(Map<String, Object> type) {
            this.type = type;
        }
    }

    public static class Data {
        private List<List<String>> values;

        // Getters and Setters
        public List<List<String>> getValues() {
            return values;
        }

        public void setValues(List<List<String>> values) {
            this.values = values;
        }
    }
}
