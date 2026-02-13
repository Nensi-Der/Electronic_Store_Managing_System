package clementechModel;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Employee implements Serializable{



    private static final long serialVersionUID = 1L;
    protected static Set<Integer> usedIds = new HashSet<>();
    protected int employeeId;
    protected String firstName;
    protected String lastName;
    protected LocalDate dateOfBirth;

    protected String phone;
    protected String email;
    protected double salary;

    protected String username;
    protected String password;

    protected LocalDate lastLogIn;
    protected boolean isFirstLogin = true;

    protected Set<Permission> permissions;

    protected Employee(int employeeId, String firstName, String lastName, LocalDate dateOfBirth, String phone,
                       String email, double salary)
    {
        if (usedIds.contains(employeeId)) {
            throw new IllegalArgumentException("Employee ID already exists: " + employeeId);
        }
        this.employeeId = employeeId;
        usedIds.add(employeeId);
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.email = email;
        this.salary = salary;
        this.username = firstName +"." + lastName;
        this.password = firstName + "."+lastName;
        this.permissions = new HashSet<>();

    }

    public int getEmployeeId()
    {
        return this.employeeId;
    }
    public boolean needsPasswordChange() {
        return isFirstLogin;
    }
    public String getFullName()
    {
        return this.firstName+" "+this.lastName;
    }
    public void setFullName(String name, String surname)
    {
        if(name != null && surname != null)
        {this.firstName = name;
            this.lastName = surname;}
        else
            throw new IllegalArgumentException("Name and surname cannot be empty.");
    }
    public String getContactInfo()
    {
        return this.phone+" "+this.email;
    }
    public void setContactInfo(String phone, String email) {
        if (phone == null || email == null) {
            throw new IllegalArgumentException("Phone number and email cannot be empty.");
        }
        this.phone = phone;
        this.email = email;
    }
    public void setNewEmployeePhone(String newPhone)
    {
        if(newPhone != null)
            this.phone = newPhone;
        else
            throw new IllegalArgumentException("Phone number cannot be empty.");
    }
    public void changePassword(String oldPassword, String newPassword)
    {
        if (isFirstLogin || this.password.equals(oldPassword)) {
            this.password = newPassword;
            isFirstLogin = false;
            System.out.println("Password changed successfully.");
        } else {
            System.out.println("Wrong password.");
        }
    }
    public String getPassword()
    {
        return this.password;
    }
    public String getUsername()
    {
        return this.username;
    }
    public double getSalary()
    {
        return this.salary;
    }

    public void setSalary(double salary)
    {   if (salary < 400) {
        throw new IllegalArgumentException("Salary cannot be less than 400 dollars.");
    }this.salary = salary;
    }

    public LocalDate getLastLogIn()
    {
        return lastLogIn;
    }
    public void setLastLogIn(LocalDate lastLogIn)
    {

        this.lastLogIn = lastLogIn;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Set<Permission> getPermissions()
    {
        return permissions;
    }


    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }



}
