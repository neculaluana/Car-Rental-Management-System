public class User {
    protected String Username;
    protected String Password;
    protected int UserId;
    protected int EmployeeId;
    protected int RoleId;

    public User(String username, String password, int userId, int employeeId, int roleId) {
        Username = username;
        Password = password;
        UserId = userId;
        EmployeeId = employeeId;
        RoleId = roleId;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public int getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(int employeeId) {
        EmployeeId = employeeId;
    }

    public int getRoleId() {
        return RoleId;
    }

    public void setRoleId(int roleId) {
        RoleId = roleId;
    }

    @Override
    public String toString() {
        return "User{" +
                "Username='" + Username + '\'' +
                ", Password='" + Password + '\'' +
                ", UserId=" + UserId +
                ", EmployeeId=" + EmployeeId +
                ", RoleId=" + RoleId +
                '}';
    }
}
