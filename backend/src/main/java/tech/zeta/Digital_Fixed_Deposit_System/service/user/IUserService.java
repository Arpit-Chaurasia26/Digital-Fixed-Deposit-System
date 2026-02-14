/*
Author : Priyanshu Mishra
*/
package tech.zeta.Digital_Fixed_Deposit_System.service.user;
 
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UserProfileResponse;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.UpdateUserProfileRequest;
import tech.zeta.Digital_Fixed_Deposit_System.dto.auth.ChangePasswordRequest;
 
public interface IUserService {
 
    UserProfileResponse getCurrentUserProfile();
 
    UserProfileResponse updateCurrentUserProfile(UpdateUserProfileRequest request);
 
    void changePassword(ChangePasswordRequest request);
}
 