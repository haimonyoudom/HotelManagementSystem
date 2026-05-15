package hotel.service;

public abstract class BaseService {

    protected abstract String getServiceName();

    protected void logAction(String action) {
        System.out.println("[" + getServiceName() + "] " + action);
    }
}