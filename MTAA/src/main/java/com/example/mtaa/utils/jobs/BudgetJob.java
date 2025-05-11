package com.example.mtaa.utils.jobs;


import com.example.mtaa.service.BudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BudgetJob {

    private final BudgetService budgetService;

    @Scheduled(cron = "${budgetResetJob.value}")
    public void budgetResetJob(){
        log.info("Cron time reached - reseting budgets");
        budgetService.resetBudgets();
    }
}
