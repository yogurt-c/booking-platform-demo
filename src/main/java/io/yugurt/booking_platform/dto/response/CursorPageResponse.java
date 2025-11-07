package io.yugurt.booking_platform.dto.response;

import java.util.List;
import java.util.function.Function;

public record CursorPageResponse<T>(
    List<T> content,
    String nextCursor,
    boolean hasNext,
    int size
) {
    public static <T> CursorPageResponse<T> of(
        List<T> results,
        int requestedSize,
        Function<T, String> cursorExtractor
    ) {
        boolean hasNext = results.size() > requestedSize;
        List<T> content = hasNext ? results.subList(0, requestedSize) : results;
        String nextCursor = hasNext && !content.isEmpty()
            ? cursorExtractor.apply(content.get(content.size() - 1))
            : null;

        return new CursorPageResponse<>(content, nextCursor, hasNext, content.size());
    }
}
