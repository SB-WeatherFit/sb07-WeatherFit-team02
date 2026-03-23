package com.codeit.weatherfit.domain.clothes.service;

import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefGetRequest;
import com.codeit.weatherfit.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherfit.domain.clothes.entity.ClothesAttributeType;
import com.codeit.weatherfit.domain.clothes.entity.SelectableValue;
import com.codeit.weatherfit.domain.clothes.repository.ClothesAttributeTypeRepository;
import com.codeit.weatherfit.domain.clothes.repository.SelectableValueRepository;
import jakarta.validation.constraints.NotBlank;
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

    @Override
    public void deleteAttributeDef(UUID defId) {
        //todo : customException 작업
        typeRepository.deleteById(defId);
    }

    @Override
    public ClothesAttributeDefDto patchAttributeDef(UUID defId, ClothesAttributeDefUpdateRequest request) {
        List<String> stringList = request.selectableValues();
        String newName = request.name();
        ClothesAttributeType attributeType = typeRepository.findById(defId).orElseThrow();
        attributeType.updateName(newName);
        valueRepository.deleteSelectableValuesByType(defId);
        List<SelectableValue> selectableValueList = stringList.stream()
                .map(
                        x -> SelectableValue.create(attributeType, x)
                )
                .toList();
        ;
        List<SelectableValue> savedSelectableValues = valueRepository.saveAll(selectableValueList);

        return ClothesAttributeDefDto.from(attributeType, savedSelectableValues);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClothesAttributeDefDto> getAll() {
        return typeRepository.findAll().stream()
                .map(type -> {

                    List<SelectableValue> values =
                            valueRepository.findByClothesAttributeType(type);

                    return ClothesAttributeDefDto.from(type, values);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClothesAttributeDefDto> getAttributeDefs(ClothesAttributeDefGetRequest request) {

        return List.of();
    }


}
