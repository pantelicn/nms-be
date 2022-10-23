package com.opdev.message;

import com.opdev.config.security.SpELAuthorizationExpressions;
import com.opdev.dto.AvailableChatViewDto;
import com.opdev.model.chat.AvailableChat;
import com.opdev.model.user.User;
import com.opdev.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/available-chats/{username}")
public class AvailableChatController {

    private final AvailableChatService availableChatService;
    private final UserService userService;

    @PreAuthorize(SpELAuthorizationExpressions.AS_MATCHING_TALENT_OR_COMPANY)
    @GetMapping(params = "search")
    public Page<AvailableChatViewDto> get(@PathVariable String username, @RequestParam String search, Pageable pageable) {
        User currentUser = userService.getLoggedInUser();
        Page<AvailableChat> availableChats = availableChatService.searchAvailableChats(username, search, currentUser.getType(), pageable);
        return availableChats.map(AvailableChatViewDto::new);
    }

    @PreAuthorize(SpELAuthorizationExpressions.AS_MATCHING_TALENT_OR_COMPANY)
    @GetMapping
    public Page<AvailableChatViewDto> get(@PathVariable String username, Pageable pageable) {
        User currentUser = userService.getLoggedInUser();
        Page<AvailableChat> availableChats = availableChatService.getAvailableChats(username, currentUser.getType(), pageable);
        return availableChats.map(AvailableChatViewDto::new);
    }

}
