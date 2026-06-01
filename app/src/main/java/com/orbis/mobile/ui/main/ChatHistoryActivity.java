package com.orbis.mobile.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orbis.mobile.R;
import com.orbis.mobile.adapter.ChatSessionAdapter;
import com.orbis.mobile.model.AppDatabase;
import com.orbis.mobile.model.ChatDao;
import com.orbis.mobile.model.ChatSession;

import java.util.ArrayList;
import java.util.List;

public class ChatHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerSessions;
    private ChatSessionAdapter adapter;
    private final List<ChatSession> sessions = new ArrayList<>();
    private ChatDao chatDao;
    private View layoutEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        chatDao = AppDatabase.getInstance(this).chatDao();

        MaterialToolbar toolbar = findViewById(R.id.toolbarHistory);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        layoutEmpty = findViewById(R.id.layoutEmpty);
        recyclerSessions = findViewById(R.id.recyclerSessions);

        adapter = new ChatSessionAdapter(sessions, new ChatSessionAdapter.OnSessionClickListener() {
            @Override
            public void onSessionClick(ChatSession session) {
                openSession(session);
            }

            @Override
            public void onSessionDelete(ChatSession session) {
                confirmDelete(session);
            }
        });

        recyclerSessions.setLayoutManager(new LinearLayoutManager(this));
        recyclerSessions.setAdapter(adapter);

        FloatingActionButton fabNew = findViewById(R.id.fabNewChat);
        fabNew.setOnClickListener(v -> openNewChat());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSessions();
    }

    private void loadSessions() {
        sessions.clear();
        sessions.addAll(chatDao.getAllSessions());
        adapter.notifyDataSetChanged();

        boolean isEmpty = sessions.isEmpty();
        layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerSessions.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void openSession(ChatSession session) {
        Intent intent = new Intent(this, IaActivity.class);
        intent.putExtra(IaActivity.EXTRA_SESSION_ID, session.getId());
        intent.putExtra(IaActivity.EXTRA_SESSION_TITLE, session.getTitle());
        startActivity(intent);
    }

    private void openNewChat() {
        Intent intent = new Intent(this, IaActivity.class);
        // sem EXTRA_SESSION_ID = nova sessão
        startActivity(intent);
    }

    private void confirmDelete(ChatSession session) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir conversa")
                .setMessage("Tem certeza que deseja excluir esta conversa?")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    chatDao.deleteMessagesBySession(session.getId());
                    chatDao.deleteSession(session.getId());
                    loadSessions();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}