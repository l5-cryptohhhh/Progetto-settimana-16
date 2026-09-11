package org.example.progettosettimana16.controller;

import lombok.RequiredArgsConstructor;
import org.example.progettosettimana16.dto.GeocodingResult;
import org.example.progettosettimana16.service.GeocodingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Geocoding per associare una posizione a un post: ricerca per indirizzo o punto scelto sulla mappa.
 */
@RestController
@RequestMapping("/api/geocoding")
@RequiredArgsConstructor
public class GeocodingController {

    private final GeocodingService geocodingService;

    @GetMapping("/search")
    public List<GeocodingResult> search(@RequestParam("q") String query) {
        return geocodingService.search(query);
    }

    @GetMapping("/reverse")
    public GeocodingResult reverse(@RequestParam("lat") double latitude, @RequestParam("lon") double longitude) {
        return geocodingService.reverse(latitude, longitude);
    }
}
