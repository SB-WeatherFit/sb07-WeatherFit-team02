package com.codeit.weatherfit.domain.clothes.repository;

import com.codeit.weatherfit.domain.clothes.entity.ClothesAttributeType;
import com.codeit.weatherfit.domain.clothes.entity.SelectableValue;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({
        SelectableValueRepositoryImpl.class,
        SelectableValueRepositoryImplTest.QuerydslConfig.class
})
class SelectableValueRepositoryImplTest {

    @Autowired
    private SelectableValueRepository selectableValueRepository;

    @Autowired
    private EntityManager em;

    @TestConfiguration
    static class QuerydslConfig {

        @PersistenceContext
        private EntityManager em;

        @Bean
        public JPAQueryFactory jpaQueryFactory() {
            return new JPAQueryFactory(em);
        }
    }

    @Test
    @DisplayName("findByType")
    void findByType() {
        ClothesAttributeType type = createType();

        saveValue(type, "RED");
        saveValue(type, "BLUE");

        List<SelectableValue> result =
                selectableValueRepository.findByClothesAttributeType(type);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("findByTypeAndOption")
    void findByTypeAndOption() {
        ClothesAttributeType type = createType();

        saveValue(type, "RED");
        saveValue(type, "BLUE");

        SelectableValue result =
                selectableValueRepository
                        .findByClothesAttributeTypeAndOption(type, "RED")
                        .orElseThrow();

        assertThat(result.getOption()).isEqualTo("RED");
    }

    @Test
    @DisplayName("deleteSelectableValuesByType")
    void deleteByType() {
        ClothesAttributeType type = createType();

        saveValue(type, "RED");
        saveValue(type, "BLUE");

        selectableValueRepository.deleteSelectableValuesByType(type.getId());

        List<SelectableValue> result =
                selectableValueRepository.findByClothesAttributeType(type);

        assertThat(result).isEmpty();
    }


    private ClothesAttributeType createType() {
        ClothesAttributeType type = newInstance(ClothesAttributeType.class);

        ReflectionTestUtils.setField(type, "name", "color");
        ReflectionTestUtils.setField(type, "createdAt", Instant.now());

        em.persist(type);
        return type;
    }

    private SelectableValue saveValue(ClothesAttributeType type, String option) {
        SelectableValue value = newInstance(SelectableValue.class);

        ReflectionTestUtils.setField(value, "clothesAttributeType", type);
        ReflectionTestUtils.setField(value, "option", option);
        ReflectionTestUtils.setField(value, "createdAt", Instant.now());

        em.persist(value);
        return value;
    }

    private <T> T newInstance(Class<T> clazz) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}