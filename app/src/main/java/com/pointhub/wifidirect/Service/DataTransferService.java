package com.pointhub.wifidirect.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class DataTransferService extends IntentService {

    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_DATA = "com.example.android.wifidirect.SEND_DATA";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "sd_go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "sd_go_port";
    public static final String MESSAGE = "msg";

    public DataTransferService(String name) {
        super(name);
    }

    public DataTransferService() {
        super("DataTransferService");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {


        showToast("Inside onHandleIntent.");

        Context context = getApplicationContext();

        if (intent.getAction().equals(ACTION_SEND_DATA)) {

            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);

            Socket socket = new Socket();

            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

            try {

                Log.d("bizzmark", "Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                Log.d("bizzmark", "Client socket - " + socket.isConnected());

                /*returns an output stream to write data into this socket*/
                OutputStream stream = socket.getOutputStream();

                String message = intent.getExtras().getString(MESSAGE);

                stream.write(message.getBytes());



            } catch (IOException e) {

                Log.e("bizzmark", e.getMessage());
            } finally {

                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }


    public void showToast(String message) {
        final String msg = message;
        new Handler(Looper.getMainLooper())
                .post(
                        new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            }
                        });
    }
}