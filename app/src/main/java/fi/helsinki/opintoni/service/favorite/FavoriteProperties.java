package fi.helsinki.opintoni.service.favorite;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "userDefaults")
public class FavoriteProperties {
    private List<Map<String, String>> defaultFavorites = new ArrayList<>();

    public List<Map<String, String>> getDefaultFavorites() {
        return defaultFavorites;
    }

    public void setDefaultFavorites(List<Map<String, String>> defaultFavorites) {
        this.defaultFavorites = defaultFavorites;
    }
}
