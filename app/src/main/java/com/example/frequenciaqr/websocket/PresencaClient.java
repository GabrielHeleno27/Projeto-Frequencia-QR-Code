package com.example.frequenciaqr.websocket;

import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

public class PresencaClient extends WebSocketClient {
    private static final String TAG = "PresencaClient";
    private final PresencaListener presencaListener;
    private final String alunoEmail;
    private final String codigoAula;

    public interface PresencaListener {
        void onPresencaConfirmada();
        void onPresencaRejeitada(String motivo);
    }

    public PresencaClient(String serverUrl, String alunoEmail, String codigoAula, PresencaListener listener) {
        super(URI.create(serverUrl));
        this.alunoEmail = alunoEmail;
        this.codigoAula = codigoAula;
        this.presencaListener = listener;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "Conexão estabelecida com o servidor");
        // Envia os dados do aluno no formato email|codigoAula
        send(alunoEmail + "|" + codigoAula);
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "Mensagem recebida: " + message);
        if (message.equals("PRESENCA_OK")) {
            if (presencaListener != null) {
                presencaListener.onPresencaConfirmada();
            }
        } else {
            if (presencaListener != null) {
                presencaListener.onPresencaRejeitada(message);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "Conexão fechada: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        Log.e(TAG, "Erro na conexão", ex);
        if (presencaListener != null) {
            presencaListener.onPresencaRejeitada("Erro de conexão: " + ex.getMessage());
        }
    }
} 