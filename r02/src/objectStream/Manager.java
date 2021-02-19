package objectStream;

import java.util.*;

public class Manager extends Employee
{

	private double bonus;
	private Employee secretary;

	public Manager(String n, double s, int yr, int m, int d)
	{
		super(n, s, yr, m, d);
		bonus = 0;
		secretary = null;
	}

	public double getSalary()
	{
		double sal = super.getSalary();
		return sal + bonus;
	}

	public void setSecretary(Employee secretary)
	{
		this.secretary = secretary;
	}

	public void setBonus(double b)
	{
		bonus = b;
	}
}
