package com.example.oshpazbackendsystem.mapper;

import com.example.oshpazbackendsystem.dto.TagRequest;
import com.example.oshpazbackendsystem.dto.response.TagResponse;
import com.example.oshpazbackendsystem.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TagMapper {

    @Mapping(target = "id", ignore = true)
    Tag toEntity(TagRequest request);

    TagResponse toResponse(Tag tag);

    @Mapping(target = "id", ignore = true)
    void updateEntity(TagRequest request, @MappingTarget Tag tag);
}
