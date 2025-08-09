package pl.fortx.report.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pl.fortx.report.config.PluginConfig;
import pl.fortx.report.helper.AdminChatHelper;
import pl.fortx.report.helper.ReportHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
public class RedisManager {
    private final @NotNull PluginConfig config;
    private final @NotNull JavaPlugin plugin;

    @Getter
    private JedisPool jedisPool;
    private ExecutorService executorService;
    private boolean running = true;

    public void initialize() {
        String host = config.getConfig().getString("redis.host");
        int port = config.getConfig().getInt("redis.port");
        String password = config.getConfig().getString("redis.password");

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);

        if (password != null && !password.isEmpty()) {
            jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
        } else {
            jedisPool = new JedisPool(poolConfig, host, port);
        }

        executorService = Executors.newSingleThreadExecutor();
        plugin.getLogger().info("Redis connection initialized successfully.");
    }

    public void startListening(ReportHelper reportHelper, AdminChatHelper adminChatHelper) {
        executorService.submit(() -> {
            try {
                while (running) {
                    try (Jedis jedis = jedisPool.getResource()) {
                        jedis.subscribe(new JedisPubSub() {
                                            @Override
                                            public void onMessage(String channel, String message) {
                                                String reportChannel = config.getConfig().getString("redis.channels.report");
                                                String adminChatChannel = config.getConfig().getString("redis.channels.adminchat");

                                                if (channel.equals(reportChannel)) {
                                                    reportHelper.processRedisReport(message);
                                                } else if (channel.equals(adminChatChannel)) {
                                                    adminChatHelper.processAdminChatMessage(message);
                                                }
                                            }
                                        }, config.getConfig().getString("redis.channels.report"),
                                config.getConfig().getString("redis.channels.adminchat"));
                    } catch (Exception e) {
                        plugin.getLogger().warning("Redis subscription error: " + e.getMessage());
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Fatal Redis error: " + e.getMessage());
            }
        });
    }

    public void publishReport(String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(config.getConfig().getString("redis.channels.report"), message);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to publish report: " + e.getMessage());
        }
    }

    public void publishAdminChat(String message) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(config.getConfig().getString("redis.channels.adminchat"), message);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to publish admin chat message: " + e.getMessage());
        }
    }

    public void shutdown() {
        running = false;
        if (executorService != null) {
            executorService.shutdown();
        }
        if (jedisPool != null) {
            jedisPool.close();
        }
    }
}