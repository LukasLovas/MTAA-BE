package com.example.mtaa.utils.jobs;


import com.example.mtaa.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BudgetJob {

    private final BudgetService budgetService;

    @Scheduled(cron = "${budgetResetJob.value}")
    public void budgetResetJob(){
        budgetService.resetBudgets();
    }
}
