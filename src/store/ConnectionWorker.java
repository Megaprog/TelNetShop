package store;

import java.sql.Connection;

/**
 * User: Tomas
 * Date: 22.10.12
 * Time: 22:14
 * To change this template use File | Settings | File Templates.
 */
public interface ConnectionWorker {

    void executeWithConnection(ConnectionExecutor executor);
}
