package com.bprflavorshub.bpr_flavors_hub.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bprflavorshub.bpr_flavors_hub.entity.Role;
import com.bprflavorshub.bpr_flavors_hub.entity.User;
import com.bprflavorshub.bpr_flavors_hub.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeData(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            /*
             * ============================================================
             * ADMIN ACCOUNT
             * ============================================================
             *
             * Phone is the primary login identifier.
             *
             * Admin login:
             * Phone    : 9391902028
             * Password : Reddy09
             *
             * Owner approval is handled by the admin.
             */

            User admin = userRepository.findByPhone("9391902028")
                    .orElse(null);

            if (admin == null) {

                admin = new User();

                admin.setFullName("BPR Flavors Hub Admin");
                admin.setEmail("admin@bprflavorshub.com");
                admin.setPhone("9391902028");
                admin.setPassword(passwordEncoder.encode("Reddy09"));
                admin.setAddress("BPR Flavors Hub");
                admin.setRole(Role.ADMIN);

                /*
                 * No OTP is required.
                 * Admin is already approved.
                 */
                admin.setPhoneVerified(true);
                admin.setApproved(true);

                userRepository.save(admin);

                System.out.println();
                System.out.println("==========================================");
                System.out.println("       ADMIN ACCOUNT CREATED");
                System.out.println("==========================================");
                System.out.println("Phone    : 9391902028");
                System.out.println("Password : Reddy09");
                System.out.println("Role     : ADMIN");
                System.out.println("Approved : true");
                System.out.println("==========================================");
                System.out.println();

            } else {

                /*
                 * Existing admin.
                 * Make sure the account remains ADMIN and approved.
                 */
                boolean changed = false;

                if (admin.getRole() != Role.ADMIN) {
                    admin.setRole(Role.ADMIN);
                    changed = true;
                }

                if (!admin.isApproved()) {
                    admin.setApproved(true);
                    changed = true;
                }

                if (!admin.isPhoneVerified()) {
                    admin.setPhoneVerified(true);
                    changed = true;
                }

                if (changed) {
                    userRepository.save(admin);
                }

                System.out.println();
                System.out.println("==========================================");
                System.out.println("       ADMIN ACCOUNT ALREADY EXISTS");
                System.out.println("==========================================");
                System.out.println("Phone : " + admin.getPhone());
                System.out.println("Role  : " + admin.getRole());
                System.out.println("Approved : " + admin.isApproved());
                System.out.println("==========================================");
                System.out.println();
            }
        };
    }
}