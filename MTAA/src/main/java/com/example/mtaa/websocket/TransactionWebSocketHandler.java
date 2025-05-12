package com.example.mtaa.websocket;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.example.mtaa.event.TransactionChangedEvent;
import com.example.mtaa.model.Transaction;
import com.example.mtaa.model.User;
import com.example.mtaa.repository.UserRepository;
import com.example.mtaa.service.TransactionService;
import com.example.mtaa.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TransactionWebSocketHandler {

    private final SocketIOServer server;
    private final TransactionService transactionService;
    private final UserRepository userRepository;
    private final SocketIOServer socketIOServer;
    private final ObjectMapper objectMapper;

    private final Map<Long, Map<String, SocketIOClient>> userClients = new ConcurrentHashMap<>();

    @Autowired
    public TransactionWebSocketHandler(SocketIOServer server, TransactionService transactionService, UserRepository userRepository, SocketIOServer socketIOServer, ObjectMapper objectMapper) {
        this.server = server;
        this.transactionService = transactionService;
        this.userRepository = userRepository;
        this.socketIOServer = socketIOServer;
        this.objectMapper = objectMapper;

        try {
            server.start();
            System.out.println("Socket.IO server started successfully on port " + server.getConfiguration().getPort());
        } catch (Exception e) {
            System.err.println("Failed to start Socket.IO server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        String token = getToken(client);

        if (token != null && JwtUtil.validateToken(token)) {
            Long userId = JwtUtil.extractUserId(token);
            String sessionId = client.getSessionId().toString();

            userClients.computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                    .put(sessionId, client);

            System.out.println("Client connected: " + sessionId + " for user " + userId);
        } else {
            client.disconnect();
        }
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String sessionId = client.getSessionId().toString();

        // Odstránenie klienta zo všetkých máp
        userClients.values().forEach(clients -> clients.remove(sessionId));

        System.out.println("Client disconnected: " + sessionId);
    }

    @OnEvent("subscribe_transactions")
    public void onSubscribeTransactions(SocketIOClient client, AckRequest ackRequest) {
        String token = getToken(client);
        String username = JwtUtil.extractUsername(token);
        List<Transaction> transactions = transactionService.getAllTransactions(username);

        try {
            ArrayNode transactionsArray = objectMapper.createArrayNode();

            for (Transaction transaction : transactions) {
                ObjectNode transactionNode = objectMapper.createObjectNode();

                transactionNode.put("id", transaction.getId());
                transactionNode.put("label", transaction.getLabel());
                transactionNode.put("amount", transaction.getAmount());

                if (transaction.getCreationDate() != null) {
                    transactionNode.put("creationDate",
                            transaction.getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }

                if (transaction.getTransactionTypeEnum() != null) {
                    transactionNode.put("transactionTypeEnum", transaction.getTransactionTypeEnum().name());
                }

                if (transaction.getFrequencyEnum() != null) {
                    transactionNode.put("frequencyEnum", transaction.getFrequencyEnum().name());
                }

                if (transaction.getNote() != null) {
                    transactionNode.put("note", transaction.getNote());
                }

                if (transaction.getFilename() != null) {
                    transactionNode.put("filename", transaction.getFilename());
                }

                if (transaction.getUser() != null) {
                    ObjectNode userNode = objectMapper.createObjectNode();
                    userNode.put("id", transaction.getUser().getId());
                    userNode.put("username", transaction.getUser().getUsername());
                    transactionNode.set("user", userNode);
                }

                if (transaction.getCategory() != null) {
                    ObjectNode categoryNode = objectMapper.createObjectNode();
                    categoryNode.put("id", transaction.getCategory().getId());
                    categoryNode.put("name", transaction.getCategory().getLabel());
                    transactionNode.set("category", categoryNode);
                }

                if (transaction.getBudget() != null) {
                    ObjectNode budgetNode = objectMapper.createObjectNode();
                    budgetNode.put("id", transaction.getBudget().getId());
                    budgetNode.put("name", transaction.getBudget().getLabel());
                    transactionNode.set("budget", budgetNode);
                }

                if (transaction.getLocation() != null) {
                    ObjectNode locationNode = objectMapper.createObjectNode();
                    locationNode.put("id", transaction.getLocation().getId());
                    locationNode.put("name", transaction.getLocation().getName());
                    transactionNode.set("location", locationNode);
                }

                transactionsArray.add(transactionNode);
            }

            client.sendEvent("transactions_update", transactionsArray);
            System.out.println("Transactions update sent");
        } catch (Exception e) {
            System.err.println("Error sending transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @EventListener
    public void handleTransactionChangedEvent(TransactionChangedEvent event) {
        notifyTransactionUpdates(event.getUserId());
    }

    private String getToken(SocketIOClient client) {
        if (client.getHandshakeData().getSingleUrlParam("token") != null) {
            return client.getHandshakeData().getSingleUrlParam("token");
        }

        if (client.getHandshakeData().getHttpHeaders().contains("Authorization")) {
            String authHeader = client.getHandshakeData().getHttpHeaders().get("Authorization");
            if (authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }

        return null;
    }

    private String getUsernameByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(User::getUsername).orElse(null);
    }

    private void notifyTransactionUpdates(Long userId) {
        Map<String, SocketIOClient> clients = userClients.get(userId);
        if (clients == null || clients.isEmpty()) {
            return;
        }

        String username = getUsernameByUserId(userId);
        if (username == null) {
            return;
        }

        List<Transaction> transactions = transactionService.getAllTransactions(username);
        try {
            ArrayNode transactionsArray = objectMapper.createArrayNode();

            for (Transaction transaction : transactions) {
                ObjectNode transactionNode = objectMapper.createObjectNode();

                transactionNode.put("id", transaction.getId());
                transactionNode.put("label", transaction.getLabel());
                transactionNode.put("amount", transaction.getAmount());

                if (transaction.getCreationDate() != null) {
                    transactionNode.put("creationDate",
                            transaction.getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }

                if (transaction.getTransactionTypeEnum() != null) {
                    transactionNode.put("transactionTypeEnum", transaction.getTransactionTypeEnum().name());
                }

                if (transaction.getFrequencyEnum() != null) {
                    transactionNode.put("frequencyEnum", transaction.getFrequencyEnum().name());
                }

                if (transaction.getNote() != null) {
                    transactionNode.put("note", transaction.getNote());
                }

                if (transaction.getFilename() != null) {
                    transactionNode.put("filename", transaction.getFilename());
                }

                if (transaction.getUser() != null) {
                    ObjectNode userNode = objectMapper.createObjectNode();
                    userNode.put("id", transaction.getUser().getId());
                    userNode.put("username", transaction.getUser().getUsername());
                    transactionNode.set("user", userNode);
                }

                if (transaction.getCategory() != null) {
                    ObjectNode categoryNode = objectMapper.createObjectNode();
                    categoryNode.put("id", transaction.getCategory().getId());
                    categoryNode.put("name", transaction.getCategory().getLabel());
                    transactionNode.set("category", categoryNode);
                }

                if (transaction.getBudget() != null) {
                    ObjectNode budgetNode = objectMapper.createObjectNode();
                    budgetNode.put("id", transaction.getBudget().getId());
                    budgetNode.put("name", transaction.getBudget().getLabel());
                    transactionNode.set("budget", budgetNode);
                }

                if (transaction.getLocation() != null) {
                    ObjectNode locationNode = objectMapper.createObjectNode();
                    locationNode.put("id", transaction.getLocation().getId());
                    locationNode.put("name", transaction.getLocation().getName());
                    transactionNode.set("location", locationNode);
                }

                transactionsArray.add(transactionNode);
            }

            clients.values().forEach(client -> {
                client.sendEvent("transactions_update", transactionsArray);
            });
            System.out.println("Transactions update sent");
        } catch (Exception e) {
            System.err.println("Error sending transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }
}