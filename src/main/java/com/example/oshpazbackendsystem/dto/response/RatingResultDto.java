package com.example.oshpazbackendsystem.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RatingResultDto {
    private final int     myScore;       // 0 = hali baholalmagan
    private final double  averageRating;
    private final long    ratingCount;
}
