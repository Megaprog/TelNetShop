package store;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * User: Tomas
 * Date: 23.10.12
 * Time: 1:00
 * To change this template use File | Settings | File Templates.
 */
public interface StatementProvider {

    Statement getStatement() throws SQLException;
}
