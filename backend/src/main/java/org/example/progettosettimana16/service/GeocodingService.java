package org.example.progettosettimana16.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.progettosettimana16.config.AppProperties;
import org.example.progettosettimana16.dto.GeocodingResult;
import org.example.progettosettimana16.exception.ApiException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Proxy verso Nominatim (OpenStreetMap): trasforma un indirizzo in coordinate (search)
 * e delle coordinate in un indirizzo (reverse). La policy di Nominatim richiede uno User-Agent identificativo.
 */
@Service
public class GeocodingService {

    private static final int MIN_QUERY_LENGTH = 3;
    private static final int MAX_RESULTS = 5;

    private final RestClient restClient;

    public GeocodingService(RestClient.Builder restClientBuilder, AppProperties properties) {
        this.restClient = restClientBuilder
                .baseUrl(properties.geocoding().baseUrl())
                .defaultHeader(HttpHeaders.USER_AGENT, properties.geocoding().userAgent())
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "it")
                .build();
    }

    public List<GeocodingResult> search(String query) {
        String cleanQuery = query == null ? "" : query.strip();
        if (cleanQuery.length() < MIN_QUERY_LENGTH) {
            throw ApiException.badRequest("La ricerca deve contenere almeno " + MIN_QUERY_LENGTH + " caratteri");
        }
        NominatimPlace[] places = call(() -> restClient.get()
                .uri(uri -> uri.path("/search")
                        .queryParam("q", "{q}")
                        .queryParam("format", "jsonv2")
                        .queryParam("limit", MAX_RESULTS)
                        .build(Map.of("q", cleanQuery)))
                .retrieve()
                .body(NominatimPlace[].class));
        return places == null ? List.of() : Arrays.stream(places).map(NominatimPlace::toResult).toList();
    }

    public GeocodingResult reverse(double latitude, double longitude) {
        if (!(latitude >= -90 && latitude <= 90) || !(longitude >= -180 && longitude <= 180)) {
            throw ApiException.badRequest("Coordinate non valide: latitude tra -90 e 90, longitude tra -180 e 180");
        }
        NominatimPlace place = call(() -> restClient.get()
                .uri(uri -> uri.path("/reverse")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .queryParam("format", "jsonv2")
                        .build())
                .retrieve()
                .body(NominatimPlace.class));
        if (place == null || place.error() != null || place.displayName() == null) {
            throw ApiException.notFound("Nessun indirizzo trovato per queste coordinate");
        }
        return place.toResult();
    }

    private static <T> T call(Supplier<T> request) {
        try {
            return request.get();
        } catch (RestClientException e) {
            throw ApiException.badGateway("Servizio di geocoding non disponibile, riprova piu' tardi");
        }
    }

    /** Sottoinsieme dei campi restituiti da Nominatim (formato jsonv2). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    record NominatimPlace(String lat, String lon, @JsonProperty("display_name") String displayName, String error) {

        GeocodingResult toResult() {
            return new GeocodingResult(displayName, Double.parseDouble(lat), Double.parseDouble(lon));
        }
    }
}
