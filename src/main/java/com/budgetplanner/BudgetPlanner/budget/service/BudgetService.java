package com.budgetplanner.BudgetPlanner.budget.service;

import com.budgetplanner.BudgetPlanner.budget.dto.BudgetRecommendRequest;
import com.budgetplanner.BudgetPlanner.budget.dto.BudgetRecommendResponse;
import com.budgetplanner.BudgetPlanner.budget.dto.BudgetSettingsRequest;
import com.budgetplanner.BudgetPlanner.budget.dto.CategoriesResponse;
import com.budgetplanner.BudgetPlanner.budget.entity.Budget;
import com.budgetplanner.BudgetPlanner.budget.entity.Category;
import com.budgetplanner.BudgetPlanner.budget.repository.BudgetRepository;
import com.budgetplanner.BudgetPlanner.common.exception.CustomException;
import com.budgetplanner.BudgetPlanner.common.exception.ErrorCode;
import com.budgetplanner.BudgetPlanner.user.entity.User;
import com.budgetplanner.BudgetPlanner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;


@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    @Cacheable(value = "categories", key = "'allCategories'")
    public List<CategoriesResponse> getCategories() {

        return Arrays.stream(Category.values())
                .map(category -> new CategoriesResponse(category.name(), category.getCategoryName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void setting(BudgetSettingsRequest request, Authentication authentication) {

        User user = userRepository.findByAccount(authentication.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        checkTheNumberOfEnteredCategories(request);

        saveBudgets(request, user);
    }

    //모든 카테고리가 입력되야 하므로 갯수를 검사한다.
    private void checkTheNumberOfEnteredCategories(BudgetSettingsRequest request) {
        if (request.getCategoryAndBudget().size() != Category.values().length) {
            throw new CustomException(ErrorCode.CATEGORY_MISSING);
        }
    }

    private void saveBudgets(BudgetSettingsRequest request, User user) {
        request.getCategoryAndBudget().stream()
                .map(createCategoryAndBudget -> Budget.builder()
                        .budget(createCategoryAndBudget.getBudget())
                        .category(createCategoryAndBudget.getCategory())
                        .yearMonth(request.getYearMonth())
                        .user(user)
                        .build())
                .forEach(budgetRepository::save);
    }


    @Cacheable(value = "budget", key = "'recommend'")
    public List<BudgetRecommendResponse> recommend(BudgetRecommendRequest request) {
        List<Object[]> data = budgetRepository.findCategoryAndBudget();
        long[] budgets = data.stream().mapToLong(entry -> ((Number) entry[1]).longValue()).toArray();

        double totalBudget = calculateTotalBudget(budgets);
        double[] ratios = calculateRatios(budgets, totalBudget);

        long userBudget = request.getBudget();

        return IntStream.range(0, data.size())
                .mapToObj(i -> createBudgetRecommendResponse(data.get(i), ratios[i], userBudget))
                .collect(Collectors.toList());
    }

    private BudgetRecommendResponse createBudgetRecommendResponse(Object[] entry, double ratio, long userBudget) {
        String categoryName = entry[0].toString();
        Category category = Category.valueOf(categoryName);
        long recommendedBudget = (long) (ratio * userBudget / 100.0);
        return new BudgetRecommendResponse(category, recommendedBudget);
    }

    private double calculateTotalBudget(long[] budgets) {
        return LongStream.of(budgets).sum();
    }

    private double[] calculateRatios(long[] budgets, double totalBudget) {
        return LongStream.of(budgets)
                .mapToDouble(budget -> budget / totalBudget * 100.0)
                .toArray();
    }


}
