package store;

/**
 * User: Tomas
 * Date: 23.10.12
 * Time: 0:59
 * To change this template use File | Settings | File Templates.
 */
public interface StatementWorker {

    void executeWithStatement(StatementProvider statementProvider, StatementExecutor statementExecutor);
}
