package com.example.frequenciaqr.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frequenciaqr.R;
import com.example.frequenciaqr.model.Disciplina;
import java.util.List;

public class DisciplinasAdapter extends RecyclerView.Adapter<DisciplinasAdapter.DisciplinaViewHolder> {
    private List<Disciplina> disciplinas;
    private OnDisciplinaClickListener clickListener;

    public interface OnDisciplinaClickListener {
        void onDisciplinaClick(Disciplina disciplina);
    }

    public DisciplinasAdapter(List<Disciplina> disciplinas, OnDisciplinaClickListener clickListener) {
        this.disciplinas = disciplinas;
        this.clickListener = clickListener;
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
        holder.txtNomeDisciplina.setText(disciplina.getNome());
        holder.txtSemestre.setText(disciplina.getSemestre());
        holder.txtProfessor.setText(disciplina.getEmailProfessor());
        holder.itemView.setOnClickListener(v -> clickListener.onDisciplinaClick(disciplina));
    }

    @Override
    public int getItemCount() {
        return disciplinas.size();
    }

    public void atualizarDisciplinas(List<Disciplina> novasDisciplinas) {
        this.disciplinas = novasDisciplinas;
        notifyDataSetChanged();
    }

    public Disciplina getDisciplina(int position) {
        if (position >= 0 && position < disciplinas.size()) {
            return disciplinas.get(position);
        }
        return null;
    }

    static class DisciplinaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeDisciplina;
        TextView txtSemestre;
        TextView txtProfessor;

        DisciplinaViewHolder(View itemView) {
            super(itemView);
            txtNomeDisciplina = itemView.findViewById(R.id.txtNomeDisciplina);
            txtSemestre = itemView.findViewById(R.id.txtSemestre);
            txtProfessor = itemView.findViewById(R.id.txtProfessor);
        }
    }
} 