package fr.pixelqilin.pixelqilinranked.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.pixelqilin.pixelqilinranked.PixelQilinRanked;
import fr.pixelqilin.pixelqilinranked.database.handlers.SQLLoader;
import fr.pixelqilin.pixelqilinranked.database.handlers.SQLSaver;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;

public class MySQLManager extends SQLManager {

    // database settings
    private String host;
    private String dbName;
    private String password;
    private String user;
    private String port;

    // instances
    private final FileConfiguration config;

    /**
     * Constructor.
     * @param plugin main class instance.
     */
    public MySQLManager(PixelQilinRanked plugin) {
        this.config = plugin.getConfig();

        super.sqlLoader = new SQLLoader(this);
        super.sqlSaver = new SQLSaver(this);

        setupDatabase();
    }

    /**
     * Load identifiers for database connection.
     */
    public void initCredentials() {

        ConfigurationSection sqlSection= config.getConfigurationSection("database");

        host = sqlSection.getString("host");
        port = sqlSection.getString("port");
        dbName = sqlSection.getString("name");
        user = sqlSection.getString("user");
        password = sqlSection.getString("password");
    }

    /**
     * Connect to database.
     */
    public void initHikariCP(){

        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setJdbcUrl(this.toUri());
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.setMaxLifetime(300000L);
        hikariConfig.setLeakDetectionThreshold(10000L);
        hikariConfig.setConnectionTimeout(10000L);

        super.hikariDataSource = new HikariDataSource(hikariConfig);
    }

    /**
     * Init database.
     */
    public void setupDatabase() {
        initCredentials();
        initHikariCP();

        try {
            testConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setupTables();
    }

    /**
     * Setup JdbcUrl.
     * @return JdbcUrl.
     */
    private String toUri(){
        return "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.dbName + "?useSSL=false";
    }

}
