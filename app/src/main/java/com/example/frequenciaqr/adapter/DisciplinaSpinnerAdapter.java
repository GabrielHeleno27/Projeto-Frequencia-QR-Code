package com.example.frequenciaqr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.frequenciaqr.model.Disciplina;

import java.util.List;

public class DisciplinaSpinnerAdapter extends ArrayAdapter<Disciplina> {
    private final LayoutInflater inflater;

    public DisciplinaSpinnerAdapter(Context context, List<Disciplina> disciplinas) {
        super(context, android.R.layout.simple_spinner_item, disciplinas);
        inflater = LayoutInflater.from(context);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView text = view.findViewById(android.R.id.text1);
        Disciplina disciplina = getItem(position);
        if (disciplina != null) {
            text.setText(String.format("%s (%s)", disciplina.getNome(), disciplina.getSemestre()));
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView text = view.findViewById(android.R.id.text1);
        Disciplina disciplina = getItem(position);
        if (disciplina != null) {
            text.setText(String.format("%s (%s)", disciplina.getNome(), disciplina.getSemestre()));
        }

        return view;
    }
} 