package fr.pixelqilin.pixelqilinranked.database;

import com.zaxxer.hikari.HikariDataSource;
import fr.pixelqilin.pixelqilinranked.database.handlers.SQLLoader;
import fr.pixelqilin.pixelqilinranked.database.handlers.SQLSaver;
import fr.pixelqilin.pixelqilinranked.utils.PluginLogger;

import java.sql.*;

public abstract class SQLManager {

    protected HikariDataSource hikariDataSource;

    protected SQLLoader sqlLoader;
    protected SQLSaver sqlSaver;

    /**
     * Setup the database.
     */
    public void setupTables() {
        final Connection connection = getConnection();
        try {
            if (!tableExists(connection, "RANKED_PLAYERS")) {

                String str = "create table RANKED_PLAYERS\n" +
                        "  (\n" +
                        "     PLAYER_UUID char(36)  not null  ,\n" +
                        "     PLAYER_RANK char(32) not null, \n" +
                        "     PLAYER_ELO int not null, \n" +
                        "     constraint PK_PLAYER_UUID primary key (PLAYER_UUID)\n" +
                        "  );";

                PreparedStatement preparedStatement = connection.prepareStatement(str);
                preparedStatement.execute();

                preparedStatement.close();
                PluginLogger.info("Table 'RANKED_PLAYERS' created in database.");
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if a table exists in database.
     *
     * @param connection connection to check.
     * @param tableName  name of the table to check.
     * @return true if table exists.
     * @throws SQLException SQL errors.
     */
    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[]{"TABLE"});

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
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Test database connection.
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
     * @return load progression SQL instance.
     */
    public SQLLoader getSqlLoader() {
        return sqlLoader;
    }

    /**
     * Get save progression SQL instance.
     * @return save progression SQL instance.
     */
    public SQLSaver getSqlSaver() {
        return sqlSaver;
    }
}
