package company.vk.edu.distrib.compute.vodobryshkin;

import company.vk.edu.distrib.compute.Dao;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

/**
 * Декоратор для реализации кэша.
 *
 * <p>
 *     Спроектирован таким образом, чтобы кэш можно было тоже использовать как Dao.
 * </p>
 */
public class CacheDao implements Dao<byte[]> {
    private final int limit;
    private final Dao<byte[]> dao;
    private final Deque<String> cachedKeys = new ArrayDeque<>();
    private final Object lock = new Object();

    public CacheDao(int limit) {
        this(limit, new InMemoryDao());
    }

    public CacheDao(int limit, Dao<byte[]> dao) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Cache must contain more than 0 elements.");
        }

        this.limit = limit;
        this.dao = dao;
    }

    @Override
    public byte[] get(String key) throws NoSuchElementException, IllegalArgumentException, IOException {
        synchronized (lock) {
            return dao.get(key);
        }
    }

    @Override
    public void upsert(String key, byte[] value) throws IllegalArgumentException, IOException {
        synchronized (lock) {
            boolean present;

            try {
                dao.get(key);
                present = true;
            } catch (NoSuchElementException e) {
                present = false;
            }

            if (present) {
                cachedKeys.remove(key);
            } else if (cachedKeys.size() == limit) {
                String firstKey = cachedKeys.removeFirst();
                dao.delete(firstKey);
            }

            dao.upsert(key, value);
            cachedKeys.addLast(key);
        }
    }

    @Override
    public void delete(String key) throws IllegalArgumentException, IOException {
        synchronized (lock) {
            dao.delete(key);
            cachedKeys.remove(key);
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            dao.close();
        }
    }
}