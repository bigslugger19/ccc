package org.rmcc.ccc.controller;


        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.HttpStatus;
        import org.springframework.messaging.simp.SimpMessagingTemplate;
        import org.springframework.web.bind.annotation.ExceptionHandler;
        import org.springframework.web.bind.annotation.ResponseStatus;

        import javax.servlet.http.HttpServletRequest;

public class BaseController {

    protected Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    protected GraphDatabase graphDatabase;

    @Autowired
    protected Neo4jTemplate template;

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected CurrencyRepository currencyRepository;
    @Autowired
    protected BalanceRepository balanceRepository;
    @Autowired
    protected MarketRepository marketRepository;
    @Autowired
    protected SettingsRepository settingsRepository;
    @Autowired
    protected AlertRepository alertRepository;
    @Autowired
    protected AlertEventRepository alertEventRepository;
    @Autowired
    protected TradeRepository tradeRepository;
    @Autowired
    protected TrendRepository trendRepository;
    @Autowired
    protected TrendEventRepository trendEventRepository;
    @Autowired
    protected BotRepository botRepository;
    @Autowired
    protected StrategyRepository strategyRepository;
    @Autowired
    protected OhlcRepository ohlcRepository;
    @Autowired
    protected OrderRepository orderRepository;
    @Autowired
    protected FinancialActivityEntryRepository financialActivityEntryRepository;
    @Autowired
    protected AccountActivityEntryRepository accountActivityEntryRepository;
    @Autowired
    protected IndicatorValueRepository indicatorValueRepository;

    @Autowired
    protected PushoverService pushoverService;
    @Autowired
    protected ActivityService activityService;

    @Autowired
    protected SimpMessagingTemplate messagingTemplate;

    @Loggable
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorInfo handleException(HttpServletRequest request, Exception ex) {
        ex.printStackTrace();
        return new ErrorInfo(ex.getMessage(), request.getRequestURL().toString());
    }

    @Loggable
    @ExceptionHandler(TwoFactorException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ErrorInfo handleException(HttpServletRequest request, TwoFactorException ex) {
        return new ErrorInfo("2FA", request.getRequestURL().toString());
    }
}
