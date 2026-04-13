package com.codeit.weatherfit.domain.clothes.service;

import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesCreateRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeDto;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesDto;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesDtoCursorResponse;
import com.codeit.weatherfit.domain.clothes.entity.*;
import com.codeit.weatherfit.domain.clothes.exception.*;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        UUID ownerId = request.ownerId();
        String name = request.name();


        ClothesType cType = request.type();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));// 나중에 커스텀 예외 처리

        String key = null;

        if (image != null && !image.isEmpty()) {
            try {
                key = s3Service.put(
                        image.getOriginalFilename(),
                        image.getContentType(),
                        image.getBytes()
                );

            } catch (IOException e) {
                throw new S3UploadException(image.getOriginalFilename());
            }
        }

        Clothes clothes =
                clothesRepository.save(Clothes.create(
                                owner,
                                name,
                                cType,
                                key
                        )
                );

        List<ClothesAttributeDto> attributeDtos = request.attributes();

        if (attributeDtos != null) {

            for (ClothesAttributeDto attributeDto : attributeDtos) {

                UUID definitionId = attributeDto.definitionId();
                String value = attributeDto.value();
                ClothesAttributeType type =
                        clothesAttributeTypeRepository.findById(definitionId)
                                .orElseThrow(() -> new ClothesAttributeTypeNotFoundException(
                                        ErrorCode.CLOTHES_ATTRIBUTE_TYPE_NOT_FOUND
                                ));

                SelectableValue selectableValue =
                        selectableValueRepository
                                .findByClothesAttributeTypeAndOption(type, value)
                                .orElseThrow(() -> new InvalidClothesAttributeOptionException(
                                        ErrorCode.INVALID_CLOTHES_ATTRIBUTE_OPTION
                                ));

                ClothesAttribute clothesAttribute =
                        ClothesAttribute.create(clothes, selectableValue);

                clothesAttributeRepository.save(clothesAttribute);
            }

        }


        // 조회용
        List<ClothesAttribute> attributes =
                clothesAttributeRepository.findByClothes(clothes);
        String url = clothes.getImageKey() == null ? null : s3Service.getUrl(clothes.getImageKey());

        return ClothesDto.from(clothes, attributes, url);
    }

    @Override
    @Transactional
    public ClothesDto update(UUID clothesId, ClothesUpdateRequest request, MultipartFile image) {

        Clothes clothes = clothesRepository.findById(clothesId)
                .orElseThrow(() -> new ClothesNotFoundException(ErrorCode.CLOTHES_NOT_FOUND));
        String key = clothes.getImageKey();

        if (image != null && !image.isEmpty()) {
            try {
                if (key != null) {
                    s3Service.delete(key);
                }
                key = s3Service.put(
                        image.getOriginalFilename(),
                        image.getContentType(),
                        image.getBytes()
                );

            } catch (IOException e) {
                throw new S3UploadException(image.getOriginalFilename());
            }
        }

        String name = request.name();
        ClothesType type = request.type();

        clothes.update(
                name != null ? name : clothes.getName(),
                type != null ? type : clothes.getType(),
                key
        );

        List<ClothesAttributeDto> attributeDtos = request.attributes();

        if (attributeDtos != null) {
            clothesAttributeRepository.deleteByClothes(clothes);

            for (ClothesAttributeDto attributeDto : attributeDtos) {

                UUID definitionId = attributeDto.definitionId();
                String value = attributeDto.value();
                ClothesAttributeType typeEntity =
                        clothesAttributeTypeRepository.findById(definitionId)
                                .orElseThrow(() -> new ClothesAttributeTypeNotFoundException(
                                        ErrorCode.CLOTHES_ATTRIBUTE_TYPE_NOT_FOUND
                                ));

                SelectableValue selectableValue =
                        selectableValueRepository
                                .findByClothesAttributeTypeAndOption(typeEntity, value)
                                .orElseThrow(() -> new InvalidClothesAttributeOptionException(
                                        ErrorCode.INVALID_CLOTHES_ATTRIBUTE_OPTION
                                ));
                ClothesAttribute newAttr =
                        ClothesAttribute.create(clothes, selectableValue);
                clothesAttributeRepository.save(newAttr);
            }
        }

        List<ClothesAttribute> attributes =
                clothesAttributeRepository.findByClothes(clothes);

        String url = clothes.getImageKey() == null ? null : s3Service.getUrl(clothes.getImageKey());

        return ClothesDto.from(clothes, attributes, url);
    }

    @Override
    @Transactional
    public void delete(UUID clothesId) {
        Clothes clothes = clothesRepository.findById(clothesId)
                .orElseThrow(() -> new ClothesNotFoundException(ErrorCode.CLOTHES_NOT_FOUND));

        clothesAttributeRepository.deleteByClothes(clothes);
        clothesRepository.delete(clothes);
        if (clothes.getImageKey() != null) {
            s3Service.delete(clothes.getImageKey());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ClothesDtoCursorResponse search(
            UUID ownerId,
            String cursor,
            UUID idAfter,
            ClothesType type,
            int size
    ) {

        Instant cursorTime = cursor != null ? Instant.parse(cursor) : null;

        List<Clothes> clothesList = clothesRepository.search(
                ownerId,
                cursorTime,
                idAfter,
                type,
                size
        );

        boolean hasNext = clothesList.size() > size;

        List<Clothes> page = hasNext
                ? clothesList.subList(0, size)
                : clothesList;

        List<ClothesDto> data = page.stream()
                .map(clothes -> {
                    List<ClothesAttribute> attributes =
                            clothesAttributeRepository.findByClothes(clothes);

                    String url = clothes.getImageKey() == null
                            ? null
                            : s3Service.getUrl(clothes.getImageKey());

                    return ClothesDto.from(clothes, attributes, url);
                })
                .toList();

        Clothes last = page.isEmpty() ? null : page.get(page.size() - 1);

        String nextCursor = last != null ? last.getCreatedAt().toString() : null;
        UUID nextIdAfter = last != null ? last.getId() : null;

        int totalCount = (int) clothesRepository.count(ownerId, type);

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

    @Override
    public ClothesDto extractionFromUrl(String url, UUID ownerId) {

        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .header("Accept-Language", "ko-KR,ko;q=0.9")
                    .header("Referer", "https://www.google.com")
                    .timeout(5000)
                    .get();

            String name = doc.select("meta[property=og:title]").attr("content");
            if (name.isEmpty()) {
                name = doc.title();
            }

            String imageUrl = doc.select("meta[property=og:image]").attr("content");

            User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new RuntimeException("유저 없음")); // 커스텀

            Clothes temp = Clothes.create(
                    owner,
                    name,
                    null
            );

            return ClothesDto.from(temp, List.of(), imageUrl);

        } catch (IOException e) {
            throw new ClothesExtractionException(ErrorCode.URL_PARSING_FAILED);
        }
    }
}
