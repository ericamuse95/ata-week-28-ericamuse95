package com.kenzie.caching.goodreads.caching;


import com.kenzie.caching.goodreads.dao.NonCachingReadingLogDao;
import com.kenzie.caching.goodreads.dao.ReadingLogDao;
import com.kenzie.caching.goodreads.dao.models.ReadingLog;

import java.time.ZonedDateTime;
import java.util.Optional;
import javax.inject.Inject;

public class CachingReadingLogDao implements ReadingLogDao {

    private NonCachingReadingLogDao nonCachingReadingLogDao;
    private CacheClient cacheClient;

    @Inject
    public CachingReadingLogDao(CacheClient cacheClient, NonCachingReadingLogDao nonCachingReadingLogDao) {
        this.cacheClient = cacheClient;
        this.nonCachingReadingLogDao = nonCachingReadingLogDao;
    }

    @Override
    public ReadingLog updateReadingProgress(String userId, String isbn, ZonedDateTime timestamp,
                                            int pageNumber, boolean isFinished) {
        deleteCacheEntry(userId, timestamp.getYear());
        return nonCachingReadingLogDao.updateReadingProgress(userId, isbn, timestamp, pageNumber, isFinished);
    }

    @Override
    public int getBooksReadInYear(String userId, int year) {
            String cacheKey = String.format("books_read:%s:%d", userId, year);
            String cachedValue = cacheClient.getValue(cacheKey);
            if (cachedValue != null) {
                return Integer.parseInt(cachedValue);
            } else {
                int booksRead = nonCachingReadingLogDao.getBooksReadInYear(userId, year);
                cacheClient.setValue(cacheKey, Integer.toString(booksRead), 60);
                return booksRead;
            }
        }
    private void deleteCacheEntry(String userId, int year) {
        String cacheKey = String.format("books_read:%s:%d", userId, year);
        cacheClient.delete(cacheKey);
    }
}
