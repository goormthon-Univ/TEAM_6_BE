package com.LikeCloud.LikeCloud.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CloudType {
    MINICLOUD(2,"MINICLOUD"),
    BIGCLOUD(3,"BIGCLOUD");

    private final Integer num;
    private final String CloudType;
}
