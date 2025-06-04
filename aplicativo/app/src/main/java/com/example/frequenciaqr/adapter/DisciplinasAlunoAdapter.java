package com.example.frequenciaqr.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frequenciaqr.R;
import com.example.frequenciaqr.model.Disciplina;
import com.example.frequenciaqr.ui.aluno.DetalhesFrequenciaActivity;
import java.util.List;

public class DisciplinasAlunoAdapter extends RecyclerView.Adapter<DisciplinasAlunoAdapter.ViewHolder> {
    private List<Disciplina> disciplinas;

    public DisciplinasAlunoAdapter(List<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_disciplina_aluno, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Disciplina disciplina = disciplinas.get(position);
        holder.txtNomeDisciplina.setText(disciplina.getNome());
        holder.txtProfessor.setText("Professor: " + disciplina.getProfessor());
        holder.txtPresencas.setText("Presenças: " + disciplina.getPresencas() + " aulas");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetalhesFrequenciaActivity.class);
            intent.putExtra("disciplina_id", disciplina.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return disciplinas.size();
    }

    public void atualizarDisciplinas(List<Disciplina> novasDisciplinas) {
        this.disciplinas = novasDisciplinas;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeDisciplina;
        TextView txtProfessor;
        TextView txtPresencas;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeDisciplina = itemView.findViewById(R.id.txtNomeDisciplina);
            txtProfessor = itemView.findViewById(R.id.txtProfessor);
            txtPresencas = itemView.findViewById(R.id.txtPresencas);
        }
    }
} 