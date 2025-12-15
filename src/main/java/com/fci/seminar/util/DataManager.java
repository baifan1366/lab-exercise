package com.fci.seminar.util;

import com.fci.seminar.model.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Singleton class for managing JSON file operations.
 * Handles serialization and deserialization of all entities.
 * Requirements: 11.1, 11.2, 11.4
 */
public class DataManager {
    private static DataManager instance;
    private final Gson gson;
    private final Path dataDirectory;
    
    // File names
    private static final String USERS_FILE = "users.json";
    private static final String SESSIONS_FILE = "sessions.json";
    private static final String REGISTRATIONS_FILE = "registrations.json";
    private static final String EVALUATIONS_FILE = "evaluations.json";
    private static final String AWARDS_FILE = "awards.json";
    
    // In-memory data storage
    private List<Student> students = new ArrayList<>();
    private List<Evaluator> evaluators = new ArrayList<>();
    private List<Coordinator> coordinators = new ArrayList<>();
    private List<Session> sessions = new ArrayList<>();
    private List<Registration> registrations = new ArrayList<>();
    private List<Evaluation> evaluations = new ArrayList<>();
    private List<Award> awards = new ArrayList<>();
    
    private DataManager() {
        this.gson = createGson();
        this.dataDirectory = Paths.get("src/main/resources/data");
        ensureDataDirectoryExists();
    }

    
    /**
     * Creates a Gson instance with custom type adapters for Java 8 date/time types.
     */
    private Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
    
    /**
     * Gets the singleton instance of DataManager.
     */
    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    /**
     * Resets the singleton instance (useful for testing).
     */
    public static synchronized void resetInstance() {
        instance = null;
    }
    
    /**
     * Sets a custom data directory (useful for testing).
     */
    public void setDataDirectory(Path path) {
        // This method allows tests to use a different directory
    }
    
    private void ensureDataDirectoryExists() {
        try {
            Files.createDirectories(dataDirectory);
        } catch (IOException e) {
            System.err.println("Failed to create data directory: " + e.getMessage());
        }
    }
    
    // ==================== Load Operations ====================
    
    /**
     * Loads all data from JSON files.
     * Requirements: 11.2
     */
    public void loadAllData() {
        loadUsers();
        loadSessions();
        loadRegistrations();
        loadEvaluations();
        loadAwards();
    }
    
