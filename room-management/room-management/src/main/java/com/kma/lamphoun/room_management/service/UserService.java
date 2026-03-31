package com.kma.lamphoun.room_management.service;

import com.kma.lamphoun.room_management.dto.request.ChangePasswordRequest;
import com.kma.lamphoun.room_management.dto.request.UpdateProfileRequest;
import com.kma.lamphoun.room_management.dto.response.UserResponse;

public interface UserService {

    /** Xem profile của chính mình (từ token) */
    UserResponse getMyProfile(String username);

    /** Xem profile bất kỳ theo id (public info) */
    UserResponse getUserById(Long id);

    /** Cập nhật profile — chỉ chính chủ */
    UserResponse updateProfile(String username, UpdateProfileRequest request);

    /** Đổi mật khẩu — chỉ chính chủ */
    void changePassword(String username, ChangePasswordRequest request);
}
