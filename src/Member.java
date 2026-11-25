public class Member {
    private String name;
    private String department;
    private String email;
    private String phone;

    public Member(String name, String department,
                  String email, String phone) {
        this.name = name;
        this.department = department;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}