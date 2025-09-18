package gov.uk.ets.registry.api.common.security;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsedTokenService {

    private final UsedTokenRepository repository;

    public void saveToken(String token, Date expiredAt) {
        UsedToken usedToken = new UsedToken();
        usedToken.setToken(token);
        usedToken.setExpiredAt(expiredAt);
        usedToken.setCreatedAt(new Date());

        repository.save(usedToken);
    }

    public boolean isTokenAlreadyUsed(String token) {
        return repository.findByToken(token).isPresent();
    }

    @Transactional
    public void deleteExpiredTokens() {
        repository.deleteAllByExpiredAtBefore(new Date());
    }
}
