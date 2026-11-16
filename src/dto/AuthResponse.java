package dto;

public class AuthResponse {
    private boolean success;
    private String message;
    private String token;
    private String role;
    private int userId;
    private String namaLengkap;
    private String email;

    public AuthResponse() {}

    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public AuthResponse(boolean success, String message, String token, String role, int userId, String namaLengkap, String email) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.namaLengkap = namaLengkap;
        this.email = email;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}