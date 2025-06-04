package com.example.frequenciaqr.local;

import android.content.Context;
import android.util.Log;
import org.json.JSONObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class PresencaLocal {
    private static final String TAG = "PresencaLocal";
    private static final String FILENAME = "codigo_aula.json";
    private final Context context;

    public interface PresencaListener {
        void onPresencaConfirmada();
        void onError(String message);
    }

    public PresencaLocal(Context context) {
        this.context = context;
    }

    public void registrarCodigoAula(String jsonStr) {
        try {
            File file = new File(context.getFilesDir(), FILENAME);
            FileWriter writer = new FileWriter(file);
            writer.write(jsonStr);
            writer.close();
        } catch (Exception e) {
            Log.e(TAG, "Erro ao registrar código: " + e.getMessage());
        }
    }

    public void verificarPresenca(String qrContent, String emailAluno, PresencaListener listener) {
        try {
            File file = new File(context.getFilesDir(), FILENAME);
            if (!file.exists()) {
                listener.onError("Código inválido ou expirado");
                return;
            }

            FileReader reader = new FileReader(file);
            StringBuilder content = new StringBuilder();
            char[] buffer = new char[1024];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                content.append(buffer, 0, read);
            }
            reader.close();

            JSONObject jsonSalvo = new JSONObject(content.toString());
            JSONObject jsonQR = new JSONObject(qrContent);

            // Verificar se os códigos são iguais
            if (!jsonSalvo.getString("codigo").equals(jsonQR.getString("codigo"))) {
                listener.onError("Código inválido");
                return;
            }

            // Verificar se o código não expirou (5 minutos)
            long timestampSalvo = jsonSalvo.getLong("timestamp");
            long agora = System.currentTimeMillis();
            if ((agora - timestampSalvo) > 5 * 60 * 1000) {
                listener.onError("Código expirado");
                return;
            }

            listener.onPresencaConfirmada();
        } catch (Exception e) {
            Log.e(TAG, "Erro ao verificar presença: " + e.getMessage());
            listener.onError("Erro ao verificar presença: " + e.getMessage());
        }
    }

    public void limparCodigo() {
        File file = new File(context.getFilesDir(), FILENAME);
        if (file.exists()) {
            file.delete();
        }
    }
} 