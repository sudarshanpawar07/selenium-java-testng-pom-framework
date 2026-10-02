package testdata;

/**
 * Test data for a single employee creation run.
 *
 * <p>Deliberately immutable: the data provider builds these once and TestNG hands the
 * same instance to the test, so there is nothing to mutate.
 */
public final class EmployeeData {

    private final String name;
    private final String age;
    private final String salary;
    private final String duration;
    private final String grade;
    private final String emailPrefix;

    public EmployeeData(String name, String age, String salary,
                        String duration, String grade, String emailPrefix) {
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.duration = duration;
        this.grade = grade;
        this.emailPrefix = emailPrefix;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getSalary() {
        return salary;
    }

    public String getDuration() {
        return duration;
    }

    public String getGrade() {
        return grade;
    }

    public String getEmailPrefix() {
        return emailPrefix;
    }
}