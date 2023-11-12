package go.storage

/**
 * Defines basic operations for key-value storage.
 *
 * @param Key The type of the key for the storage entries.
 * @param Data The type of the data associated with the storage entries.
 */
interface Storage <Key,Data> {

    /**
     * Creates a new entry in the storage.
     *
     * @param key The key of the entry.
     * @param data The data of the entry.
     */
    fun create(key:Key, data:Data)

    /**
     * Retrieves the data associated with the specified key from the storage.
     *
     * @param key The key to look up in the storage.
     * @return The data associated with the given key, or null if the key is not found.
     */
    fun read(key:Key):Data?

    /**
     * Updates the data associated with the specified key in the storage.
     *
     * @param key The key of the entry to be updated.
     * @param data The new data to be associated with the key.
     */
    fun update ( key: Key, data:Data)
    /**
     * Deletes the entry with the specified key from the storage.
     *
     * @param key The key of the entry to be deleted.
     */
    fun delete(key:Key)
}