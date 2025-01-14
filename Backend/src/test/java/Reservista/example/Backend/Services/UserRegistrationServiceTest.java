package Reservista.example.Backend.Services;

import static org.junit.jupiter.api.Assertions.*;


import Reservista.example.Backend.DAOs.UserRepository;
import Reservista.example.Backend.DTOs.Registration.RegistrationRequestDTO;
import Reservista.example.Backend.Enums.ErrorCode;
import Reservista.example.Backend.Error.GlobalException;
import Reservista.example.Backend.Models.EntityClasses.User;
import Reservista.example.Backend.Services.Registration.OTPService;
import Reservista.example.Backend.Services.Registration.UserRegistrationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserRegistrationServiceTest {

    @Autowired
    UserRegistrationService userRegistrationService;

    @MockBean
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private OTPService otpService;

    @Test
    public void whenEmailAlreadyExists_thenUserAccountNotCreated(){


        Mockito.when(userRepository.existsByEmail("mariam@gmail.com")).thenReturn(true);
        Mockito.when(userRepository.findIsActivatedByEmail("mariam@gmail.com")).thenReturn(true);

        RegistrationRequestDTO registrationRequest =
                RegistrationRequestDTO.builder()
                        .email("mariam@gmail.com")
                        .userName("mariam")
                        .build();

        GlobalException exception = assertThrows(GlobalException.class,()->userRegistrationService.registerUser(registrationRequest));

        assertEquals(ErrorCode.EMAIL_ALREADY_EXIST, exception.getErrorCode());

        verify(userRepository,never()).save(any());

    }

    @Test
    public void whenUsernameAlreadyExists_thenUserAccountNotCreated(){

        Mockito.when(userRepository.existsByEmail("mariam@gmail.com")).thenReturn(false);
        Mockito.when(userRepository.existsByUserName("mariam")).thenReturn(true);

        RegistrationRequestDTO registrationRequest =
                RegistrationRequestDTO.builder()
                        .email("mariam@gmail.com")
                        .userName("mariam")
                        .build();

        GlobalException exception = assertThrows(GlobalException.class,()->userRegistrationService.registerUser(registrationRequest));

        assertEquals(ErrorCode.USERNAME_ALREADY_EXIST, exception.getErrorCode());

        verify(userRepository,never()).save(any(User.class));

    }


    @Test
    public void whenUserIsBlocked_thenUserAccountNotCreated(){

        Mockito.when(userRepository.existsByEmail("mariam@gmail.com")).thenReturn(true);
        Mockito.when(userRepository.findIsBlockedByEmail("mariam@gmail.com")).thenReturn(true);

        RegistrationRequestDTO registrationRequest =
                RegistrationRequestDTO.builder()
                        .email("mariam@gmail.com")
                        .userName("mariam")
                        .build();

        GlobalException exception =assertThrows(GlobalException.class,()->userRegistrationService.registerUser(registrationRequest));

        assertEquals(ErrorCode.ACCOUNT_BLOCKED, exception.getErrorCode());

        verify(userRepository,never()).save(any(User.class));

    }


    @Test
    public void whenUserHasADeactivatedAccount_thenUserAccountNotCreatedAgain() throws GlobalException {

        Mockito.when(userRepository.existsByEmail("mariam@gmail.com")).thenReturn(true);
        Mockito.when(userRepository.findIsActivatedByEmail("mariam@gmail.com")).thenReturn(false);
        Mockito.when(userRepository.findIsBlockedByEmail("mariam@gmail.com")).thenReturn(false);
        doNothing().when(otpService).refreshOTP("mariam@gmail.com");


        RegistrationRequestDTO registrationRequest =
                RegistrationRequestDTO.builder()
                        .email("mariam@gmail.com")
                        .userName("mariam")
                        .build();

        GlobalException exception =assertThrows(GlobalException.class,()->userRegistrationService.registerUser(registrationRequest));

        assertEquals(ErrorCode.ACCOUNT_DEACTIVATED, exception.getErrorCode());

        verify(userRepository,never()).save(any(User.class));

    }

    @Test
    public void whenValidUserCredentials_thenUserAccountCreated() throws GlobalException {

        Mockito.when(userRepository.existsByEmail("mariam@gmail.com")).thenReturn(false);
        Mockito.when(userRepository.existsByUserName("mariam")).thenReturn(false);
        Mockito.when(userRepository.findIsActivatedByEmail("mariam@gmail.com")).thenReturn(false);
        Mockito.when(userRepository.findIsBlockedByEmail("mariam@gmail.com")).thenReturn(false);
        Mockito.when(otpService.createAndSendOTP(any(User.class))).thenReturn(true);

        Mockito.when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setPassword(passwordEncoder.encode("password"));
            return savedUser;
        });

        RegistrationRequestDTO registrationRequest =
                RegistrationRequestDTO.builder()
                        .email("mariam@gmail.com")
                        .firstName("mariam")
                        .lastName("elsamni")
                        .userName("samni")
                        .password("password")
                        .build();

        User result = userRegistrationService.registerUser(registrationRequest);
        verify(userRepository, times(1)).save(any());

        assert result.getEmail().equals(registrationRequest.getEmail());

    }
}