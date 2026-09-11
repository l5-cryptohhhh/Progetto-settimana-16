package org.example.progettosettimana16.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static org.example.progettosettimana16.exception.ApiException.badRequest;

/**
 * Posizione in cui sono state scattate le foto. E' incorporata nella tabella dei post:
 * appartiene al post nel suo insieme e non alle singole foto.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Location {

    public static final int MAX_ADDRESS_LENGTH = 500;

    private Double latitude;

    private Double longitude;

    @Column(length = MAX_ADDRESS_LENGTH)
    private String address;

    /**
     * Crea e valida una posizione. Restituisce null se non e' stata indicata alcuna posizione.
     */
    public static Location of(Double latitude, Double longitude, String address) {
        String cleanAddress = (address == null || address.isBlank()) ? null : address.trim();

        if (latitude == null && longitude == null) {
            if (cleanAddress != null) {
                throw badRequest("Per associare un indirizzo al post servono anche le coordinate (latitude e longitude)");
            }
            return null;
        }
        if (latitude == null || longitude == null) {
            throw badRequest("latitude e longitude devono essere indicate insieme");
        }
        if (!(latitude >= -90 && latitude <= 90)) {
            throw badRequest("latitude deve essere compresa tra -90 e 90");
        }
        if (!(longitude >= -180 && longitude <= 180)) {
            throw badRequest("longitude deve essere compresa tra -180 e 180");
        }
        if (cleanAddress != null && cleanAddress.length() > MAX_ADDRESS_LENGTH) {
            throw badRequest("L'indirizzo non puo' superare " + MAX_ADDRESS_LENGTH + " caratteri");
        }
        return new Location(latitude, longitude, cleanAddress);
    }
}
