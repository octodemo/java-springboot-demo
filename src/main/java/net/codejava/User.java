package net.codejava;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    // getters and setters methods
    // getter for id
    public Long getId() {
        return id;
    }

    // setter for id
    public void setId(Long id) {
        this.id = id;
    }

    // getter for username
    public String getUsername() {
        return username;
    }

    // setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // getter for password
    public String getPassword() {
        return password;
    }

    // setter for password{
    public void setPassword(String password){
        this.password = password;
    }
}