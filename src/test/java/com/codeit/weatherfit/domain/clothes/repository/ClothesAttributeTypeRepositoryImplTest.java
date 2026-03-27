package com.codeit.weatherfit.domain.clothes.repository;

import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherfit.domain.clothes.dto.response.SortBy;
import com.codeit.weatherfit.domain.clothes.dto.response.SortDirection;
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
        ClothesAttributeTypeRepositoryImpl.class,
        ClothesAttributeTypeRepositoryImplTest.QuerydslConfig.class
})
class ClothesAttributeTypeRepositoryImplTest {

    @Autowired
    private ClothesAttributeTypeRepository repository;

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
    @DisplayName("전체 조회 + 그룹핑 + 정렬")
    void getAttributeDefs_all() {
        ClothesAttributeType type1 = createType("color");
        ClothesAttributeType type2 = createType("size");

        saveValue(type1, "RED");
        saveValue(type1, "BLUE");
        saveValue(type2, "L");

        List<ClothesAttributeDefDto> result =
                repository.getAttributeDefs(SortBy.NAME, SortDirection.ASCENDING);

        assertThat(result).hasSize(2);

        assertThat(result.get(0).name()).isEqualTo("color");
        assertThat(result.get(1).name()).isEqualTo("size");

        assertThat(result.get(0).selectableValues()).hasSize(2);
    }

    @Test
    @DisplayName("keyword 필터링")
    void getAttributeDefs_withKeyword() {
        ClothesAttributeType type1 = createType("color");
        ClothesAttributeType type2 = createType("size");

        saveValue(type1, "RED");
        saveValue(type2, "L");

        List<ClothesAttributeDefDto> result =
                repository.getAttributeDefs(
                        SortBy.NAME,
                        SortDirection.ASCENDING,
                        "col"
                );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("color");
    }


    private ClothesAttributeType createType(String name) {
        ClothesAttributeType type = newInstance(ClothesAttributeType.class);

        ReflectionTestUtils.setField(type, "name", name);
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