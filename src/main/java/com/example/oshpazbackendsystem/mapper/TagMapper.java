package com.example.oshpazbackendsystem.mapper;

import com.example.oshpazbackendsystem.dto.response.TagDto;
import com.example.oshpazbackendsystem.entity.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TagMapper extends EntityMapper<TagDto, Tag> {
}
