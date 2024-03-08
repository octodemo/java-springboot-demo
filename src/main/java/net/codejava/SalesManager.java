package net.codejava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SalesManager {

	public static void main(String[] args) {
		SpringApplication.run(SalesManager.class, args);
	}

	// A simple static method to print a greeting
	public static void printGreeting() {
		System.out.println("Hello, welcome to Sales Manager!");
	}

	// A simple static method to calculate the sum of two numbers
	public static int sum(int a, int b) {
		return a + b;
	}
}