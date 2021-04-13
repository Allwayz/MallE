package cn.allwayz.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author allwayz
 */
@EnableRedisHttpSession
@Configuration
public class MallESessionConfig {
    /**
     * The serialization mechanism selects JSON format, and JDK serialization is used by default.
     * All objects to be saved will implement the Serializable interface
     * Method names cannot be modified
     * @return
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    /**
     * Customize the cookie Settings that the server returns to the browser
     * @return
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        // Set the Coolie name
        serializer.setCookieName("MALLESESSION"); // <1>
        // The default Coolie valid domain is the current domain. For example, the valid field for the cookie data returned by the auth.malle.com server is auth.malle.com
        // To make a login available everywhere. Set its valid domain to the top-level domain malle.com
        serializer.setDomainName("malle.com");
        return serializer;
    }
}
