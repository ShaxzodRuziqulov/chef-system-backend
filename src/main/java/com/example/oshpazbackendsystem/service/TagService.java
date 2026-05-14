package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.response.TagDto;
import com.example.oshpazbackendsystem.entity.Tag;
import com.example.oshpazbackendsystem.mapper.TagMapper;
import com.example.oshpazbackendsystem.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository repository;
    private final TagMapper mapper;

    public TagDto create(TagDto response) {
        return mapper.toDto(repository.save(mapper.toEntity(response)));
    }

    public TagDto update(TagDto dto) {
        Tag tag = mapper.toEntity(dto);
        return mapper.toDto(repository.save(tag));
    }

    public List<TagDto> findAll() {
        return mapper.toDto(repository.findAll());
    }

    public TagDto findById(Long id) {
        return mapper.toDto(findByTagId(id));
    }

    public TagDto deleteById(Long id) {
        Tag tag = findByTagId(id);
        repository.delete(tag);
        return mapper.toDto(tag);
    }

    public Tag findByTagId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("tog not found"));
    }
}
