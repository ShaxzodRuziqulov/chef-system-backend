package com.example.oshpazbackendsystem.service;

import com.example.oshpazbackendsystem.dto.CommentRequest;
import com.example.oshpazbackendsystem.dto.response.CommentDto;
import com.example.oshpazbackendsystem.dto.response.PageResponse;
import com.example.oshpazbackendsystem.entity.Recipe;
import com.example.oshpazbackendsystem.entity.RecipeComment;
import com.example.oshpazbackendsystem.entity.User;
import com.example.oshpazbackendsystem.exception.NotFoundException;
import com.example.oshpazbackendsystem.repository.RecipeCommentRepository;
import com.example.oshpazbackendsystem.repository.RecipeRepository;
import com.example.oshpazbackendsystem.service.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final RecipeCommentRepository commentRepository;
    private final RecipeRepository        recipeRepository;
    private final CurrentUserService      currentUserService;

    @Transactional(readOnly = true)
    public PageResponse<CommentDto> getComments(Long recipeId, Pageable pageable) {
        if (!recipeRepository.existsById(recipeId)) {
            throw new NotFoundException("RECIPE_NOT_FOUND", "Retsept topilmadi: " + recipeId);
        }
        UUID currentUserId = currentUserService.getCurrentUserIdOrNull();
        Page<CommentDto> page = commentRepository
                .findActiveByRecipeId(recipeId, pageable)
                .map(c -> CommentDto.from(c, currentUserId));
        return PageResponse.of(page);
    }

    public CommentDto addComment(Long recipeId, CommentRequest request) {
        User   user   = currentUserService.getCurrentUser();
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NotFoundException("RECIPE_NOT_FOUND", "Retsept topilmadi: " + recipeId));

        RecipeComment comment = RecipeComment.builder()
                .user(user)
                .recipe(recipe)
                .content(request.getContent().trim())
                .build();

        RecipeComment saved = commentRepository.save(comment);
        return CommentDto.from(saved, user.getId());
    }

    // ── Izohni o'chirish (soft-delete) ────────────────────────────────────────

    public void deleteComment(Long recipeId, Long commentId) {
        User user = currentUserService.getCurrentUser();

        RecipeComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("COMMENT_NOT_FOUND", "Izoh topilmadi: " + commentId));

        if (!comment.getRecipe().getId().equals(recipeId)) {
            throw new NotFoundException("COMMENT_NOT_FOUND", "Bu retseptda bunday izoh yo'q");
        }

        // Faqat o'z izi yoki admin o'chira oladi
        boolean isOwner = comment.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() != null &&
                "ADMIN".equals(user.getRole().name());

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Bu izohni o'chirish uchun ruxsat yo'q");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
    }
}
