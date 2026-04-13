package com.codeit.weatherfit.domain.feed.event;

import java.util.UUID;

public record FeedDeletedEvent (
        UUID feedId
){
}
