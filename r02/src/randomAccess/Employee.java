package randomAccess;

import java.time.LocalDate;

public class Employee
{
    public static final int NAME_SIZE = 40;
	public static final long RECORD_SIZE = 100;
	private String name;
	private double salary;
	private LocalDate hireDay;

	public Employee()
	{
		name = "BRAK";
		salary = 0;
		hireDay = LocalDate.of(1970, 1, 1);
	}

	public Employee(String n, double s, int yr, int m, int d)
	{
		name = n;
		salary = s;
		hireDay = LocalDate.of(yr, m, d);
	}

	public String getName()
	{
		return name;
	}

	public double getSalary()
	{
		return salary;
	}

	public LocalDate getHireDay()
	{
		return hireDay;
	}

	public void raiseSalary(double pr)
	{
		salary *= (1.0 + pr / 100);
	}

	@Override
	public String toString()
	{
		return "Employee{" +
				"name='" + name + '\'' +
				", salary=" + salary +
				", hireDay=" + hireDay +
				'}';
	}
}