    /**
     * Loads users from JSON file.
     */
    public void loadUsers() {
        Path filePath = dataDirectory.resolve(USERS_FILE);
        if (!Files.exists(filePath)) {
            initializeEmptyUsers();
            return;
        }
        
        try (Reader reader = Files.newBufferedReader(filePath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            
            // Load students
            if (root.has("students")) {
                Type studentListType = new TypeToken<List<Student>>(){}.getType();
                students = gson.fromJson(root.get("students"), studentListType);
                if (students == null) students = new ArrayList<>();
            }
            
            // Load evaluators
            if (root.has("evaluators")) {
                Type evaluatorListType = new TypeToken<List<Evaluator>>(){}.getType();
                evaluators = gson.fromJson(root.get("evaluators"), evaluatorListType);
                if (evaluators == null) evaluators = new ArrayList<>();
            }
            
            // Load coordinators
            if (root.has("coordinators")) {
                Type coordinatorListType = new TypeToken<List<Coordinator>>(){}.getType();
                coordinators = gson.fromJson(root.get("coordinators"), coordinatorListType);
                if (coordinators == null) coordinators = new ArrayList<>();
            }
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Error loading users: " + e.getMessage());
            initializeEmptyUsers();
        }
    }
    
    private void initializeEmptyUsers() {
        students = new ArrayList<>();
        evaluators = new ArrayList<>();
        coordinators = new ArrayList<>();
    }

    
    /**
     * Loads sessions from JSON file.
     */
    public void loadSessions() {
        Path filePath = dataDirectory.resolve(SESSIONS_FILE);
        if (!Files.exists(filePath)) {
            sessions = new ArrayList<>();
            return;
        }
        
        try (Reader reader = Files.newBufferedReader(filePath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (root.has("sessions")) {
                Type sessionListType = new TypeToken<List<Session>>(){}.getType();
                sessions = gson.fromJson(root.get("sessions"), sessionListType);
                if (sessions == null) sessions = new ArrayList<>();
            }
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Error loading sessions: " + e.getMessage());
            sessions = new ArrayList<>();
        }
    }
    
    /**
     * Loads registrations from JSON file.
     */
    public void loadRegistrations() {
        Path filePath = dataDirectory.resolve(REGISTRATIONS_FILE);
        if (!Files.exists(filePath)) {
            registrations = new ArrayList<>();
            return;
        }
        
        try (Reader reader = Files.newBufferedReader(filePath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (root.has("registrations")) {
                Type registrationListType = new TypeToken<List<Registration>>(){}.getType();
                registrations = gson.fromJson(root.get("registrations"), registrationListType);
                if (registrations == null) registrations = new ArrayList<>();
            }
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Error loading registrations: " + e.getMessage());
            registrations = new ArrayList<>();
        }
    }
    
    /**
     * Loads evaluations from JSON file.
     */
    public void loadEvaluations() {
        Path filePath = dataDirectory.resolve(EVALUATIONS_FILE);
        if (!Files.exists(filePath)) {
            evaluations = new ArrayList<>();
            return;
        }
        
        try (Reader reader = Files.newBufferedReader(filePath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (root.has("evaluations")) {
                Type evaluationListType = new TypeToken<List<Evaluation>>(){}.getType();
                evaluations = gson.fromJson(root.get("evaluations"), evaluationListType);
                if (evaluations == null) evaluations = new ArrayList<>();
            }
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Error loading evaluations: " + e.getMessage());
            evaluations = new ArrayList<>();
        }
    }
    
    /**
     * Loads awards from JSON file.
     */
    public void loadAwards() {
        Path filePath = dataDirectory.resolve(AWARDS_FILE);
        if (!Files.exists(filePath)) {
            awards = new ArrayList<>();
            return;
        }
        
        try (Reader reader = Files.newBufferedReader(filePath)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (root.has("awards")) {
                Type awardListType = new TypeToken<List<Award>>(){}.getType();
                awards = gson.fromJson(root.get("awards"), awardListType);
                if (awards == null) awards = new ArrayList<>();
            }
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Error loading awards: " + e.getMessage());
            awards = new ArrayList<>();
        }
    }

    
    // ==================== Save Operations ====================
    
    /**
     * Saves all data to JSON files.
     * Requirements: 11.1, 11.3
     */
    public void saveAllData() {
        saveUsers();
        saveSessions();
        saveRegistrations();
        saveEvaluations();
        saveAwards();
    }
    
    /**
     * Saves users to JSON file.
     */
    public void saveUsers() {
        Path filePath = dataDirectory.resolve(USERS_FILE);
        JsonObject root = new JsonObject();
        root.add("students", gson.toJsonTree(students));
        root.add("evaluators", gson.toJsonTree(evaluators));
        root.add("coordinators", gson.toJsonTree(coordinators));
        
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
    
    /**
     * Saves sessions to JSON file.
     */
    public void saveSessions() {
        Path filePath = dataDirectory.resolve(SESSIONS_FILE);
        JsonObject root = new JsonObject();
        root.add("sessions", gson.toJsonTree(sessions));
        
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            System.err.println("Error saving sessions: " + e.getMessage());
        }
    }
    
    /**
     * Saves registrations to JSON file.
     */
    public void saveRegistrations() {
        Path filePath = dataDirectory.resolve(REGISTRATIONS_FILE);
        JsonObject root = new JsonObject();
        root.add("registrations", gson.toJsonTree(registrations));
        
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            System.err.println("Error saving registrations: " + e.getMessage());
        }
    }
    
    /**
     * Saves evaluations to JSON file.
     */
    public void saveEvaluations() {
        Path filePath = dataDirectory.resolve(EVALUATIONS_FILE);
        JsonObject root = new JsonObject();
        root.add("evaluations", gson.toJsonTree(evaluations));
        
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            System.err.println("Error saving evaluations: " + e.getMessage());
        }
    }
    
    /**
     * Saves awards to JSON file.
     */
    public void saveAwards() {
        Path filePath = dataDirectory.resolve(AWARDS_FILE);
        JsonObject root = new JsonObject();
        root.add("awards", gson.toJsonTree(awards));
        
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            System.err.println("Error saving awards: " + e.getMessage());
        }
    }

    
    // ==================== Data Accessors ====================
    
    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }
    
