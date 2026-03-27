package com.codeit.weatherfit.domain.clothes.service;

import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefGetRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherfit.domain.clothes.dto.response.SortBy;
import com.codeit.weatherfit.domain.clothes.dto.response.SortDirection;
import com.codeit.weatherfit.domain.clothes.entity.ClothesAttributeType;
import com.codeit.weatherfit.domain.clothes.entity.SelectableValue;
import com.codeit.weatherfit.domain.clothes.repository.ClothesAttributeRepository;
import com.codeit.weatherfit.domain.clothes.repository.ClothesAttributeTypeRepository;
import com.codeit.weatherfit.domain.clothes.repository.SelectableValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AttributeDefServiceImpl implements AttributeDefService {
    private final ClothesAttributeTypeRepository typeRepository;
    private final SelectableValueRepository valueRepository;
    private final ClothesAttributeRepository repository;


    @Override
    public void deleteAttributeDef(UUID defId) {

        ClothesAttributeType type = typeRepository.findById(defId)
                .orElseThrow();

        repository.deleteByAttributeType(defId);
        valueRepository.deleteSelectableValuesByType(defId);
        typeRepository.delete(type);
    }

    @Override
    public ClothesAttributeDefDto patchAttributeDef(UUID defId, ClothesAttributeDefUpdateRequest request) {

        ClothesAttributeType attributeType = typeRepository.findById(defId)
                .orElseThrow();

        attributeType.updateName(request.name());
        repository.deleteByAttributeType(defId);
        valueRepository.deleteSelectableValuesByType(defId);

        List<SelectableValue> selectableValueList = request.selectableValues().stream()
                .map(x -> SelectableValue.create(attributeType, x))
                .toList();

        List<SelectableValue> saved =
                valueRepository.saveAll(selectableValueList);

        return ClothesAttributeDefDto.from(attributeType, saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClothesAttributeDefDto> getAttributeDefs(ClothesAttributeDefGetRequest request) {
        SortBy sortBy = request.sortBy();
        SortDirection sortDirection = request.sortDirection();
        String keyword = request.keyword();
        List<ClothesAttributeDefDto> attributeDefDtos;
        if(keyword == null) attributeDefDtos =typeRepository.getAttributeDefs(sortBy, sortDirection);
        else attributeDefDtos = typeRepository.getAttributeDefs(sortBy, sortDirection, keyword);
        return attributeDefDtos;
    }

    @Override
    public ClothesAttributeDefDto createAttributeDef(ClothesAttributeDefCreateRequest request) {
        String name = request.name();
        List<String> stringList = request.selectableValues();

        ClothesAttributeType savedType = typeRepository.save(ClothesAttributeType.create(name));
        List<SelectableValue> selectableValueList = stringList.stream()
                .map(x -> SelectableValue.create(savedType, x))
                .toList();
        List<SelectableValue> savedSelectableValues = valueRepository.saveAll(selectableValueList);

        return ClothesAttributeDefDto.from(savedType, savedSelectableValues);

    }
}
