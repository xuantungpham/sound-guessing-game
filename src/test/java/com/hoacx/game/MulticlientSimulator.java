package test.java.com.hoacx.game;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MulticlientSimulator {

    public static void main(String[] args) throws Exception {

        String wsUrl = "ws://localhost:8080/ws/game";
        int numClients = 5;

        ExecutorService workers = Executors.newFixedThreadPool(numClients);

        for (int i = 1; i <= numClients; i++) {

            final int userId = i;

            workers.submit(() -> {
                try {
                    WebSocketClient client = new WebSocketClient(new URI(wsUrl)) {

                        @Override
                        public void onOpen(ServerHandshake handshake) {
                            System.out.println(" Client " + userId + " connected");
                            send("{\"type\":\"join\",\"payload\":{\"userId\":\"" + userId + "\",\"roomId\":\"1\"}}");
                        }

                        @Override
                        public void onMessage(String message) {
                            System.out.println("User " + userId + " received: " + message);

                            if (message.contains("roundStart")) {
                                send("{\"type\":\"answer\",\"payload\":{\"answerId\":\"A\"}}");
                            }
                        }

                        @Override
                        public void onClose(int code, String reason, boolean remote) {
                            System.out.println("Client " + userId + " disconnected");
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    };

                    client.connectBlocking();
                    Thread.sleep(20000); 

                    client.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        workers.shutdown();
    }
}
