
package com.shopeasy.model;

public class ExpenseSummary {

    private double todayExpenses;
    private double monthlyExpenses;
    private double totalExpenses;

    public ExpenseSummary() {
    }

    public double getTodayExpenses() {
        return todayExpenses;
    }

    public void setTodayExpenses(double todayExpenses) {
        this.todayExpenses = todayExpenses;
    }

    public double getMonthlyExpenses() {
        return monthlyExpenses;
    }

    public void setMonthlyExpenses(double monthlyExpenses) {
        this.monthlyExpenses = monthlyExpenses;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }
}