package org.example.progettosettimana16.dto;

import org.example.progettosettimana16.entity.PhotoSource;
import org.example.progettosettimana16.entity.Post;

import java.time.Instant;
import java.util.List;

public record PostResponse(
        Long id,
        String caption,
        PhotoSource photoSource,
        LocationResponse location,
        List<PhotoResponse> photos,
        AuthorResponse author,
        Instant createdAt) {

    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getCaption(),
                post.getPhotoSource(),
                LocationResponse.from(post.getLocation()),
                post.getPhotos().stream().map(PhotoResponse::from).toList(),
                AuthorResponse.from(post.getAuthor()),
                post.getCreatedAt());
    }
}
