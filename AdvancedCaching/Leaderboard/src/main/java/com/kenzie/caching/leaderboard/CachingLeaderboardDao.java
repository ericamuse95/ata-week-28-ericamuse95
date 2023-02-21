package com.kenzie.caching.leaderboard;

import com.kenzie.caching.leaderboard.resources.datasource.Entry;
import com.kenzie.caching.leaderboard.resources.datasource.LeaderboardDao;
import redis.clients.jedis.Jedis;

import javax.inject.Inject;
import java.util.Optional;

public class CachingLeaderboardDao {
    private final LeaderboardDao dataSource;
    private final CacheClient cache;

    /**
     * Constructor.
     *
     * @param dataSource LeaderboardDAO object
     * @param cache      CacheClient object
     */
    @Inject
    public CachingLeaderboardDao(LeaderboardDao dataSource, CacheClient cache) {
        this.dataSource = dataSource;
        this.cache = cache;
    }

    /**
     * Retrieves score associated with the specified user. Should use the cache when possible, but the dataSource object
     * is our source of truth for high scores. The TTL for our high scores should be 5 minutes.
     * <p>
     * PARTICIPANTS: replace return 0 with your implementation of this method.
     *
     * @param username String representing player username
     * @return long representing score associated with username
     */
    public long getHighScore(String username) {
        Optional<String> cachedValue = cache.getValue(username);
        if (cachedValue.isPresent()) {
            return Long.parseLong(cachedValue.get());
        } else {
            Optional<Entry> valueFromDao = Optional.ofNullable(dataSource.getEntry(username));
            //get the score if the entry is not null, otherwise set the highScore to 0
            long highScore = valueFromDao.map(Entry::getScore).orElse(0L);
            cache.setValue(username, 5 * 60, Long.toString(highScore));
            return highScore;
        }
    }

        public boolean invalidateScore (String username){
        boolean success = false;
        if(cache.invalidate(username)){
            dataSource.getEntry(username);
            success = true;
        }
        return success;
}
}


