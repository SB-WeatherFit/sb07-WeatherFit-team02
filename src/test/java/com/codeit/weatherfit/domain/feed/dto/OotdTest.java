package com.codeit.weatherfit.domain.feed.dto;

import com.codeit.weatherfit.domain.feed.entity.ClothesSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OotdTest {

    /**
     * imageKey와 imageUrl을 완전히 다른 값으로 설정하여
     * Ootd.from()이 파라미터 imageUrl을 사용하는지, snapshot의 imageKey를 사용하는지 감지한다.
     */
    private static final UUID CLOTHES_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final String CLOTHES_NAME = "린넨 셔츠";
    private static final String IMAGE_KEY = "images/internal-key-999";
    private static final String IMAGE_URL = "https://cdn.example.com/presigned-url-xyz";
    private static final List<String> ATTRIBUTES = List.of("린넨", "화이트", "여름");

    private ClothesSnapshot createSnapshot() {
        return new ClothesSnapshot(CLOTHES_ID, CLOTHES_NAME, IMAGE_KEY, ATTRIBUTES);
    }

    @Nested
    @DisplayName("from")
    class From {

        @Test
        @DisplayName("clothesId가 매핑된다")
        void clothesId() {
            Ootd ootd = Ootd.from(createSnapshot(), IMAGE_URL);

            assertThat(ootd.clothesId()).isEqualTo(CLOTHES_ID);
        }

        @Test
        @DisplayName("name이 매핑된다")
        void name() {
            Ootd ootd = Ootd.from(createSnapshot(), IMAGE_URL);

            assertThat(ootd.name()).isEqualTo(CLOTHES_NAME);
        }

        @Test
        @DisplayName("imageUrl은 파라미터 값을 사용한다 — snapshot의 imageKey가 아님")
        void imageUrl() {
            Ootd ootd = Ootd.from(createSnapshot(), IMAGE_URL);

            assertThat(ootd.imageUrl()).isEqualTo(IMAGE_URL);
            assertThat(ootd.imageUrl()).isNotEqualTo(IMAGE_KEY);
        }

        @Test
        @DisplayName("attributes가 Option 리스트로 변환된다")
        void attributesToOptions() {
            Ootd ootd = Ootd.from(createSnapshot(), IMAGE_URL);

            assertThat(ootd.attributes()).hasSize(3);
            assertThat(ootd.attributes().get(0).value()).isEqualTo("린넨");
            assertThat(ootd.attributes().get(1).value()).isEqualTo("화이트");
            assertThat(ootd.attributes().get(2).value()).isEqualTo("여름");
        }

        @Test
        @DisplayName("attributes 순서가 유지된다")
        void attributeOrder() {
            List<String> orderedAttributes = List.of("C", "A", "B");
            ClothesSnapshot snapshot = new ClothesSnapshot(CLOTHES_ID, CLOTHES_NAME, IMAGE_KEY, orderedAttributes);

            Ootd ootd = Ootd.from(snapshot, IMAGE_URL);

            assertThat(ootd.attributes())
                    .extracting(Ootd.Option::value)
                    .containsExactly("C", "A", "B");
        }

        @Test
        @DisplayName("빈 attributes면 빈 리스트를 반환한다")
        void emptyAttributes() {
            ClothesSnapshot snapshot = new ClothesSnapshot(CLOTHES_ID, CLOTHES_NAME, IMAGE_KEY, List.of());

            Ootd ootd = Ootd.from(snapshot, IMAGE_URL);

            assertThat(ootd.attributes()).isEmpty();
        }

        @Test
        @DisplayName("imageUrl이 null이면 null이 들어간다")
        void nullImageUrl() {
            Ootd ootd = Ootd.from(createSnapshot(), null);

            assertThat(ootd.imageUrl()).isNull();
        }
    }
}
