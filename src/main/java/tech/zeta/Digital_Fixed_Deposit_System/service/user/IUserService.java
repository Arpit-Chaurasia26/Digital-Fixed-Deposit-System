package tech.zeta.Digital_Fixed_Deposit_System.service.user;

import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UserProfileResponse;

public interface IUserService {

    UserProfileResponse getCurrentUserProfile();
}
