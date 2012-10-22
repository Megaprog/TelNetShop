package store;

import java.sql.Connection;

/**
 * User: Tomas
 * Date: 22.10.12
 * Time: 22:17
 * To change this template use File | Settings | File Templates.
 */
public interface ConnectionExecutor {

    void execute(Connection conn);
}
