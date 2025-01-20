package academy.learnprogramming.service;

import academy.learnprogramming.data.model.UserPrincipal;
import academy.learnprogramming.data.model.Users;
import academy.learnprogramming.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("\n\n\n loadUserByUsername01: "+email+"\n\n\n");
        System.out.println("\n\nChecking if Emails is: "+ email + " " + email.equals("loading172@email.com")+ " loading172@email.com" + "\n\n");
        if(email.equals("loading172@email.com")){
            System.out.println("\n\n\n"+"Email == loading172@email.com"+"\n\n\n");
            }else {
            System.out.println("\n\n\n"+"Wrong email == loading172@email.com"+"\n\n\n");
        }
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("\n\n\n\n\n\n\n\nfail\n\n\n\n\n\n\n"));

        if (user == null) {
            System.out.println("User not found1");
            throw new UsernameNotFoundException("User not found2");
        }else {
            System.out.println("\n\n\n loadUserByUsername002: "+email+"\n\n\n");
            return new UserPrincipal(user);
        }
    }
}
