package com.emptrack.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class EmpTrackApiApplication {

	public static void main(String[] args) {
//		// ✅ Step 1 — Print hash (remove after copying!)
//		String hash = new BCryptPasswordEncoder().encode("12345@Sk");
//		System.out.println("==========================================");
//		System.out.println("BCrypt Hash: " + hash);
//		System.out.println("==========================================");

		SpringApplication.run(EmpTrackApiApplication.class, args);}
}
