package com.codeit.weatherfit.domain.clothes.service;

import com.codeit.weatherfit.domain.clothes.dto.request.ClothesUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesDto;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesCreateRequest;
import com.codeit.weatherfit.domain.clothes.entity.Clothes;
import com.codeit.weatherfit.domain.clothes.repository.ClothesRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class ClothesServiceImpl implements ClothesService {
    private final ClothesRepository clothesRepository;
    private final UserRepository userRepository;

    public ClothesServiceImpl(ClothesRepository clothesRepository, UserRepository userRepository) {
        this.clothesRepository = clothesRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ClothesDto create(ClothesCreateRequest request, MultipartFile image) {
        User owner = userRepository.findById(request.ownerId())
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다.")); // 나중에 커스텀 예외 처리

        Clothes clothes = new Clothes(
                owner,
                request.name(),
                request.type()
        );
        clothesRepository.save(clothes);
        return ClothesDto.from(clothes);
    }

    @Override
    public ClothesDto update(UUID clothesId, ClothesUpdateRequest request) {
        return null;
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
