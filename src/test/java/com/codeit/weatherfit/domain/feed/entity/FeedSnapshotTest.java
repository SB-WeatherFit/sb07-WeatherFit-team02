package com.codeit.weatherfit.domain.feed.entity;

import com.codeit.weatherfit.domain.base.BaseEntity;
import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.entity.ClothesType;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.weather.entity.PrecipitationType;
import com.codeit.weatherfit.domain.weather.entity.SkyStatus;
import com.codeit.weatherfit.domain.weather.entity.Weather;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

class FeedSnapshotTest {

    @Nested
    @DisplayName("WeatherSnapshot.from")
    class WeatherSnapshotFrom {

        /**
         * Weather에는 current(습도)와 temperatureCurrent(기온)가 모두 존재한다.
         * 각 필드를 서로 다른 고유값으로 설정하여 잘못된 getter 호출을 감지한다.
         */
        private Weather createWeather() {
            return Instancio.of(Weather.class)
                    .set(field(Weather.class, "skyStatus"), SkyStatus.CLOUDY)
                    .set(field(Weather.class, "type"), PrecipitationType.RAIN)
                    .set(field(Weather.class, "temperatureCurrent"), 25.5)
                    .set(field(Weather.class, "current"), 65.0) // 습도 — temperatureCurrent와 반드시 다른 값
                    .set(field(Weather.class, "min"), 15.0)
                    .set(field(Weather.class, "max"), 35.0)
                    .create();
        }

        @Test
        @DisplayName("skyStatus가 올바르게 매핑된다")
        void skyStatus() {
            Weather weather = createWeather();

            WeatherSnapshot snapshot = WeatherSnapshot.from(weather);

            assertThat(snapshot.skyStatus()).isEqualTo(SkyStatus.CLOUDY);
        }

        @Test
        @DisplayName("precipitationType이 올바르게 매핑된다")
        void precipitationType() {
            Weather weather = createWeather();

            WeatherSnapshot snapshot = WeatherSnapshot.from(weather);

            assertThat(snapshot.type()).isEqualTo(PrecipitationType.RAIN);
        }

        @Test
        @DisplayName("temperatureCurrent가 temperature.current에 매핑된다 — humidity current(65.0)가 아님")
        void temperatureCurrent() {
            Weather weather = createWeather();

            WeatherSnapshot snapshot = WeatherSnapshot.from(weather);

            assertThat(snapshot.temperature().current()).isEqualTo(25.5);
            assertThat(snapshot.temperature().current()).isNotEqualTo(65.0);
        }

        @Test
        @DisplayName("min이 temperature.min에 매핑된다 — max(35.0)가 아님")
        void min() {
            Weather weather = createWeather();

            WeatherSnapshot snapshot = WeatherSnapshot.from(weather);

            assertThat(snapshot.temperature().min()).isEqualTo(15.0);
            assertThat(snapshot.temperature().min()).isNotEqualTo(35.0);
        }

        @Test
        @DisplayName("max가 temperature.max에 매핑된다 — min(15.0)이 아님")
        void max() {
            Weather weather = createWeather();

            WeatherSnapshot snapshot = WeatherSnapshot.from(weather);

            assertThat(snapshot.temperature().max()).isEqualTo(35.0);
            assertThat(snapshot.temperature().max()).isNotEqualTo(15.0);
        }
    }

    @Nested
    @DisplayName("ClothesSnapshot.from")
    class ClothesSnapshotFrom {

        private final UUID clothesId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        private final String clothesName = "겨울 패딩";
        private final String imageKey = "images/unique-key-abc";
        private final List<String> attributes = List.of("면", "빨강", "겨울");

        private Clothes createClothes() {
            return Instancio.of(Clothes.class)
                    .set(field(BaseEntity.class, "id"), clothesId)
                    .set(field(Clothes.class, "name"), clothesName)
                    .set(field(Clothes.class, "imageKey"), imageKey)
                    .create();
        }

        @Test
        @DisplayName("clothesId가 clothes.getId()에서 온다")
        void clothesId() {
            Clothes clothes = createClothes();

            ClothesSnapshot snapshot = ClothesSnapshot.from(clothes, attributes);

            assertThat(snapshot.clothesId()).isEqualTo(clothesId);
        }

        @Test
        @DisplayName("name이 clothes.getName()에서 온다 — imageKey가 아님")
        void name() {
            Clothes clothes = createClothes();

            ClothesSnapshot snapshot = ClothesSnapshot.from(clothes, attributes);

            assertThat(snapshot.name()).isEqualTo(clothesName);
            assertThat(snapshot.name()).isNotEqualTo(imageKey);
        }

