package com.codeit.weatherfit.domain.profile.location;

import org.springframework.stereotype.Component;

@Component
public class WeatherGridCoordinateCalculator {

    private static final double RE = 6371.00877;
    private static final double GRID = 5.0;
    private static final double SLAT1 = 30.0;
    private static final double SLAT2 = 60.0;
    private static final double OLON = 126.0;
    private static final double OLAT = 38.0;
    private static final double XO = 43.0;
    private static final double YO = 136.0;

    public WeatherGridCoordinate convert(double latitude, double longitude) {
        double degrad = Math.PI / 180.0;

        double re = RE / GRID;
        double slat1 = SLAT1 * degrad;
        double slat2 = SLAT2 * degrad;
        double olon = OLON * degrad;
        double olat = OLAT * degrad;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);

        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;

        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + latitude * degrad * 0.5);
        ra = re * sf / Math.pow(ra, sn);

        double theta = longitude * degrad - olon;
        if (theta > Math.PI) {
            theta -= 2.0 * Math.PI;
        }
        if (theta < -Math.PI) {
            theta += 2.0 * Math.PI;
        }
        theta *= sn;

        int x = (int) Math.floor(ra * Math.sin(theta) + XO + 0.5);
        int y = (int) Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

        return new WeatherGridCoordinate(x, y);
    }
}