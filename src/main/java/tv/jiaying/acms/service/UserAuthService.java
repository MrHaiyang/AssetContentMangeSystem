package tv.jiaying.acms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tv.jiaying.acms.entity.SystemUser;
import tv.jiaying.acms.entity.repository.SystemUserRepository;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserAuthService implements UserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(UserAuthService.class);

    @Resource
    SystemUserRepository systemUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SystemUser user = systemUserRepository.getFirstByUsername(username);
        if(user==null){
            return null;
        }else
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                List<GrantedAuthority> authoritys = new LinkedList<GrantedAuthority>();;

                if ("admin".equals(user.getRole().name())) {

                    authoritys.add(new GrantedAuthority() {

                        @Override
                        public String getAuthority() {
                            return "ADMIN";
                        }
                    });
                } else if ("user".equals(user.getRole().name())) {
                    authoritys.add(new GrantedAuthority() {

                        @Override
                        public String getAuthority() {
                            return "USER";
                        }
                    });
                } else if("superAdmin".equals(user.getRole().name())){
                    authoritys.add(new GrantedAuthority() {

                        @Override
                        public String getAuthority() {
                            return "SUPER";
                        }
                    });
                }
                return authoritys;
            }

            @Override
            public String getPassword() {
                return user.getPassword();
            }

            @Override
            public String getUsername() {
                return user.getUsername();
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return user.getEnable();
            }
        };

    }
}
