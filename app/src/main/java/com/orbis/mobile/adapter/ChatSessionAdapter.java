package com.orbis.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orbis.mobile.R;
import com.orbis.mobile.model.ChatSession;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatSessionAdapter extends RecyclerView.Adapter<ChatSessionAdapter.SessionViewHolder>{

    public interface OnSessionClickListener{
        void onSessionClick(ChatSession session);
        void onSessionDelete(ChatSession session);
    }

    private final List<ChatSession> sessions;
    private final OnSessionClickListener listener;

    public ChatSessionAdapter(List<ChatSession> sessions, OnSessionClickListener listener) {
        this.sessions = sessions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        ChatSession session = sessions.get(position);
        holder.txtTitle.setText(session.getTitle());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.txtDate.setText(sdf.format(new Date(session.getTimestamp())));

        holder.itemView.setOnClickListener(v -> listener.onSessionClick(session));
        holder.btnDelete.setOnClickListener(v -> listener.onSessionDelete(session));
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        TextView txtDate;
        ImageButton btnDelete;

        SessionViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtSessionTitle);
            txtDate = itemView.findViewById(R.id.txtSessionDate);
            btnDelete = itemView.findViewById(R.id.btnDeleteSession);
        }
    }
}
