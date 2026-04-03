package com.codeit.weatherfit.domain.clothes.service;

import com.codeit.weatherfit.domain.clothes.dto.request.ClothesCreateRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeDto;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesDto;
import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.entity.ClothesAttributeType;
import com.codeit.weatherfit.domain.clothes.entity.ClothesType;
import com.codeit.weatherfit.domain.clothes.exception.ClothesAttributeTypeNotFoundException;
import com.codeit.weatherfit.domain.clothes.exception.ClothesNotFoundException;
import com.codeit.weatherfit.domain.clothes.exception.InvalidClothesAttributeOptionException;
import com.codeit.weatherfit.domain.clothes.repository.ClothesAttributeRepository;
import com.codeit.weatherfit.domain.clothes.repository.ClothesAttributeTypeRepository;
import com.codeit.weatherfit.domain.clothes.repository.ClothesRepository;
import com.codeit.weatherfit.domain.clothes.repository.SelectableValueRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.global.s3.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)class ClothesServiceImplTest {

    @InjectMocks
    private ClothesServiceImpl service;

    @Mock
    private ClothesRepository clothesRepository;
    @Mock private UserRepository userRepository;
    @Mock private SelectableValueRepository selectableValueRepository;
    @Mock private ClothesAttributeTypeRepository clothesAttributeTypeRepository;
    @Mock private ClothesAttributeRepository clothesAttributeRepository;
    @Mock private S3Service s3Service;
    @Mock private ApplicationEventPublisher eventPublisher;

    @Nested
    class Create {

        @Test
        @DisplayName("생성 성공")
        void success() {
            UUID userId = UUID.randomUUID();
            User user = mock(User.class);

            ClothesCreateRequest request =
                    new ClothesCreateRequest(userId, "티셔츠", ClothesType.TOP, null);

            Clothes saved = Clothes.create(user, "티셔츠", ClothesType.TOP, null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(clothesRepository.save(any())).thenReturn(saved);
            when(clothesAttributeRepository.findByClothes(any())).thenReturn(List.of());

            ClothesDto result = service.create(request, null);

            assertThat(result).isNotNull();
            verify(clothesRepository).save(any());
        }

        @Test
        @DisplayName("유저 없음")
        void user_not_found() {
            UUID userId = UUID.randomUUID();

            ClothesCreateRequest request =
                    new ClothesCreateRequest(userId, "티셔츠", ClothesType.TOP, null);

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.create(request, null))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    class Update {

        @Test
        @DisplayName("수정 성공 - 이미지 없음")
        void success_no_image() {
            UUID clothesId = UUID.randomUUID();

            Clothes clothes = mock(Clothes.class);
            User owner = mock(User.class);

            when(owner.getId()).thenReturn(UUID.randomUUID());
            when(clothes.getOwner()).thenReturn(owner);
            when(clothes.getName()).thenReturn("기존옷");
            when(clothes.getType()).thenReturn(ClothesType.TOP);

            when(clothesRepository.findById(clothesId))
                    .thenReturn(Optional.of(clothes));
            when(clothesAttributeRepository.findByClothes(any()))
                    .thenReturn(List.of());

            ClothesUpdateRequest request =
                    new ClothesUpdateRequest("후드티", ClothesType.TOP, null);

            service.update(clothesId, request, null);

            verify(clothes).update(any(), any(), any());
        }

        @Test
        @DisplayName("수정 성공 - 이미지 있음")
        void success_with_image() throws Exception {
            UUID clothesId = UUID.randomUUID();

            Clothes clothes = mock(Clothes.class);
            User owner = mock(User.class);

            when(owner.getId()).thenReturn(UUID.randomUUID());
            when(clothes.getOwner()).thenReturn(owner);

            MultipartFile file = mock(MultipartFile.class);
            when(file.isEmpty()).thenReturn(false);
            when(file.getOriginalFilename()).thenReturn("img.png");
            when(file.getContentType()).thenReturn("image/png");
            when(file.getBytes()).thenReturn(new byte[]{1});

            when(clothesRepository.findById(clothesId))
                    .thenReturn(Optional.of(clothes));
            when(clothesAttributeRepository.findByClothes(any()))
                    .thenReturn(List.of());

            ClothesUpdateRequest request =
                    new ClothesUpdateRequest(null, null, null);

            service.update(clothesId, request, file);

            verify(s3Service).put(any(), any(), any());
        }

        @Test
        @DisplayName("옷 없음")
        void not_found() {
            UUID id = UUID.randomUUID();

            when(clothesRepository.findById(id))
                    .thenReturn(Optional.empty());

            ClothesUpdateRequest request =
                    new ClothesUpdateRequest(null, null, null);

            assertThatThrownBy(() -> service.update(id, request, null))
                    .isInstanceOf(ClothesNotFoundException.class);
        }

        @Test
        @DisplayName("속성 타입 없음")
        void attribute_type_not_found() {

            UUID clothesId = UUID.randomUUID();
            UUID defId = UUID.randomUUID();

            Clothes clothes = mock(Clothes.class);

            ClothesAttributeDto attr =
                    new ClothesAttributeDto(defId, "RED");

            ClothesUpdateRequest request =
                    new ClothesUpdateRequest(null, null, List.of(attr));

            when(clothesRepository.findById(clothesId))
                    .thenReturn(Optional.of(clothes));

            when(clothesAttributeTypeRepository.findById(defId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(clothesId, request, null))
                    .isInstanceOf(ClothesAttributeTypeNotFoundException.class);
        }

        @Test
        @DisplayName("옵션 없음")
        void invalid_option() {

            UUID clothesId = UUID.randomUUID();
            UUID defId = UUID.randomUUID();

            Clothes clothes = mock(Clothes.class);
            ClothesAttributeType type = mock(ClothesAttributeType.class);

            ClothesAttributeDto attr =
                    new ClothesAttributeDto(defId, "RED");

            ClothesUpdateRequest request =
                    new ClothesUpdateRequest(null, null, List.of(attr));

            when(clothesRepository.findById(clothesId))
                    .thenReturn(Optional.of(clothes));

            when(clothesAttributeTypeRepository.findById(defId))
                    .thenReturn(Optional.of(type));

            when(selectableValueRepository
                    .findByClothesAttributeTypeAndOption(type, "RED"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(clothesId, request, null))
                    .isInstanceOf(InvalidClothesAttributeOptionException.class);
        }
    }

    @Nested
    class Delete {

        @Test
        @DisplayName("삭제 성공")
        void success() {
            UUID id = UUID.randomUUID();
            Clothes clothes = mock(Clothes.class);

            when(clothesRepository.findById(id))
                    .thenReturn(Optional.of(clothes));

            service.delete(id);

            verify(clothesRepository).delete(clothes);
            verify(clothesAttributeRepository).deleteByClothes(clothes);
        }

        @Test
        @DisplayName("삭제 실패 - 없음")
        void fail_not_found() {
            UUID id = UUID.randomUUID();

            when(clothesRepository.findById(id))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(id))
                    .isInstanceOf(ClothesNotFoundException.class);
        }
    }

}