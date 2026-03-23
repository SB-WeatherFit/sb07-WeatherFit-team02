package com.codeit.weatherfit.domain.clothes.service;

import com.codeit.weatherfit.domain.clothes.dto.response.ClothesAttributeDefDto;
import com.codeit.weatherfit.domain.clothes.entity.SelectableValue;
import com.codeit.weatherfit.domain.clothes.repository.ClothesAttributeTypeRepository;
import com.codeit.weatherfit.domain.clothes.repository.SelectableValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeDefServiceImpl implements AttributeDefService {
    private final ClothesAttributeTypeRepository typeRepository;
    private final SelectableValueRepository valueRepository;

    @Override
    public List<ClothesAttributeDefDto> getAll() {
        return typeRepository.findAll().stream()
                .map(type -> {


                    List<SelectableValue> values =
                            valueRepository.findByClothesAttributeType(type);

                    return ClothesAttributeDefDto.from(type, values);
                })
                .toList();
    }
}
