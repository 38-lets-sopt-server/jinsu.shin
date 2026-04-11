package org.sopt.repository;

import org.sopt.domain.Post;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository {
    private final List<Post> postList = new ArrayList<>();
    private Long nextId = 1L;

    public Post save(Post post) {
        postList.add(post);
        return post;
    }

    public Long generateId() {
        return nextId++;
    }

    public List<Post> findAll() {
        return new ArrayList<>(postList);
    }

    public Optional<Post> findById(Long id) {
        return postList.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public boolean deleteById(Long id) {
        return postList.removeIf(p -> p.getId().equals(id));
    }
}
