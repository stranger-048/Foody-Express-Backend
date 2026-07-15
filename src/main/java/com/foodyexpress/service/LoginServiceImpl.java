package com.foodyexpress.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.foodyexpress.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodyexpress.exception.LoginException;
import com.foodyexpress.repository.CustomerRepo;
import com.foodyexpress.repository.AdminRepo;
import com.foodyexpress.repository.CurrentUserSessionRepo;

import net.bytebuddy.utility.RandomString;

@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private CustomerRepo customerRepo;

	@Autowired
	private AdminRepo adminRepo;

	@Autowired
	private CurrentUserSessionRepo sessionRepo;

    @Override
    public LoginResponseDTO loginAccount(LoginDTO loginDTO)
            throws LoginException {

        if (loginDTO.getRole().equalsIgnoreCase("customer")) {

            Customer customer =
                    customerRepo.findByEmail(loginDTO.getEmail());

            if (customer == null) {
                throw new LoginException("Invalid email");
            }

            if (!customer.getPassword()
                    .equals(loginDTO.getPassword())) {

                throw new LoginException(
                        "Please Enter a valid password"
                );
            }

            CurrentUserSession currentSession =
                    sessionRepo.findByEmail(loginDTO.getEmail());

            if (currentSession != null) {
                throw new LoginException(
                        "User already logged-In!"
                );
            }

            CurrentUserSession currentUserSession =
                    new CurrentUserSession();

            currentUserSession.setEmail(loginDTO.getEmail());
            currentUserSession.setLoginDateTime(LocalDateTime.now());
            currentUserSession.setRole("customer");

            String privateKey = RandomString.make(6);

            currentUserSession.setPrivateKey(privateKey);

            sessionRepo.save(currentUserSession);

            return new LoginResponseDTO(
                    "Login Successful!",
                    privateKey,
                    customer.getCustomerId(),
                    "customer"
            );

        } else if (loginDTO.getRole().equalsIgnoreCase("admin")) {

            Admin admin =
                    adminRepo.findByEmail(loginDTO.getEmail());

            if (admin == null) {
                throw new LoginException("Invalid email");
            }

            if (!admin.getPassword()
                    .equals(loginDTO.getPassword())) {

                throw new LoginException(
                        "Please Enter a valid password"
                );
            }

            CurrentUserSession currentSession =
                    sessionRepo.findByEmail(loginDTO.getEmail());

            if (currentSession != null) {
                throw new LoginException(
                        "User already logged-In!"
                );
            }

            CurrentUserSession currentUserSession =
                    new CurrentUserSession();

            currentUserSession.setEmail(loginDTO.getEmail());
            currentUserSession.setLoginDateTime(LocalDateTime.now());
            currentUserSession.setRole("admin");

            String privateKey = RandomString.make(6);

            currentUserSession.setPrivateKey(privateKey);

            sessionRepo.save(currentUserSession);

            return new LoginResponseDTO(
                    "Login Successful!",
                    privateKey,
                    admin.getAdminId(),
                    "admin"
            );
        }

        throw new LoginException("Invalid role");
    }

	@Override
	public String logoutAccount(String role, String key) throws LoginException {

		if (role.equalsIgnoreCase("customer")) {

			CurrentUserSession currSession = sessionRepo.findByPrivateKey(key);
			if (currSession == null)
				throw new LoginException("Invalid key");

			if (currSession.getRole().equalsIgnoreCase("customer")) {

				sessionRepo.delete(currSession);
				return "Logged Out!";

			} else
				throw new LoginException("Invalid role");

		} else if (role.equalsIgnoreCase("admin")) {

			CurrentUserSession currSession = sessionRepo.findByPrivateKey(key);
			if (currSession == null)
				throw new LoginException("Invalid key");

			if (currSession.getRole().equalsIgnoreCase("admin")) {

				sessionRepo.delete(currSession);
				return "Logged Out!";

			} else
				throw new LoginException("Invalid role");

		} else
			throw new LoginException("Invalid role");
	}
}
