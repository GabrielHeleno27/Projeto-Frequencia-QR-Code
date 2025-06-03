package com.example.frequenciaqr.ui.fragments;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.frequenciaqr.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class QRCodeDialogFragment extends DialogFragment {
    private static final String ARG_QR_BITMAP = "qr_bitmap";
    private Bitmap qrBitmap;

    public static QRCodeDialogFragment newInstance(Bitmap bitmap) {
        QRCodeDialogFragment fragment = new QRCodeDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_QR_BITMAP, bitmap);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            qrBitmap = (Bitmap) getArguments().getParcelable(ARG_QR_BITMAP);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_qr_code, null);
        ImageView imageViewQR = view.findViewById(R.id.imageViewQR);
        imageViewQR.setImageBitmap(qrBitmap);

        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.titulo_qr_code)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
} 