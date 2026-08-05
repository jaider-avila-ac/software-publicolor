package com.publicolor.dashboard.dto;

import com.publicolor.job.dto.TrabajoResponse;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter @Builder
public class DashboardResponse {
    private BigDecimal soldToday;
    private BigDecimal soldMonth;
    private BigDecimal soldHistoric;
    private BigDecimal pendingTotal;
    private BigDecimal receivedToday;
    private BigDecimal receivedMonth;
    private BigDecimal expensesToday;
    private BigDecimal expensesMonth;
    private BigDecimal balanceToday;
    private BigDecimal balanceMonth;
    private long openAccounts;
    private List<TrabajoResponse> recentJobs;
}
