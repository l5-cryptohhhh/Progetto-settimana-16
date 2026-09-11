package org.example.progettosettimana16.entity;

import org.example.progettosettimana16.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocationTest {

    private static void assertBadRequest(ThrowingSupplier<Location> call) {
        assertThatThrownBy(call::get)
                .isInstanceOfSatisfying(ApiException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void returnsNullWhenNoLocationIsProvided() {
        assertThat(Location.of(null, null, null)).isNull();
    }

    @Test
    void returnsNullWhenOnlyBlankAddressIsProvided() {
        assertThat(Location.of(null, null, "   ")).isNull();
    }

    @Test
    void keepsCoordinatesAndTrimmedAddress() {
        Location location = Location.of(41.8902, 12.4922, "  Piazza del Colosseo, Roma  ");

        assertThat(location.getLatitude()).isEqualTo(41.8902);
        assertThat(location.getLongitude()).isEqualTo(12.4922);
        assertThat(location.getAddress()).isEqualTo("Piazza del Colosseo, Roma");
    }

    @Test
    void acceptsCoordinatesWithoutAddressAndStoresBlankAddressAsNull() {
        Location location = Location.of(45.4642, 9.19, " ");

        assertThat(location.getLatitude()).isEqualTo(45.4642);
        assertThat(location.getLongitude()).isEqualTo(9.19);
        assertThat(location.getAddress()).isNull();
    }

    @Test
    void acceptsBoundaryCoordinates() {
        Location location = Location.of(-90.0, 180.0, null);

        assertThat(location.getLatitude()).isEqualTo(-90.0);
        assertThat(location.getLongitude()).isEqualTo(180.0);
    }

    @Test
    void rejectsLatitudeWithoutLongitude() {
        assertBadRequest(() -> Location.of(41.89, null, null));
    }

    @Test
    void rejectsLongitudeWithoutLatitude() {
        assertBadRequest(() -> Location.of(null, 12.49, null));
    }

    @Test
    void rejectsLatitudeOutOfRange() {
        assertBadRequest(() -> Location.of(90.5, 12.49, null));
    }

    @Test
    void rejectsLongitudeOutOfRange() {
        assertBadRequest(() -> Location.of(41.89, -180.5, null));
    }

    @Test
    void rejectsAddressWithoutCoordinates() {
        assertBadRequest(() -> Location.of(null, null, "Via Roma 1, Milano"));
    }

    @Test
    void rejectsAddressLongerThan500Characters() {
        assertBadRequest(() -> Location.of(41.89, 12.49, "a".repeat(501)));
    }
}
