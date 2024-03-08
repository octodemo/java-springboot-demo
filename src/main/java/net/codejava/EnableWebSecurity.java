package net.codejava;

public @interface EnableWebSecurity {
    String message() default "Web security enabled";
    int securityLevel() default 1;
}