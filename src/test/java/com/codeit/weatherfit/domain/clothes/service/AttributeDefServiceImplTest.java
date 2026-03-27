package com.codeit.weatherfit.domain.clothes.service;

import com.codeit.weatherfit.domain.clothes.dto.request.*;
import com.codeit.weatherfit.domain.clothes.dto.response.*;
import com.codeit.weatherfit.domain.clothes.entity.ClothesAttributeType;
import com.codeit.weatherfit.domain.clothes.entity.SelectableValue;
import com.codeit.weatherfit.domain.clothes.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttributeDefServiceImplTest {

    @InjectMocks
    private AttributeDefServiceImpl service;

    @Mock private ClothesAttributeTypeRepository typeRepository;
    @Mock private SelectableValueRepository valueRepository;
    @Mock private ClothesAttributeRepository repository;

    @Nested
    class Create {

        @Test
        @DisplayName("속성 정의 생성 성공")
        void success() {
            ClothesAttributeDefCreateRequest request =
                    new ClothesAttributeDefCreateRequest(
                            "색상",
                            List.of("RED", "BLUE")
                    );

            ClothesAttributeType type = mock(ClothesAttributeType.class);
            List<SelectableValue> values = List.of(
                    mock(SelectableValue.class),
                    mock(SelectableValue.class)
            );

            when(typeRepository.save(any())).thenReturn(type);
            when(valueRepository.saveAll(any())).thenReturn(values);

            ClothesAttributeDefDto result = service.createAttributeDef(request);

            assertThat(result).isNotNull();
            verify(typeRepository).save(any());
            verify(valueRepository).saveAll(any());
        }
    }

    @Nested
    class Delete {

        @Test
        @DisplayName("삭제 성공")
        void success() {
            UUID id = UUID.randomUUID();
            ClothesAttributeType type = mock(ClothesAttributeType.class);

            when(typeRepository.findById(id)).thenReturn(Optional.of(type));

            service.deleteAttributeDef(id);

            verify(repository).deleteByAttributeType(id);
            verify(valueRepository).deleteSelectableValuesByType(id);
            verify(typeRepository).delete(type);
        }

        @Test
        @DisplayName("삭제 실패 - 없음")
        void fail_not_found() {
            UUID id = UUID.randomUUID();

            when(typeRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deleteAttributeDef(id))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    class Patch {

        @Test
        @DisplayName("수정 성공")
        void success() {
            UUID id = UUID.randomUUID();

            ClothesAttributeType type = mock(ClothesAttributeType.class);

            ClothesAttributeDefUpdateRequest request =
                    new ClothesAttributeDefUpdateRequest(
                            "새이름",
                            List.of("A", "B")
                    );

            List<SelectableValue> savedValues = List.of(
                    mock(SelectableValue.class),
                    mock(SelectableValue.class)
            );

            when(typeRepository.findById(id)).thenReturn(Optional.of(type));
            when(valueRepository.saveAll(any())).thenReturn(savedValues);

            ClothesAttributeDefDto result = service.patchAttributeDef(id, request);

            assertThat(result).isNotNull();

            verify(type).updateName("새이름");
            verify(repository).deleteByAttributeType(id);
            verify(valueRepository).deleteSelectableValuesByType(id);
            verify(valueRepository).saveAll(any());
        }

        @Test
        @DisplayName("수정 실패 - 없음")
        void fail_not_found() {
            UUID id = UUID.randomUUID();

            ClothesAttributeDefUpdateRequest request =
                    new ClothesAttributeDefUpdateRequest(
                            "이름",
                            List.of("A")
                    );

            when(typeRepository.findById(id)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.patchAttributeDef(id, request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    class Get {

        @Test
        @DisplayName("조회 성공 - keyword 없음")
        void success_no_keyword() {

            ClothesAttributeDefGetRequest request =
                    new ClothesAttributeDefGetRequest(
                            SortBy.NAME,
                            SortDirection.ASCENDING,
                            null
                    );

            when(typeRepository.getAttributeDefs(any(), any()))
                    .thenReturn(List.of());

            List<ClothesAttributeDefDto> result = service.getAttributeDefs(request);

            assertThat(result).isNotNull();
            verify(typeRepository).getAttributeDefs(SortBy.NAME, SortDirection.ASCENDING);
        }

        @Test
        @DisplayName("조회 성공 - keyword 있음")
        void success_with_keyword() {

            ClothesAttributeDefGetRequest request =
                    new ClothesAttributeDefGetRequest(
                            SortBy.NAME,
                            SortDirection.ASCENDING,
                            "색상"
                    );

            when(typeRepository.getAttributeDefs(any(), any(), any()))
                    .thenReturn(List.of());

            List<ClothesAttributeDefDto> result = service.getAttributeDefs(request);

            assertThat(result).isNotNull();
            verify(typeRepository).getAttributeDefs(SortBy.NAME, SortDirection.ASCENDING, "색상");
        }
    }
}