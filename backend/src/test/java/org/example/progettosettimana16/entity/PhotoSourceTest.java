package org.example.progettosettimana16.entity;

import org.example.progettosettimana16.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PhotoSourceTest {

    @Test
    void cameraAcceptsExactlyOnePhoto() {
        assertThatCode(() -> PhotoSource.CAMERA.validatePhotoCount(1)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 5})
    void cameraRejectsAnyCountOtherThanOne(int count) {
        assertThatThrownBy(() -> PhotoSource.CAMERA.validatePhotoCount(count))
                .isInstanceOfSatisfying(ApiException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 10})
    void uploadAcceptsFromOneToTenPhotos(int count) {
        assertThatCode(() -> PhotoSource.UPLOAD.validatePhotoCount(count)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11})
    void uploadRejectsCountsOutsideOneToTen(int count) {
        assertThatThrownBy(() -> PhotoSource.UPLOAD.validatePhotoCount(count))
                .isInstanceOfSatisfying(ApiException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }
}
