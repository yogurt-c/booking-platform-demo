package io.yugurt.booking_platform.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record CursorPageRequest(
    String cursor,

    @Min(value = 1, message = "페이지 크기는 최소 1 이상이어야 합니다")
    @Max(value = 100, message = "페이지 크기는 최대 100 이하여야 합니다")
    Integer size
) {
    public CursorPageRequest(String cursor, Integer size) {
        this.cursor = cursor;
        this.size = (size != null) ? size : 20;
    }
}
