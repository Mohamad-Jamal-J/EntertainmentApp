package bzu.android.mohamad.jaradat1193265.entertainmentapp;

import androidx.annotation.NonNull;


public class Account implements Comparable<Account>{
    String name;
    String email ;
    String password;

    public Account(String name,String email, String password) {
        setName(name);
        setEmail(email);
        setPassword(password);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public int compareTo(Account o) { //if the two has same email then they're equal
        return this.email.compareToIgnoreCase(o.email);
    }

    @NonNull
    @Override
    public String toString() {
        return "email: "+email + " password: "+password;
    }

}
