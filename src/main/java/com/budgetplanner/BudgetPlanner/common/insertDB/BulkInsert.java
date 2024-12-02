package com.budgetplanner.BudgetPlanner.common.insertDB;

import com.budgetplanner.BudgetPlanner.expense.entity.Expense;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BulkInsert {

    private final JdbcTemplate jdbcTemplate;

    public void insert(List<Expense> expenses) {

        String itemSql = "INSERT INTO EXPENSE (exclude_total_expenses, expenses, id, spending_time, " +
                "user_id, category, memo) VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(itemSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Expense expense = expenses.get(i);
                ps.setTimestamp(1, Timestamp.valueOf(expense.getSpendingTime()));
                ps.setLong(2, expense.getExpenses());
                ps.setString(3, expense.getCategory().name());
                ps.setString(4, expense.getMemo());
                ps.setLong(5, expense.getUser().getId());
                ps.setBoolean(6, expense.isExcludeTotalExpenses());

            }

            @Override
            public int getBatchSize() {
                return expenses.size();
            }
        });
    }
}
