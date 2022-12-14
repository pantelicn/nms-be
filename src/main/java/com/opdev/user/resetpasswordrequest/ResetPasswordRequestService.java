package com.opdev.user.resetpasswordrequest;

import java.util.UUID;

import com.opdev.model.user.ResetPasswordRequest;
import com.opdev.model.user.User;

public interface ResetPasswordRequestService {

    ResetPasswordRequest create(User user);

    ResetPasswordRequest findByValidityToken(UUID validityToken);

}
