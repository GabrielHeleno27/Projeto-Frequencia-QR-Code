package com.example.frequenciaqr.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.frequenciaqr.R;
import com.example.frequenciaqr.model.Usuario;
import java.util.List;

public class AlunosAdapter extends RecyclerView.Adapter<AlunosAdapter.AlunoViewHolder> {
    private List<Usuario> alunos;

    public AlunosAdapter(List<Usuario> alunos) {
        this.alunos = alunos;
    }

    @NonNull
    @Override
    public AlunoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new AlunoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlunoViewHolder holder, int position) {
        Usuario aluno = alunos.get(position);
        holder.txtNomeAluno.setText(aluno.getNome());
    }

    @Override
    public int getItemCount() {
        return alunos.size();
    }

    static class AlunoViewHolder extends RecyclerView.ViewHolder {
        TextView txtNomeAluno;

        AlunoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNomeAluno = itemView.findViewById(android.R.id.text1);
        }
    }
} 