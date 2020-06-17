package com.shivam.keepnote.note_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shivam.keepnote.R;
import com.shivam.keepnote.database.Note;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.NoteHolder> {

    private List<Note> notes = new ArrayList<>();

    private onItemClickListener clickListener;

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,parent,false);
        return new NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = notes.get(position);
        holder.textViewName.setText(currentNote.getTitle());
        holder.textViewEmail.setText(currentNote.getDesciption());
        holder.textViewPriority.setText(String.valueOf(currentNote.getPriority()));

    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NoteHolder extends RecyclerView.ViewHolder{
        private TextView textViewName;
        private TextView textViewEmail;
        private TextView textViewPriority;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewEmail = itemView.findViewById(R.id.text_view_email);
            textViewPriority = itemView.findViewById(R.id.text_view_priority);

            ImageButton DELETE = itemView.findViewById(R.id.delete_item);
            DELETE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onDeleteItem(notes.get(getAdapterPosition()));
                }
            });

        }
    }

    public interface onItemClickListener{
        void onDeleteItem(Note note);
//        void deleteAllItem();
    }

    public void setOnClickListener(onItemClickListener listener){
        this.clickListener = listener;
    }

}