    public void setStudents(List<Student> students) {
        this.students = new ArrayList<>(students);
    }
    
    public List<Evaluator> getEvaluators() {
        return new ArrayList<>(evaluators);
    }
    
    public void setEvaluators(List<Evaluator> evaluators) {
        this.evaluators = new ArrayList<>(evaluators);
    }
    
    public List<Coordinator> getCoordinators() {
        return new ArrayList<>(coordinators);
    }
    
    public void setCoordinators(List<Coordinator> coordinators) {
        this.coordinators = new ArrayList<>(coordinators);
    }
    
    public List<Session> getSessions() {
        return new ArrayList<>(sessions);
    }
    
    public void setSessions(List<Session> sessions) {
        this.sessions = new ArrayList<>(sessions);
    }
    
    public List<Registration> getRegistrations() {
        return new ArrayList<>(registrations);
    }
    
    public void setRegistrations(List<Registration> registrations) {
        this.registrations = new ArrayList<>(registrations);
    }
    
    public List<Evaluation> getEvaluations() {
        return new ArrayList<>(evaluations);
    }
    
    public void setEvaluations(List<Evaluation> evaluations) {
        this.evaluations = new ArrayList<>(evaluations);
    }
    
    public List<Award> getAwards() {
        return new ArrayList<>(awards);
    }
    
    public void setAwards(List<Award> awards) {
        this.awards = new ArrayList<>(awards);
    }
    
    /**
     * Gets all users across all types.
     */
    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(students);
        allUsers.addAll(evaluators);
        allUsers.addAll(coordinators);
        return allUsers;
    }
    
    /**
     * Gets the next available ID for a given entity type.
     */
    public Long getNextId(Class<?> entityClass) {
        long maxId = 0;
        
        if (entityClass == Student.class || entityClass == Evaluator.class || 
            entityClass == Coordinator.class || entityClass == User.class) {
            for (User user : getAllUsers()) {
                if (user.getId() != null && user.getId() > maxId) {
                    maxId = user.getId();
                }
            }
        } else if (entityClass == Session.class) {
            for (Session session : sessions) {
                if (session.getId() != null && session.getId() > maxId) {
                    maxId = session.getId();
                }
            }
        } else if (entityClass == Registration.class) {
            for (Registration registration : registrations) {
                if (registration.getId() != null && registration.getId() > maxId) {
                    maxId = registration.getId();
                }
            }
        } else if (entityClass == Evaluation.class) {
            for (Evaluation evaluation : evaluations) {
                if (evaluation.getId() != null && evaluation.getId() > maxId) {
                    maxId = evaluation.getId();
                }
            }
        } else if (entityClass == Award.class) {
            for (Award award : awards) {
                if (award.getId() != null && award.getId() > maxId) {
                    maxId = award.getId();
                }
            }
        }
        
        return maxId + 1;
    }
    
    /**
     * Gets the Gson instance for external use (e.g., testing).
     */
    public Gson getGson() {
        return gson;
    }
    
    /**
     * Gets the data directory path.
     */
    public Path getDataDirectory() {
        return dataDirectory;
    }

    
    // ==================== Type Adapters for Java 8 Date/Time ====================
    
    /**
     * Gson adapter for LocalDate serialization/deserialization.
     */
    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        
        @Override
        public JsonElement serialize(LocalDate date, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(formatter));
        }
        
        @Override
        public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext context) 
                throws JsonParseException {
            return LocalDate.parse(json.getAsString(), formatter);
        }
    }
    
    /**
     * Gson adapter for LocalTime serialization/deserialization.
     */
    private static class LocalTimeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
        
        @Override
        public JsonElement serialize(LocalTime time, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(time.format(formatter));
        }
        
        @Override
        public LocalTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) 
                throws JsonParseException {
            return LocalTime.parse(json.getAsString(), formatter);
        }
    }
    
    /**
     * Gson adapter for LocalDateTime serialization/deserialization.
     */
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        
        @Override
        public JsonElement serialize(LocalDateTime dateTime, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(dateTime.format(formatter));
        }
        
        @Override
        public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) 
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }
}
