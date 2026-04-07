package org.sopt.repository;

import org.sopt.domain.Post;
import org.sopt.exception.PostNotFoundException;

import java.util.ArrayList;
import java.util.List;

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

    public Post findById(Long id) {
        return postList.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    public void deleteById(Long id) {
        Post post = findById(id);
        postList.remove(post);
    }
}
