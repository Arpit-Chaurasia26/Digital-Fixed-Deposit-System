package tech.zeta.Digital_Fixed_Deposit_System.service.support;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tech.zeta.Digital_Fixed_Deposit_System.config.security.CurrentUserProvider;
import tech.zeta.Digital_Fixed_Deposit_System.dto.support.SupportTicketRequestDTO;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.Role;
import tech.zeta.Digital_Fixed_Deposit_System.entity.user.User;
import tech.zeta.Digital_Fixed_Deposit_System.mapper.SupportTicketMapper;
import tech.zeta.Digital_Fixed_Deposit_System.repository.FixedDepositRepository;
import tech.zeta.Digital_Fixed_Deposit_System.repository.SupportTicketRepository;
import tech.zeta.Digital_Fixed_Deposit_System.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DisplayName("SupportTicketServiceImpl Smoke Tests")
class SupportTicketServiceImplTest {

    @Test
    void createTicket_rejectsAdmin() {
        SupportTicketRepository repository = Mockito.mock(SupportTicketRepository.class);
        SupportTicketMapper mapper = Mockito.mock(SupportTicketMapper.class);
        CurrentUserProvider currentUserProvider = Mockito.mock(CurrentUserProvider.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        FixedDepositRepository fdRepository = Mockito.mock(FixedDepositRepository.class);

        SupportTicketServiceImpl service = new SupportTicketServiceImpl(
                repository,
                mapper,
                currentUserProvider,
                userRepository,
                fdRepository
        );

        User admin = new User("Admin", "a@example.com", "pw", Role.ADMIN);
        when(currentUserProvider.getCurrentUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> service.createTicket(new SupportTicketRequestDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Admins cannot create");
    }
}

