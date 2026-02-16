package finance.services.tasks;

import finance.services.ServiceType;

public enum AnalyticTask implements AutoUpdateTask {
    MONEY_FLOWS_FOR_PERIOD {
        @Override
        public ServiceType getOwner() {
            return ServiceType.ANALYTIC;
        }

        @Override
        public String getDescription() {
            return "Расчет доходов, расходов и их разницы за установленный период.";
        }
    },
    INCOME_FOR_PERIOD_BY_CATEGORY {
        public ServiceType getOwner() {
            return ServiceType.ANALYTIC;
        }

        @Override
        public String getDescription() {
            return "Группировка доходов по категориям за установленный период.";
        }
    },
    EXPENSE_FOR_PERIOD_BY_CATEGORY {
        public ServiceType getOwner() {
            return ServiceType.ANALYTIC;
        }

        @Override
        public String getDescription() {
            return "Группировка расходов по категориям за установленный период.";
        }
    }
}
