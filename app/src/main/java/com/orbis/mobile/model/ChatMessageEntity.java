package com.orbis.mobile.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_messages")
public class ChatMessageEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int sessionId;
    private String role;
    private String content;
    private long timestamp;

    public ChatMessageEntity(int sessionId, String role, String content, long timestamp) {
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
