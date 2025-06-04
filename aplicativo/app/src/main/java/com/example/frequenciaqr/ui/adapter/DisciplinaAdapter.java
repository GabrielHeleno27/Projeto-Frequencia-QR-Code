package com.example.frequenciaqr.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frequenciaqr.R;
import com.example.frequenciaqr.model.Disciplina;

import java.util.List;

public class DisciplinaAdapter extends RecyclerView.Adapter<DisciplinaAdapter.DisciplinaViewHolder> {
    private List<Disciplina> disciplinas;
    private OnDisciplinaClickListener listener;

    public interface OnDisciplinaClickListener {
        void onDisciplinaClick(Disciplina disciplina);
    }

    public DisciplinaAdapter(List<Disciplina> disciplinas, OnDisciplinaClickListener listener) {
        this.disciplinas = disciplinas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DisciplinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_disciplina, parent, false);
        return new DisciplinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisciplinaViewHolder holder, int position) {
        Disciplina disciplina = disciplinas.get(position);
        holder.bind(disciplina);
    }

    @Override
    public int getItemCount() {
        return disciplinas.size();
    }

    public void atualizarDisciplinas(List<Disciplina> novasDisciplinas) {
        this.disciplinas = novasDisciplinas;
        notifyDataSetChanged();
    }

    class DisciplinaViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNomeDisciplina;
        private TextView txtSemestre;
        private TextView txtProfessor;

        public DisciplinaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeDisciplina = itemView.findViewById(R.id.txtNomeDisciplina);
            txtSemestre = itemView.findViewById(R.id.txtSemestre);
            txtProfessor = itemView.findViewById(R.id.txtProfessor);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDisciplinaClick(disciplinas.get(position));
                }
            });
        }

        public void bind(Disciplina disciplina) {
            txtNomeDisciplina.setText(disciplina.getNome());
            txtSemestre.setText(disciplina.getSemestre());
            txtProfessor.setText(disciplina.getEmailProfessor());
        }
    }
} 