        @Test
        @DisplayName("imageKey가 clothes.getImageKey()에서 온다 — name이 아님")
        void imageKey() {
            Clothes clothes = createClothes();

            ClothesSnapshot snapshot = ClothesSnapshot.from(clothes, attributes);

            assertThat(snapshot.imageKey()).isEqualTo(imageKey);
            assertThat(snapshot.imageKey()).isNotEqualTo(clothesName);
        }

        @Test
        @DisplayName("attributes가 파라미터 그대로 유지된다")
        void attributes() {
            Clothes clothes = createClothes();

            ClothesSnapshot snapshot = ClothesSnapshot.from(clothes, attributes);

            assertThat(snapshot.attributes()).containsExactly("면", "빨강", "겨울");
        }

        @Test
        @DisplayName("imageKey가 null인 경우에도 정상 동작한다")
        void nullImageKey() {
            Clothes clothes = Instancio.of(Clothes.class)
                    .set(field(Clothes.class, "imageKey"), null)
                    .create();

            ClothesSnapshot snapshot = ClothesSnapshot.from(clothes, List.of());

            assertThat(snapshot.imageKey()).isNull();
        }
    }

    @Nested
    @DisplayName("Feed.create")
    class FeedCreate {

        @Test
        @DisplayName("author가 세팅된다")
        void author() {
            User author = Instancio.create(User.class);
            Weather weather = Instancio.create(Weather.class);

            Feed feed = Feed.create(author, weather, "테스트 콘텐츠");

            assertThat(feed.getAuthor()).isSameAs(author);
        }

        @Test
        @DisplayName("content가 세팅된다")
        void content() {
            User author = Instancio.create(User.class);
            Weather weather = Instancio.create(Weather.class);

            Feed feed = Feed.create(author, weather, "오늘 날씨에 맞는 옷차림");

            assertThat(feed.getContent()).isEqualTo("오늘 날씨에 맞는 옷차림");
        }

        @Test
        @DisplayName("weather로부터 weatherSnapshot이 생성된다")
        void weatherSnapshot() {
            User author = Instancio.create(User.class);
            Weather weather = Instancio.of(Weather.class)
                    .set(field(Weather.class, "skyStatus"), SkyStatus.MOSTLY_CLOUDY)
                    .set(field(Weather.class, "type"), PrecipitationType.SNOW)
                    .set(field(Weather.class, "temperatureCurrent"), -3.0)
                    .set(field(Weather.class, "min"), -8.0)
                    .set(field(Weather.class, "max"), 2.0)
                    .create();

            Feed feed = Feed.create(author, weather, "내용");

            WeatherSnapshot snapshot = feed.getWeatherSnapshot();
            assertThat(snapshot).isNotNull();
            assertThat(snapshot.skyStatus()).isEqualTo(SkyStatus.MOSTLY_CLOUDY);
            assertThat(snapshot.type()).isEqualTo(PrecipitationType.SNOW);
            assertThat(snapshot.temperature().current()).isEqualTo(-3.0);
            assertThat(snapshot.temperature().min()).isEqualTo(-8.0);
            assertThat(snapshot.temperature().max()).isEqualTo(2.0);
        }
    }

    @Nested
    @DisplayName("FeedClothes.create")
    class FeedClothesCreate {

        @Test
        @DisplayName("feed가 세팅된다")
        void feed() {
            Feed feed = Instancio.create(Feed.class);
            Clothes clothes = Instancio.create(Clothes.class);

            FeedClothes feedClothes = FeedClothes.create(feed, clothes, List.of("옵션1"));

            assertThat(feedClothes.getFeed()).isSameAs(feed);
        }

        @Test
        @DisplayName("clothes와 attributes로부터 clothesSnapshot이 생성된다")
        void clothesSnapshot() {
            Feed feed = Instancio.create(Feed.class);
            String expectedName = "니트 스웨터";
            String expectedKey = "images/sweater-456";
            Clothes clothes = Instancio.of(Clothes.class)
                    .set(field(Clothes.class, "name"), expectedName)
                    .set(field(Clothes.class, "imageKey"), expectedKey)
                    .create();
            List<String> attributes = List.of("울", "베이지", "가을");

            FeedClothes feedClothes = FeedClothes.create(feed, clothes, attributes);

            ClothesSnapshot snapshot = feedClothes.getClothesSnapshot();
            assertThat(snapshot).isNotNull();
            assertThat(snapshot.clothesId()).isEqualTo(clothes.getId());
            assertThat(snapshot.name()).isEqualTo(expectedName);
            assertThat(snapshot.imageKey()).isEqualTo(expectedKey);
            assertThat(snapshot.attributes()).containsExactly("울", "베이지", "가을");
        }
    }
}
