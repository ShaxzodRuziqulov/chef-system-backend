package com.example.oshpazbackendsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformFeaturesDto {

    private List<String> publicFeatures;
    private List<String> memberFeatures;
    private List<String> bloggerFeatures;
}
