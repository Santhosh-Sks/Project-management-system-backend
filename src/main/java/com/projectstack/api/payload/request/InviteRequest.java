
package com.projectstack.api.payload.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class InviteRequest {
    @NotEmpty
    private List<String> emails;
    
    @NotEmpty
    private String role;

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
