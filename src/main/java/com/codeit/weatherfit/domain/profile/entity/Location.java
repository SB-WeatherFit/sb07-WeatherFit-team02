package com.codeit.weatherfit.domain.profile.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "x")
    private Integer x;

    @Column(name = "y")
    private Integer y;

    @ElementCollection
    @CollectionTable(
            name = "profile_location_names",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "location_name")
    private List<String> locationNames = new ArrayList<>();

    private Location(
            Double latitude,
            Double longitude,
            Integer x,
            Integer y,
            List<String> locationNames
    ) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.x = x;
        this.y = y;
        this.locationNames = locationNames == null ? new ArrayList<>() : new ArrayList<>(locationNames);
    }

    public static Location create(
            Double latitude,
            Double longitude,
            Integer x,
            Integer y,
            List<String> locationNames
    ) {
        return new Location(latitude, longitude, x, y, locationNames);
    }

    public static Location empty() {
        return new Location(null, null, null, null, new ArrayList<>());
    }
}