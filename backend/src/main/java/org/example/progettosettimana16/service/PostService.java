package org.example.progettosettimana16.service;

import lombok.RequiredArgsConstructor;
import org.example.progettosettimana16.config.AppProperties;
import org.example.progettosettimana16.dto.PageResponse;
import org.example.progettosettimana16.dto.PostResponse;
import org.example.progettosettimana16.entity.Location;
import org.example.progettosettimana16.entity.PhotoSource;
import org.example.progettosettimana16.entity.Post;
import org.example.progettosettimana16.entity.PostPhoto;
import org.example.progettosettimana16.entity.User;
import org.example.progettosettimana16.exception.ApiException;
import org.example.progettosettimana16.repository.PostRepository;
import org.example.progettosettimana16.repository.UserRepository;
import org.example.progettosettimana16.storage.FileStorageService;
import org.example.progettosettimana16.storage.FileType;
import org.example.progettosettimana16.storage.StorageArea;
import org.example.progettosettimana16.storage.StoredFile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final Set<FileType> ALLOWED_PHOTO_TYPES = EnumSet.of(FileType.JPEG, FileType.PNG, FileType.WEBP);
    private static final int MAX_PAGE_SIZE = 50;
    private static final Sort NEWEST_FIRST = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FileStorageService storage;
    private final AppProperties properties;

    /**
     * Crea un post. Tutte le validazioni (numero di foto, posizione, didascalia, formato e dimensione
     * di ogni foto) avvengono prima di scrivere qualsiasi file: una foto non valida non lascia post a meta'.
     */
    @Transactional
    public PostResponse create(User author, PhotoSource source, List<MultipartFile> photos,
                               String caption, Double latitude, Double longitude, String address) {
        List<MultipartFile> files = photos == null ? List.of()
                : photos.stream().filter(file -> file != null && !file.isEmpty()).toList();
        source.validatePhotoCount(files.size());
        Location location = Location.of(latitude, longitude, address);
        String cleanCaption = normalizeCaption(caption);
        DataSize maxPhotoSize = properties.storage().maxPhotoSize();
        List<FileType> types = files.stream()
                .map(file -> storage.validate(file, ALLOWED_PHOTO_TYPES, maxPhotoSize))
                .toList();

        Post post = new Post();
        post.setAuthor(author);
        post.setPhotoSource(source);
        post.setCaption(cleanCaption);
        post.setLocation(location);

        List<StoredFile> storedFiles = new ArrayList<>();
        try {
            for (int i = 0; i < files.size(); i++) {
                StoredFile stored = storage.store(files.get(i), StorageArea.PHOTOS, types.get(i));
                storedFiles.add(stored);
                PostPhoto photo = new PostPhoto();
                photo.setStoredFilename(stored.storedFilename());
                photo.setOriginalFilename(stored.originalFilename());
                photo.setContentType(stored.type().mimeType());
                photo.setSizeBytes(stored.size());
                post.addPhoto(photo);
            }
            postRepository.saveAndFlush(post);
        } catch (RuntimeException e) {
            storedFiles.forEach(stored -> storage.delete(StorageArea.PHOTOS, stored.storedFilename()));
            throw e;
        }
        return PostResponse.from(post);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> feed(int page, int size) {
        return PageResponse.from(postRepository.findAllBy(pageRequest(page, size)).map(PostResponse::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> postsOfUser(Long userId, int page, int size) {
        if (!userRepository.existsById(userId)) {
            throw ApiException.notFound("Utente non trovato");
        }
        return PageResponse.from(postRepository.findByAuthorId(userId, pageRequest(page, size)).map(PostResponse::from));
    }

    @Transactional(readOnly = true)
    public PostResponse get(Long id) {
        return PostResponse.from(findPost(id));
    }

    @Transactional
    public void delete(User user, Long id) {
        Post post = findPost(id);
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw ApiException.forbidden("Solo l'autore puo' eliminare il post");
        }
        List<String> filenames = post.getPhotos().stream().map(PostPhoto::getStoredFilename).toList();
        postRepository.delete(post);
        postRepository.flush();
        filenames.forEach(filename -> storage.delete(StorageArea.PHOTOS, filename));
    }

    private Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> ApiException.notFound("Post non trovato"));
    }

    private static PageRequest pageRequest(int page, int size) {
        return PageRequest.of(Math.max(page, 0), Math.clamp(size, 1, MAX_PAGE_SIZE), NEWEST_FIRST);
    }

    private static String normalizeCaption(String caption) {
        if (caption == null || caption.isBlank()) {
            return null;
        }
        String clean = caption.strip();
        if (clean.length() > Post.MAX_CAPTION_LENGTH) {
            throw ApiException.badRequest("La didascalia non puo' superare " + Post.MAX_CAPTION_LENGTH + " caratteri");
        }
        return clean;
    }
}
