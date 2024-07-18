package io.github.xitadoo.sshop.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.result.UpdateResult;
import io.github.xitadoo.sshop.models.History;
import io.github.xitadoo.sshop.models.User;
import io.github.xitadoo.sshop.util.StringTranslator;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MongoDB {

    private static MongoClient MONGOCLIENT;
    private static MongoDatabase DATABASE;
    private final MongoCollection<Document> COLLECTION = DATABASE.getCollection("sshop");
    private final Executor executor = Executors.newCachedThreadPool();

    private Plugin plugin;

    public MongoDB(Plugin plugin) {
        this.plugin = plugin;
    }

    public static MongoDB mongoRepository(Plugin plugin, String connectionString, String databaseName) throws Exception {
        try {
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionString))
                    .uuidRepresentation(UuidRepresentation.STANDARD)
                    .build();
            MONGOCLIENT = MongoClients.create(settings);
            DATABASE = MONGOCLIENT.getDatabase(databaseName);
            plugin.getLogger().info("Connected to MongoDB database '" + databaseName + "'.");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to connect to MongoDB: " + e.getMessage());
        }
        return new MongoDB(plugin);
    }

    public CompletableFuture<Boolean> pushUserToDatabase(User user) {
        Document filter = new Document("playerId", user.getPlayerId().toString());

        FindIterable<Document> documents = COLLECTION.find(filter);
        boolean userExists = documents.iterator().hasNext();

        if (!userExists) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return COLLECTION.insertOne(new Document().append("playerId", user.getPlayerId().toString())
                            .append("history", StringTranslator.historyTranslated(user.getPlayerHistory()))).wasAcknowledged();
                } catch (Exception e) {
                    return false;
                }
            }, executor);
        } else {
            updateUser(user);
            return CompletableFuture.completedFuture(false);
        }
    }

    public boolean pushMultipleUsersToDatabaseSync(Map<UUID, User> userList) {
        for (User user : userList.values()) {
            if (!user.getPlayerHistory().isEmpty()) {
                Document filter = new Document("playerId", user.getPlayerId().toString());
                FindIterable<Document> documents = COLLECTION.find(filter);
                boolean userExists = documents.iterator().hasNext();

                if (!userExists) {
                    try {
                        COLLECTION.insertOne(new Document()
                                .append("playerId", user.getPlayerId().toString())
                                .append("history", StringTranslator.historyTranslated(user.getPlayerHistory())));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                } else {
                    updateUser(user);
                }
            }
        }

        // Check if all users were successfully added
        for (User user : userList.values()) {
            if (!user.getPlayerHistory().isEmpty()) {
                Document filter = new Document("playerId", user.getPlayerId().toString());
                FindIterable<Document> documents = COLLECTION.find(filter);
                boolean userExists = documents.iterator().hasNext();

                if (!userExists) {
                    return false;
                }
            }
        }
        return true;
    }

    public CompletableFuture<Boolean> pushMultipleUsersToDatabase(Map<UUID, User> userList) {
        List<CompletableFuture<Boolean>> userFutures = new ArrayList<>();

        for (User user : userList.values()) {
            if (!user.getPlayerHistory().isEmpty()) {
                Document filter = new Document("playerId", user.getPlayerId().toString());
                FindIterable<Document> documents = COLLECTION.find(filter);
                boolean userExists = documents.iterator().hasNext();

                if (!userExists) {
                    CompletableFuture<Boolean> userFuture = CompletableFuture.supplyAsync(() -> {
                        try {
                            return COLLECTION.insertOne(new Document()
                                            .append("playerId", user.getPlayerId().toString())
                                            .append("history", StringTranslator.historyTranslated(user.getPlayerHistory())))
                                    .wasAcknowledged();
                        } catch (Exception e) {
                            return false;
                        }
                    }, executor);
                    userFutures.add(userFuture);
                } else {
                    updateUser(user);
                }
            }
        }

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(userFutures.toArray(new CompletableFuture[0]));
        return combinedFuture.thenApply(result -> {
            // Check if all users were successfully added
            for (CompletableFuture<Boolean> userFuture : userFutures) {
                if (!userFuture.join()) {
                    return false;
                }
            }
            return true;
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return false;
        });
    }


    public CompletableFuture<Boolean> deleteMany(List<String> playerIds) {
        Document query = new Document("playerId", new Document("$in", playerIds));
        return CompletableFuture.supplyAsync(() -> {
            try {
                return COLLECTION.deleteMany(query).getDeletedCount() > 0;
            } catch (Exception e) {
                return false;
            }
        }, executor);
    }


    public CompletableFuture<Boolean> updateUser(User user) {
        Document filter = new Document("playerId", user.getPlayerId().toString());

        return CompletableFuture.supplyAsync(() -> {
            try {
                Document updateDoc = new Document("$set", new Document("history", StringTranslator.historyTranslated(user.getPlayerHistory())));
                UpdateResult updateResult = COLLECTION.updateOne(filter, updateDoc);
                return updateResult.getModifiedCount() > 0;
            } catch (Exception e) {
                return false;
            }
        }, executor);
    }

    public CompletableFuture<List<User>> fetchAllUsers() {
        FindIterable<Document> documents = COLLECTION.find();

        return CompletableFuture.supplyAsync(() -> {
            try {
                List<User> userList = new ArrayList<>();
                for (Document document : documents) {
                    User user = convertDocumentToUser(document);
                    userList.add(user);
                }
                return userList;
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch users", e);
            }
        }, executor);
    }

    public CompletableFuture<User> fetchUser(String playerId) {
        Document query = new Document("playerId", playerId);
        FindIterable<Document> documents = COLLECTION.find(query).limit(1);

        return CompletableFuture.supplyAsync(() -> {
            try {
                Document document = documents.first();
                if (document != null) {
                    return convertDocumentToUser(document);
                } else {
                    return null;
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch user", e);
            }
        }, executor);
    }

    public CompletableFuture<Boolean> deleteUser(User user) {
        Document filter = new Document("playerId", user.getPlayerId().toString());

        return CompletableFuture.supplyAsync(() -> {
            try {
                return COLLECTION.deleteOne(filter).getDeletedCount() > 0;
            } catch (Exception e) {
                return false;
            }
        }, executor);
    }


    private User convertDocumentToUser(Document document) {
        UUID playerId = UUID.fromString(document.getString("playerId"));
        List<History> playerHistory = StringTranslator.retrievedHistory(document.getString("history"));
        return new User(playerId, playerHistory);
    }

    public void close() {
        if (MONGOCLIENT != null) {
            MONGOCLIENT.close();
        }
    }
}
