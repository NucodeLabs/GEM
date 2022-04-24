package ru.nucodelabs.data.ves;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class VesDataTest {

    static ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    static List<ModelLayer> vesModelData;
    static List<ExperimentalData> vesExperimentalData;

    @BeforeAll
    static void prepare() {
        List<ModelLayer> layers = new ArrayList<>();
        layers.add(ModelLayer.create(12, 12));
        layers.add(ModelLayer.create(13, 13));
        layers.add(ModelLayer.create(-1, -1));
        vesModelData = layers;

        vesExperimentalData = Collections.emptyList();
    }

    @Test
    void picket() throws JsonProcessingException {
        Picket picket
                = Picket.createWithNewId("test", vesExperimentalData, vesModelData, Picket.DEFAULT_X_OFFSET, Picket.DEFAULT_Z);

        var violations = validator.validate(picket);
        Assertions.assertFalse(violations.isEmpty());
        violations.forEach(System.out::println);

        String json = objectMapper.writeValueAsString(picket);
        System.out.println(json);

        //как получить список значений
        List<Double> power = picket.getModelData().stream().map(ModelLayer::getPower).toList();
        power.forEach(System.out::println);

        Picket picket1 = objectMapper.readValue(json, Picket.class);
        Assertions.assertEquals(picket, picket1);

        Picket picket2
                = objectMapper.readValue(
                """
                        {
                            "name" : "Пикет",
                            "experimentalData" : [],
                            "modelData" : [],
                            "z" : 0,
                            "offsetX" : 100
                        }
                        """,
                Picket.class
        );
        Assertions.assertNotNull(picket2.getId());
        Assertions.assertEquals(Collections.emptyList(), picket2.getExperimentalData());
        Assertions.assertEquals("Пикет", picket2.getName());
        Assertions.assertEquals(0, picket2.getZ());

        Assertions.assertEquals(vesModelData.get(0), ModelLayer.create(12, 12));
    }

    @Test
    void validator() {
        Picket picket = Picket.createWithNewId("test", vesExperimentalData, vesModelData, -1, 0);

        Set<ConstraintViolation<Picket>> violations = validator.validate(picket);
        Assertions.assertFalse(violations.isEmpty());
        violations.forEach(System.out::println);
    }
}
