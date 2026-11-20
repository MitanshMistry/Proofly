package com.ledgerly.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LedgerlyUserDetailsService implements UserDetailsService {
    private final UserAccountRepository users;

    public LedgerlyUserDetailsService(UserAccountRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.findByEmail(username.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }
}
