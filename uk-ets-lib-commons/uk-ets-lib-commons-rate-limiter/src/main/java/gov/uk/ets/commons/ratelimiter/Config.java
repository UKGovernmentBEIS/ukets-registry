package gov.uk.ets.commons.ratelimiter;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class Config {

    @Bean
    public LoadingCache<String, Bucket> getThrottlingCache() {
        CacheLoader<String, Bucket> loader = new CacheLoader<String, Bucket>() {

            @Override
            public Bucket load(String o) throws Exception {
                return null;
            }

        };

        return CacheBuilder.newBuilder().build(loader);
    }
}
