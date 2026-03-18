package com.codeit.weatherfit.domain.clothes.service;

import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesDto;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesCreateRequest;
import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.entity.ClothesAttribute;
import com.codeit.weatherfit.domain.clothes.entity.ClothesAttributeType;
import com.codeit.weatherfit.domain.clothes.entity.SelectableValue;
import com.codeit.weatherfit.domain.clothes.repository.ClothesAttributeRepository;
import com.codeit.weatherfit.domain.clothes.repository.ClothesAttributeTypeRepository;
import com.codeit.weatherfit.domain.clothes.repository.ClothesRepository;
import com.codeit.weatherfit.domain.clothes.repository.SelectableValueRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClothesServiceImpl implements ClothesService {
    private final ClothesRepository clothesRepository;
    private final UserRepository userRepository;
    private final SelectableValueRepository selectableValueRepository;
    private final ClothesAttributeTypeRepository clothesAttributeTypeRepository;
    private final ClothesAttributeRepository clothesAttributeRepository;

    @Override
    @Transactional
    public ClothesDto create(ClothesCreateRequest request, MultipartFile image) {
        User owner = userRepository.findById(request.ownerId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다.")); // 나중에 커스텀 예외 처리

        Clothes clothes = Clothes.create(
                owner,
                request.name(),
                request.type()
        );
        clothesRepository.save(clothes);
        List<ClothesAttribute> attributes =
                clothesAttributeRepository.findByClothes(clothes);

        return ClothesDto.from(clothes, attributes);
    }

    @Override
    @Transactional
    public ClothesDto update(UUID clothesId, ClothesUpdateRequest request) {
        Clothes clothes = clothesRepository.findById(clothesId)
                .orElseThrow(() -> new IllegalArgumentException("옷을 찾을 수 없습니다.")); // 나즁에 커스텀 예외

        clothes.update(
                request.name(),
                request.type()
        );

        for (ClothesAttributeDefUpdateRequest attr : request.attributes()) {
            ClothesAttributeType type = clothesAttributeTypeRepository.findByName(attr.name())
                    .orElseThrow(() -> new IllegalArgumentException("옷 타입을 찾을 수 없습니다."));
            List<SelectableValue> options =
                    selectableValueRepository.findByClothesAttributeTypeAndOptionIn(
                            type, attr.selectableValues()
                    );
            ClothesAttribute attribute = clothesAttributeRepository
                    .findByClothesAndOption_ClothesAttributeType(
                            clothes,
                            type
                    ).orElseThrow(() -> new IllegalArgumentException("옷 속성을 찾을 수 없습니다"));

            attribute.changeOption(options.get(0));
        }

        List<ClothesAttribute> attributes =
                clothesAttributeRepository.findByClothes(clothes);

        return ClothesDto.from(clothes, attributes);
    }

    @Override
    public List<ClothesDto> getClothes() {
        return List.of();
    }

    @Override
    public ClothesDto getClothes(UUID clothesId) {
        return null;
    }
}
