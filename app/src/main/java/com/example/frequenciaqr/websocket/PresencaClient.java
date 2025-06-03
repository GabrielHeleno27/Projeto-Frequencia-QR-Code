package com.example.frequenciaqr.websocket;

import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;

public class PresencaClient extends WebSocketListener {
    private static final String TAG = "PresencaClient";
    private WebSocket webSocket;
    private final String serverUrl;
    private final String emailAluno;
    private final String codigoAula;
    private final PresencaListener listener;
    private final OkHttpClient client;

    public interface PresencaListener {
        void onPresencaConfirmada();
        void onError(String message);
    }

    public PresencaClient(String serverUrl, String emailAluno, String codigoAula, PresencaListener listener) {
        this.serverUrl = serverUrl;
        this.emailAluno = emailAluno;
        this.codigoAula = codigoAula;
        this.listener = listener;

        this.client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();
    }

    public void connect() {
        Request request = new Request.Builder()
            .url(serverUrl)
            .build();
        webSocket = client.newWebSocket(request, this);
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Fechando conexão");
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        try {
            JSONObject message = new JSONObject();
            message.put("tipo", "registrar_presenca");
            message.put("email_aluno", emailAluno);
            message.put("codigo_aula", codigoAula);
            
            webSocket.send(message.toString());
        } catch (Exception e) {
            Log.e(TAG, "Erro ao enviar mensagem: " + e.getMessage());
            listener.onError("Erro ao enviar dados de presença");
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            JSONObject response = new JSONObject(text);
            String tipo = response.getString("tipo");
            boolean sucesso = response.getBoolean("sucesso");
            String mensagem = response.getString("mensagem");

            if ("confirmacao_presenca".equals(tipo)) {
                if (sucesso) {
                    listener.onPresencaConfirmada();
                } else {
                    listener.onError(mensagem);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar mensagem: " + e.getMessage());
            listener.onError("Erro ao processar resposta do servidor");
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "Erro na conexão WebSocket: " + t.getMessage());
        listener.onError("Erro na conexão com o servidor");
    }
} 