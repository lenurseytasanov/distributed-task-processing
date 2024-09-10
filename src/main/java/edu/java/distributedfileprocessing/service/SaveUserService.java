package edu.java.distributedfileprocessing.service;

import edu.java.distributedfileprocessing.domain.User;
import edu.java.distributedfileprocessing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;


/**
 * Сервис сохранят пользователя в систему.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SaveUserService extends OidcUserService {

    private final UserRepository userRepository;

    /**
     * После загрузки аутентифицированного пользователя проверят наличие в БД по email.
     * Если отсутствует, то создает и сохраняет пользователя.
     * @param userRequest the user request
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        log.trace("Load user {}", oidcUser);
        String email = oidcUser.getEmail();
        if (email != null) {
            userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User user = new User();
                        user.setEmail(email);
                        user = userRepository.save(user);
                        log.info("Save user {}", user);
                        return user;
                    });
        }
        return oidcUser;
    }

}
