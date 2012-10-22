package util;

/**
 * Поставщик значений
 * @author Tomas
 *
 * @param <T> тип возвращаемого значения
 */
public interface Supplier<T> {

	/**
	 * @return значение определенного типа
	 */
	T get();
}
