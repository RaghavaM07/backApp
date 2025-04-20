package daemon;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MakeObjectMapper {
    public static ObjectMapper makeNew() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(MapperFeature.ALLOW_COERCION_OF_SCALARS);
        return objectMapper;
    }
}
