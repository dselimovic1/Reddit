package com.example.reddit.controllers;

import com.example.reddit.domain.Post;
import com.example.reddit.domain.Topic;
import com.example.reddit.domain.User;
import com.example.reddit.domain.enums.RatingType;
import com.example.reddit.http.response.GenericResponse;
import com.example.reddit.http.request.CreatePostRequest;
import com.example.reddit.service.PostService;
import com.example.reddit.service.TopicService;
import com.example.reddit.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@Secured("ROLE_USER")
public class PostController {

    private final PostService postService;
    private final TopicService topicService;
    private final UserService userService;

    public PostController(PostService postService, TopicService topicService, UserService userService) {
        this.postService = postService;
        this.topicService = topicService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestParam(name = "topic") String topicName,
                                        @Valid @RequestBody CreatePostRequest createPostRequest, Principal principal) {
        Topic topic = topicService.getByName(topicName);
        User user = userService.getByUsername(principal.getName());
        if (!topicService.isSubscribed(topic, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GenericResponse("User must be subscribed to topic"));
        }
        Post createdPost = postService.create(user, topic, createPostRequest.getTitle(), createPostRequest.getText());
        return ResponseEntity.ok(createdPost);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePost(@RequestParam(name = "topic") String topicName,
                                        @RequestParam(name = "post") Long id, Principal principal) {
        Topic topic = topicService.getByName(topicName);
        User user = userService.getByUsername(principal.getName());
        if (!topicService.isSubscribed(topic, user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GenericResponse("User must be subscribed to topic"));
        }
        Optional<Post> post = postService.get(id);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new GenericResponse("Post does not exist"));
        }
        postService.delete(post.get(), user, topic);
        return ResponseEntity.ok(new GenericResponse("Post successfully deleted"));
    }

    @GetMapping({"/", ""})
    public ResponseEntity<?> getPosts(Principal principal) {
        User user = userService.getByUsername(principal.getName());
        return ResponseEntity.ok(user.getPosts());
    }

    @PostMapping("/upvote")
    public ResponseEntity<?> upvote(@RequestParam(name = "post") Long id, Principal principal) {
        Optional<Post> post = postService.get(id);
        User user = userService.getByUsername(principal.getName());
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new GenericResponse("Post does not exist"));
        }
        if (userService.isUpVoted(user, post.get().getId(), RatingType.POST)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new GenericResponse("Post already up voted"));
        }
        return ResponseEntity.ok(postService.upVote(post.get(), user, userService.isDownVoted(user, post.get().getId(), RatingType.POST)));
    }

    @PostMapping("/downvote")
    public ResponseEntity<?> downvote(@RequestParam(name = "post") Long id, Principal principal) {
        Optional<Post> post = postService.get(id);
        User user = userService.getByUsername(principal.getName());
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new GenericResponse("Post does not exist"));
        }
        if (userService.isDownVoted(user, post.get().getId(), RatingType.POST)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new GenericResponse("Post already down voted"));
        }
        return ResponseEntity.ok(postService.downVote(post.get(), user, userService.isUpVoted(user, post.get().getId(), RatingType.POST)));
    }
}
