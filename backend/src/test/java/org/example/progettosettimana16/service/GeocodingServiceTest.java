package org.example.progettosettimana16.service;

import org.example.progettosettimana16.config.AppProperties;
import org.example.progettosettimana16.dto.GeocodingResult;
import org.example.progettosettimana16.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeocodingServiceTest {

    private final RestClient.Builder builder = RestClient.builder();
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    private final GeocodingService service = new GeocodingService(builder,
            new AppProperties(null, null, null, new AppProperties.Geocoding("https://nominatim.test", "TestAgent/1.0"), null));

    private static void assertApiError(Executable call, HttpStatus expected) {
        assertThatThrownBy(call::execute)
                .isInstanceOfSatisfying(ApiException.class, ex -> assertThat(ex.getStatus()).isEqualTo(expected));
    }

    @Test
    void searchMapsNominatimResultsAndIdentifiesTheApplication() {
        server.expect(requestTo(startsWith("https://nominatim.test/search")))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andExpect(queryParam("q", "Colosseo"))
                .andExpect(queryParam("format", "jsonv2"))
                .andExpect(header("User-Agent", "TestAgent/1.0"))
                .andRespond(withSuccess("""
                        [
                          {"place_id": 1, "lat": "41.8902102", "lon": "12.4922309",
                           "display_name": "Colosseo, Piazza del Colosseo, Roma, Lazio, Italia",
                           "category": "tourism", "type": "attraction", "importance": 0.8},
                          {"place_id": 2, "lat": "45.4654", "lon": "9.1859",
                           "display_name": "Via Colosseo, Milano, Lombardia, Italia",
                           "category": "highway", "type": "residential", "importance": 0.2}
                        ]
                        """, MediaType.APPLICATION_JSON));

        List<GeocodingResult> results = service.search("Colosseo");

        assertThat(results).containsExactly(
                new GeocodingResult("Colosseo, Piazza del Colosseo, Roma, Lazio, Italia", 41.8902102, 12.4922309),
                new GeocodingResult("Via Colosseo, Milano, Lombardia, Italia", 45.4654, 9.1859));
        server.verify();
    }

    @Test
    void searchRejectsBlankOrTooShortQueriesWithoutCallingNominatim() {
        assertApiError(() -> service.search("   "), HttpStatus.BAD_REQUEST);
        assertApiError(() -> service.search("ab"), HttpStatus.BAD_REQUEST);
        assertApiError(() -> service.search(null), HttpStatus.BAD_REQUEST);
        server.verify();
    }

    @Test
    void reverseMapsCoordinatesToAddress() {
        server.expect(requestTo(startsWith("https://nominatim.test/reverse")))
                .andExpect(queryParam("lat", "41.8902"))
                .andExpect(queryParam("lon", "12.4922"))
                .andExpect(queryParam("format", "jsonv2"))
                .andExpect(header("User-Agent", "TestAgent/1.0"))
                .andRespond(withSuccess("""
                        {"place_id": 3, "lat": "41.89021", "lon": "12.49223",
                         "display_name": "Piazza del Colosseo, Monti, Roma, Italia",
                         "address": {"road": "Piazza del Colosseo", "city": "Roma", "country": "Italia"}}
                        """, MediaType.APPLICATION_JSON));

        GeocodingResult result = service.reverse(41.8902, 12.4922);

        assertThat(result).isEqualTo(new GeocodingResult("Piazza del Colosseo, Monti, Roma, Italia", 41.89021, 12.49223));
        server.verify();
    }

    @Test
    void reverseRejectsCoordinatesOutOfRange() {
        assertApiError(() -> service.reverse(95, 12), HttpStatus.BAD_REQUEST);
        assertApiError(() -> service.reverse(41, 190), HttpStatus.BAD_REQUEST);
        server.verify();
    }

    @Test
    void reverseReturnsNotFoundWhenNominatimHasNoAddress() {
        server.expect(requestTo(startsWith("https://nominatim.test/reverse")))
                .andRespond(withSuccess("{\"error\": \"Unable to geocode\"}", MediaType.APPLICATION_JSON));

        assertApiError(() -> service.reverse(0, 0), HttpStatus.NOT_FOUND);
    }

    @Test
    void upstreamFailureBecomesBadGateway() {
        server.expect(requestTo(startsWith("https://nominatim.test/search")))
                .andRespond(withServerError());

        assertApiError(() -> service.search("Roma"), HttpStatus.BAD_GATEWAY);
    }
}
