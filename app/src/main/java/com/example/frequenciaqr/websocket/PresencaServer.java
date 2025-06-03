package com.example.frequenciaqr.websocket;

import android.util.Log;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PresencaServer extends WebSocketServer {
    private static final String TAG = "PresencaServer";
    private final Map<WebSocket, String> connectedClients = new HashMap<>();
    private final String codigoAula;
    private final PresencaListener presencaListener;
    private boolean isRunning = false;

    public interface PresencaListener {
        void onPresencaRegistrada(String alunoEmail);
    }

    public PresencaServer(int port, String codigoAula, PresencaListener listener) {
        super(new InetSocketAddress(port));
        this.codigoAula = codigoAula;
        this.presencaListener = listener;
        setReuseAddr(true);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.d(TAG, "Nova conexão estabelecida");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connectedClients.remove(conn);
        Log.d(TAG, "Conexão fechada");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            // Formato esperado: email|codigoAula
            String[] parts = message.split("\\|");
            if (parts.length == 2 && parts[1].equals(codigoAula)) {
                String alunoEmail = parts[0];
                connectedClients.put(conn, alunoEmail);
                conn.send("PRESENCA_OK");
                
                if (presencaListener != null) {
                    presencaListener.onPresencaRegistrada(alunoEmail);
                }
            } else {
                conn.send("CODIGO_INVALIDO");
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao processar mensagem", e);
            conn.send("ERRO");
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.e(TAG, "Erro no servidor", ex);
        if (conn != null) {
            connectedClients.remove(conn);
        }
    }

    @Override
    public void onStart() {
        isRunning = true;
        Log.d(TAG, "Servidor iniciado na porta: " + getPort());
    }

    @Override
    public void start() {
        if (!isRunning) {
            super.start();
        }
    }

    @Override
    public void stop() throws InterruptedException {
        isRunning = false;
        try {
            // Fecha todas as conexões ativas
            for (WebSocket conn : new HashMap<>(connectedClients).keySet()) {
                try {
                    conn.close();
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao fechar conexão", e);
                }
            }
            connectedClients.clear();
            
            // Para o servidor
            super.stop();
            
        } catch (Exception e) {
            Log.e(TAG, "Erro ao parar servidor", e);
            throw new InterruptedException("Erro ao parar servidor: " + e.getMessage());
        }
    }

    public static String getLocalIpAddress() {
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (java.net.InetAddress address : Collections.list(ni.getInetAddresses())) {
                    if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(':') == -1) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao obter IP local", e);
        }
        return null;
    }

    public boolean isRunning() {
        return isRunning;
    }
} 