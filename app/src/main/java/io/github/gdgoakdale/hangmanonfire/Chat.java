package io.github.gdgoakdale.hangmanonfire;

public class Chat {
    private String name;
    private String message;
    private String userId;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    public  Chat() {
    }

    public  Chat(String name, String message, String userId) {
        this.name = name;
        this.message = message;
        this.userId = userId;
    }

    public String getName() {
        return this.name;
    }

    public String getMessage() {
        return this.message;
    }

    public String getUserId() { return this.userId; }
}
