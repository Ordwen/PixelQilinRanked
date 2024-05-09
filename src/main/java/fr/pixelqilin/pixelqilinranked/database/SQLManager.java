package fr.pixelqilin.pixelqilinranked.database;

import com.zaxxer.hikari.HikariDataSource;
import fr.pixelqilin.pixelqilinranked.database.handlers.SQLLoader;
import fr.pixelqilin.pixelqilinranked.database.handlers.SQLSaver;
import fr.pixelqilin.pixelqilinranked.database.handlers.SQLTop;
import fr.pixelqilin.pixelqilinranked.utils.PluginLogger;

import java.sql.*;

public abstract class SQLManager {

    protected HikariDataSource hikariDataSource;

    protected SQLLoader sqlLoader;
    protected SQLSaver sqlSaver;
    protected SQLTop sqlTop;

    /**
     * Set up the database.
     */
    public void setupTables() {
        final Connection connection = getConnection();
        try {
            if (!tableExists(connection)) {

                String str = """
                        create table RANKED_PLAYERS
                          (
                             PLAYER_UUID char(36)  not null  ,
                             PLAYER_RANK char(32) not null,\s
                             PLAYER_ELO int not null,\s
                             PLAYER_WINS int not null,\s
                             PLAYER_LOSSES int not null,\s
                             constraint PK_PLAYER_UUID primary key (PLAYER_UUID)
                          );""";

                PreparedStatement preparedStatement = connection.prepareStatement(str);
                preparedStatement.execute();

                preparedStatement.close();
                PluginLogger.info("Table 'RANKED_PLAYERS' created in database.");
            }

            connection.close();
        } catch (SQLException e) {
            PluginLogger.error("An error occurred while creating table 'RANKED_PLAYERS'.");
            PluginLogger.error(e.getMessage());
        }
    }

    /**
     * Check if a table exists in database.
     *
     * @param connection connection to check.
     * @return true if table exists.
     * @throws SQLException SQL errors.
     */
    private static boolean tableExists(Connection connection) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, "RANKED_PLAYERS", new String[]{"TABLE"});

        return resultSet.next();
    }

    /**
     * Close database connection.
     */
    public void close() {
        this.hikariDataSource.close();
    }

    /**
     * Get database connection.
     *
     * @return database Connection.
     */
    public Connection getConnection() {
        if (this.hikariDataSource != null && !this.hikariDataSource.isClosed()) {
            try {
                return this.hikariDataSource.getConnection();
            } catch (SQLException e) {
                PluginLogger.error("An error occurred while getting connection to database.");
                PluginLogger.error(e.getMessage());
            }
        }
        return null;
    }

    /**
     * Test database connection.
     *
     * @throws SQLException SQL errors.
     */
    protected void testConnection() throws SQLException {
        Connection con = getConnection();
        if (con.isValid(1)) {
            PluginLogger.info("Plugin successfully connected to database " + con.getCatalog() + ".");
            con.close();
        } else PluginLogger.error("IMPOSSIBLE TO CONNECT TO DATABASE");
    }

    /**
     * Get load progression SQL instance.
     *
     * @return load progression SQL instance.
     */
    public SQLLoader getSqlLoader() {
        return sqlLoader;
    }

    /**
     * Get save progression SQL instance.
     *
     * @return save progression SQL instance.
     */
    public SQLSaver getSqlSaver() {
        return sqlSaver;
    }

    /**
     * Get top SQL instance.
     *
     * @return top SQL instance.
     */
    public SQLTop getSqlTop() {
        return sqlTop;
    }
}
