package com.example.frequenciaqr.network;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class PresencaServer extends WebSocketServer {
    private static final String TAG = "PresencaServer";
    private final String codigoAula;
    private final Context context;

    public PresencaServer(int port, String codigoAula, Context context) {
        super(new InetSocketAddress(port));
        this.codigoAula = codigoAula;
        this.context = context;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.d(TAG, "Nova conexão estabelecida");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.d(TAG, "Conexão fechada");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.d(TAG, "Mensagem recebida: " + message);
        
        // Verificar se a mensagem corresponde ao código da aula
        if (message.equals(codigoAula)) {
            conn.send("PRESENCA_CONFIRMADA");
        } else {
            conn.send("CODIGO_INVALIDO");
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.e(TAG, "Erro no servidor WebSocket", ex);
    }

    @Override
    public void onStart() {
        Log.d(TAG, "Servidor WebSocket iniciado na porta: " + getPort());
    }

    public static String getLocalIpAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<java.net.InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (java.net.InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress() && addr.getHostAddress().indexOf(':') == -1) {
                        String sAddr = addr.getHostAddress();
                        if (sAddr.startsWith("192.168.") || sAddr.startsWith("10.") || sAddr.startsWith("172.")) {
                            return sAddr;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao obter endereço IP", e);
        }
        return null;
    }
} 