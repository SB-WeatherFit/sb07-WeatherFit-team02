package com.codeit.weatherfit.domain.user.controller;

import com.codeit.weatherfit.domain.user.dto.response.UserDto;
import com.codeit.weatherfit.domain.user.dto.response.UserDtoCursorResponse;
import com.codeit.weatherfit.domain.user.entity.UserRole;
import com.codeit.weatherfit.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private MockMvc mockMvc;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = Mockito.mock(UserService.class);

        UserController userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("계정 목록 조회에 성공한다")
    void getUsers() throws Exception {
        UUID userId = UUID.randomUUID();

        UserDto userDto = new UserDto(
                userId,
                Instant.parse("2026-03-12T09:15:17.989434Z"),
                "test@test.com",
                "tester",
                UserRole.USER,
                false
        );

        UserDtoCursorResponse response = UserDtoCursorResponse.of(
                List.of(userDto),
                null,
                null,
                false,
                1L,
                "createdAt",
                "DESCENDING"
        );

        when(userService.getUsers(
                isNull(),
                isNull(),
                eq(20),
                eq("createdAt"),
                eq("DESCENDING"),
                isNull(),
                isNull(),
                isNull()
        )).thenReturn(response);

        mockMvc.perform(get("/api/users")
                        .param("limit", "20")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "DESCENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(userId.toString()))
                .andExpect(jsonPath("$.data[0].email").value("test@test.com"))
                .andExpect(jsonPath("$.data[0].name").value("tester"))
                .andExpect(jsonPath("$.data[0].role").value("USER"))
                .andExpect(jsonPath("$.data[0].locked").value(false))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.sortBy").value("createdAt"))
                .andExpect(jsonPath("$.sortDirection").value("DESCENDING"));
    }

    @Test
    @DisplayName("사용자 권한 수정에 성공한다")
    void updateRole() throws Exception {
        UUID userId = UUID.randomUUID();

        UserDto response = new UserDto(
                userId,
                Instant.parse("2026-03-12T09:15:17.989434Z"),
                "admin@test.com",
                "admin",
                UserRole.ADMIN,
                false
        );

        when(userService.updateRole(eq(userId), eq(new com.codeit.weatherfit.domain.user.dto.request.UserRoleUpdateRequest(UserRole.ADMIN))))
                .thenReturn(response);

        mockMvc.perform(patch("/api/users/{userId}/role", userId)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "role": "ADMIN"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("사용자 잠금 상태 수정에 성공한다")
    void updateLock() throws Exception {
        UUID userId = UUID.randomUUID();

        UserDto response = new UserDto(
                userId,
                Instant.parse("2026-03-12T09:15:17.989434Z"),
                "locked@test.com",
                "locked-user",
                UserRole.USER,
                true
        );

        when(userService.updateLock(eq(userId), eq(new com.codeit.weatherfit.domain.user.dto.request.UserLockUpdateRequest(true))))
                .thenReturn(response);

        mockMvc.perform(patch("/api/users/{userId}/lock", userId)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "locked": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.locked").value(true));
    }
}