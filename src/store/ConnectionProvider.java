package store;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * User: Tomas
 * Date: 22.10.12
 * Time: 22:03
 * To change this template use File | Settings | File Templates.
 */
public interface ConnectionProvider {

    Connection getConnection() throws SQLException;
}
