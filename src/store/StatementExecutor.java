package store;

import java.sql.Connection;
import java.sql.Statement;

/**
 * User: Tomas
 * Date: 23.10.12
 * Time: 0:59
 * To change this template use File | Settings | File Templates.
 */
public interface StatementExecutor {

    void execute(Connection conn, Statement statement);
}
