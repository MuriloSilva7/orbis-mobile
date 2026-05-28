package com.orbis.mobile.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatDao {
    @Insert
    long insertSession(ChatSession session);

    @Query("SELECT * FROM chat_sessions ORDER BY timestamp DESC")
    List<ChatSession> getAllSessions();

    @Insert
    void insertMessage(ChatMessageEntity message);

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    List<ChatMessageEntity> getMessagesBySession(int sessionId);

    @Query("DELETE FROM chat_sessions WHERE id = :sessionId")
    void deleteSession(int sessionId);

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    void deleteMessagesBySession(int sessionId);
}
