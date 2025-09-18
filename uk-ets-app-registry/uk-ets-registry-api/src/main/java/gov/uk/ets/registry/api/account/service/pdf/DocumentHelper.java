package gov.uk.ets.registry.api.account.service.pdf;

import com.fasterxml.jackson.core.type.TypeReference;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import gov.uk.ets.registry.api.common.CountryMap;
import gov.uk.ets.registry.api.common.Mapper;
import java.awt.Color;
import java.util.Map;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentHelper {

    static final int CONTENT_WIDTH = 530;

    static final Color APP_BLUE = new Color(29, 112, 184);
    static final Color APP_LIGHT_GREY = new Color(243, 242, 241);
    static final Color APP_GREY = new Color(111, 110, 109);

    static final Color APP_YELLOW_LIGHT = new Color(255, 246, 191);
    static final Color APP_YELLOW_DARK = new Color(255, 234, 95);

    static final int[] TWO_COLUMN_WIDTH = new int[] {200, 400, 0};
    static final int[] THREE_COLUMN_WIDTH = new int[] {200, 200, 200};

    static final int TABLE_WIDTH = 600;
    static final int TABLE_WIDTH_PERCENTAGE = 88;

    private final Mapper mapper;

    private final CountryMap countryMap;

    @Value("classpath:data/activityTypes.json")
    private Resource activityTypesResource;

    private Map<String, String> activityTypes;
    private Font appFont;


    @PostConstruct
    public void init() {
        activityTypes = mapper.convertResToPojo(activityTypesResource, new TypeReference<>() { });

        FontFactory.register(new ClassPathResource("fonts/Arial.ttf").getPath(), "ukets-font");
        appFont = FontFactory.getFont("ukets-font");
    }

    public String getCountry(String code) {
        return countryMap.getCountryName(code);
    }

    public String getActivityType(String type) {
        return activityTypes.get(type);
    }

    public Font getAppFont() {
        return appFont;
    }
}
