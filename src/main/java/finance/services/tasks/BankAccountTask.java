package finance.services.tasks;

import finance.services.ServiceType;

public enum BankAccountTask implements AutoUpdateTask {
    BALANCE {
        @Override
        public ServiceType getOwner() {
            return ServiceType.BANK_ACCOUNT;
        }

        @Override
        public String getDescription() {
            return "Пересчет баланса.";
        }
    }
}
