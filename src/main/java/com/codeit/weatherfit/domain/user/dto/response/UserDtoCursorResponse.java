package com.codeit.weatherfit.domain.user.dto.response;

import java.util.List;
import java.util.UUID;

public record UserDtoCursorResponse(
        List<UserDto> data,
        String nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        long totalCount,
        String sortBy,
        String sortDirection
) {
    public static UserDtoCursorResponse of(
            List<UserDto> data,
            String nextCursor,
            UUID nextIdAfter,
            boolean hasNext,
            long totalCount,
            String sortBy,
            String sortDirection
    ) {
        return new UserDtoCursorResponse(
                data,
                nextCursor,
                nextIdAfter,
                hasNext,
                totalCount,
                sortBy,
                sortDirection
        );
    }
}