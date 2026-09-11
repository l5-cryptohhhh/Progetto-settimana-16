package org.example.progettosettimana16.controller;

import lombok.RequiredArgsConstructor;
import org.example.progettosettimana16.dto.PageResponse;
import org.example.progettosettimana16.dto.PostResponse;
import org.example.progettosettimana16.entity.PhotoSource;
import org.example.progettosettimana16.entity.User;
import org.example.progettosettimana16.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * Crea un post (multipart/form-data).
     * source=CAMERA con una sola foto, oppure source=UPLOAD con da 1 a 10 foto (campo "photos" ripetuto).
     * latitude/longitude/address sono opzionali e si riferiscono al post, non alle singole foto.
     */
    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse create(@AuthenticationPrincipal User user,
                               @RequestParam("source") PhotoSource source,
                               @RequestParam(name = "photos", required = false) List<MultipartFile> photos,
                               @RequestParam(name = "caption", required = false) String caption,
                               @RequestParam(name = "latitude", required = false) Double latitude,
                               @RequestParam(name = "longitude", required = false) Double longitude,
                               @RequestParam(name = "address", required = false) String address) {
        return postService.create(user, source, photos, caption, latitude, longitude, address);
    }

    @GetMapping("/posts")
    public PageResponse<PostResponse> feed(@RequestParam(name = "page", defaultValue = "0") int page,
                                           @RequestParam(name = "size", defaultValue = "10") int size) {
        return postService.feed(page, size);
    }

    @GetMapping("/posts/{id}")
    public PostResponse get(@PathVariable("id") Long id) {
        return postService.get(id);
    }

    @GetMapping("/users/{userId}/posts")
    public PageResponse<PostResponse> postsOfUser(@PathVariable("userId") Long userId,
                                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                                  @RequestParam(name = "size", defaultValue = "10") int size) {
        return postService.postsOfUser(userId, page, size);
    }

    @DeleteMapping("/posts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal User user, @PathVariable("id") Long id) {
        postService.delete(user, id);
    }
}
