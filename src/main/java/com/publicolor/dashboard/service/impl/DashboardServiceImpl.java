package com.publicolor.dashboard.service.impl;

import com.publicolor.dashboard.dto.DashboardResponse;
import com.publicolor.dashboard.service.DashboardService;
import com.publicolor.finance.expense.repository.EgresoRepository;
import com.publicolor.finance.income.repository.IngresoManualRepository;
import com.publicolor.job.model.EstadoCuenta;
import com.publicolor.job.repository.TrabajoRepository;
import com.publicolor.job.service.TrabajoService;
import com.publicolor.payment.repository.PagoRepository;
import com.publicolor.shared.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final TrabajoRepository trabajoRepo;
    private final PagoRepository pagoRepo;
    private final IngresoManualRepository ingresoRepo;
    private final EgresoRepository egresoRepo;
    private final TrabajoService trabajoService;

    @Override
    public DashboardResponse obtener() {
        LocalDate today = TimeUtil.today();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);

        BigDecimal soldToday = trabajoRepo.sumVendidoEnFecha(today);
        BigDecimal soldMonth = trabajoRepo.sumVendidoEntreFechas(firstDayOfMonth, today);
        BigDecimal soldHistoric = trabajoRepo.sumVendidoHistorico();
        BigDecimal paidHistoric = pagoRepo.sumCashAmountHistorico();
        BigDecimal pendingTotal = soldHistoric.subtract(paidHistoric);

        BigDecimal receivedToday = pagoRepo.sumCashAmountEnFecha(today).add(ingresoRepo.sumAmountEnFecha(today));
        BigDecimal receivedMonth = pagoRepo.sumCashAmountEntreFechas(firstDayOfMonth, today)
                .add(ingresoRepo.sumAmountEntreFechas(firstDayOfMonth, today));

        BigDecimal expensesToday = egresoRepo.sumAmountEnFecha(today);
        BigDecimal expensesMonth = egresoRepo.sumAmountEntreFechas(firstDayOfMonth, today);

        long openAccounts = trabajoRepo.countByStatus(EstadoCuenta.ABIERTA);

        return DashboardResponse.builder()
                .soldToday(soldToday)
                .soldMonth(soldMonth)
                .soldHistoric(soldHistoric)
                .pendingTotal(pendingTotal)
                .receivedToday(receivedToday)
                .receivedMonth(receivedMonth)
                .expensesToday(expensesToday)
                .expensesMonth(expensesMonth)
                .balanceToday(receivedToday.subtract(expensesToday))
                .balanceMonth(receivedMonth.subtract(expensesMonth))
                .openAccounts(openAccounts)
                .recentJobs(trabajoService.listarRecientes())
                .build();
    }
}
