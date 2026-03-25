package com.codeit.weatherfit.domain.clothes.service;

import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesCreateRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesDto;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesDtoCursorResponse;
import com.codeit.weatherfit.domain.clothes.entity.*;
import com.codeit.weatherfit.domain.clothes.exception.ClothesAttributeTypeNotFoundException;
import com.codeit.weatherfit.domain.clothes.exception.ClothesAttributeValueMissingException;
import com.codeit.weatherfit.domain.clothes.exception.ClothesNotFoundException;
import com.codeit.weatherfit.domain.clothes.exception.InvalidClothesAttributeOptionException;
import com.codeit.weatherfit.domain.clothes.repository.ClothesAttributeRepository;
import com.codeit.weatherfit.domain.clothes.repository.ClothesAttributeTypeRepository;
import com.codeit.weatherfit.domain.clothes.repository.ClothesRepository;
import com.codeit.weatherfit.domain.clothes.repository.SelectableValueRepository;
import com.codeit.weatherfit.domain.user.entity.User;
import com.codeit.weatherfit.domain.user.repository.UserRepository;
import com.codeit.weatherfit.global.exception.ErrorCode;
import com.codeit.weatherfit.global.s3.S3Service;
import com.codeit.weatherfit.global.s3.event.S3ClothesPutEvent;
import com.codeit.weatherfit.global.s3.exception.S3UploadException;
import com.codeit.weatherfit.global.s3.util.S3KeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClothesServiceImpl implements ClothesService {
    private final ClothesRepository clothesRepository;
    private final UserRepository userRepository;
    private final SelectableValueRepository selectableValueRepository;
    private final ClothesAttributeTypeRepository clothesAttributeTypeRepository;
    private final ClothesAttributeRepository clothesAttributeRepository;
    private final S3Service s3Service;
    private final ApplicationEventPublisher eventPublisher;

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
        clothes.updateImageKey(publishImageUploadEvent(clothes.getId(), image));

        if (request.attributes() != null) {
            for (ClothesAttributeDefCreateRequest attr : request.attributes()) {

                UUID definitionId = UUID.fromString(attr.name());

                ClothesAttributeType type =
                        clothesAttributeTypeRepository.findById(definitionId)
                                .orElseThrow(() -> new ClothesAttributeTypeNotFoundException(ErrorCode.CLOTHES_ATTRIBUTE_TYPE_NOT_FOUND));

                if (attr.selectableValues() == null || attr.selectableValues().isEmpty()) {
                    throw new ClothesAttributeValueMissingException(ErrorCode.CLOTHES_ATTRIBUTE_VALUE_MISSING);
                }

                String value = attr.selectableValues().get(0);

                SelectableValue selectableValue =
                        selectableValueRepository
                                .findByClothesAttributeTypeAndOption(type, value)
                                .orElseThrow(() -> new InvalidClothesAttributeOptionException(ErrorCode.INVALID_CLOTHES_ATTRIBUTE_OPTION));

                ClothesAttribute clothesAttribute =
                        ClothesAttribute.create(clothes, selectableValue);

                clothesAttributeRepository.save(clothesAttribute);
            }
        }

        // 조회용
        List<ClothesAttribute> attributes =
                clothesAttributeRepository.findByClothes(clothes);
        String url = clothes.getImageKey() == null? null : s3Service.getUrl(clothes.getImageKey());

        return ClothesDto.from(clothes, attributes, url);
    }

    @Override
    @Transactional
    public ClothesDto update(UUID clothesId, ClothesUpdateRequest request, MultipartFile image) {
        Clothes clothes = clothesRepository.findById(clothesId)
                .orElseThrow(() -> new ClothesNotFoundException(ErrorCode.CLOTHES_NOT_FOUND));

        clothes.update(
                request.name() != null ? request.name() : clothes.getName(),
                request.type() != null ? request.type() : clothes.getType(),
                image != null ? publishImageUploadEvent(clothes.getId(), image) : clothes.getImageKey()
        );

        if (request.attributes() != null) {
            for (ClothesAttributeDefUpdateRequest attr : request.attributes()) {
                UUID definitionId = UUID.fromString(attr.name());

                ClothesAttributeType type = clothesAttributeTypeRepository.findById(definitionId)
                        .orElseThrow(() -> new ClothesAttributeTypeNotFoundException(ErrorCode.CLOTHES_ATTRIBUTE_TYPE_NOT_FOUND));

                if (attr.selectableValues() == null || attr.selectableValues().size() != 1) {
                    throw new InvalidClothesAttributeOptionException(ErrorCode.INVALID_CLOTHES_ATTRIBUTE_OPTION);
                }

                String value = attr.selectableValues().get(0);

                SelectableValue selectableValue =
                        selectableValueRepository
                                .findByClothesAttributeTypeAndOption(type, value)
                                .orElseThrow(() -> new InvalidClothesAttributeOptionException(ErrorCode.INVALID_CLOTHES_ATTRIBUTE_OPTION));

                ClothesAttribute attribute =
                        clothesAttributeRepository
                                .findByClothesAndOption_ClothesAttributeType(clothes, type)
                                .orElseThrow(() -> new ClothesAttributeTypeNotFoundException(ErrorCode.CLOTHES_ATTRIBUTE_TYPE_NOT_FOUND));

                attribute.changeOption(selectableValue);
            }
        }

        List<ClothesAttribute> attributes =
                clothesAttributeRepository.findByClothes(clothes);
        String url = clothes.getImageKey() == null? null : s3Service.getUrl(clothes.getImageKey());

        return ClothesDto.from(clothes, attributes, url);
    }

    @Override
    @Transactional
    public void delete(UUID clothesId) {
        Clothes clothes = clothesRepository.findById(clothesId)
                .orElseThrow(() -> new ClothesNotFoundException(ErrorCode.CLOTHES_NOT_FOUND));

        clothesAttributeRepository.deleteByClothes(clothes);
        clothesRepository.delete(clothes);
    }

    @Override
    @Transactional(readOnly = true)
    public ClothesDtoCursorResponse search(UUID ownerId, String cursor, UUID idAfter, ClothesType type, int size) {

        Pageable pageable = PageRequest.of(0, size + 1);

        List<Clothes> clothesList;

        if (cursor == null) {
            clothesList = clothesRepository
                    .findByOwner_IdOrderByCreatedAtDescIdDesc(ownerId, pageable);
        } else {
            Instant cursorTime = Instant.parse(cursor);

            clothesList = clothesRepository.search(
                    ownerId,
                    cursorTime,
                    idAfter,
                    type,
                    size
            );
        }

        boolean hasNext = clothesList.size() > size;

        List<Clothes> page = hasNext
                ? clothesList.subList(0, size)
                : clothesList;

        List<ClothesDto> data = page.stream()
                .map(clothes -> {
                    List<ClothesAttribute> attributes =
                            clothesAttributeRepository.findByClothes(clothes);
                    String url = clothes.getImageKey() == null? null : s3Service.getUrl(clothes.getImageKey());
                    return ClothesDto.from(clothes, attributes, url);
                })
                .toList();

        Clothes last = page.isEmpty() ? null : page.getLast();

        String nextCursor = last != null ? last.getCreatedAt().toString() : null;
        UUID nextIdAfter = last != null ? last.getId() : null;

        int totalCount = (int) clothesRepository.countByOwner_Id(ownerId);

        return new ClothesDtoCursorResponse(
                data,
                nextCursor,
                nextIdAfter,
                hasNext,
                totalCount,
                "createdAt",
                "DESCENDING"
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clearImageKey(UUID clothesId) {
        Clothes clothes = clothesRepository.findById(clothesId)
                .orElseThrow(() -> new ClothesNotFoundException(ErrorCode.CLOTHES_NOT_FOUND));
        clothes.updateImageKey(null);
    }

    private String publishImageUploadEvent(UUID id, MultipartFile image) {
        String key = null;
        if (image != null && !image.isEmpty()) {
            try {
                key = S3KeyGenerator.generateKey(image.getOriginalFilename());
                eventPublisher.publishEvent(new S3ClothesPutEvent(id, key, image.getContentType(), image.getBytes()));
            } catch (IOException e) {
                throw new S3UploadException(image.getOriginalFilename());
            }
        }
        return key;
    }
}
