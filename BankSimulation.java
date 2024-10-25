import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class Bankomat implements Runnable {
    private static final int WORKING_HOURS = 10;  // Банкомат працює 10 секунд
    private Semaphore sem;  // Семафор для контролю доступу до банкоматів
    private String clientName;

    public Bankomat(Semaphore sem, String clientName) {
        this.sem = sem;
        this.clientName = clientName;
    }

    @Override
    public void run() {
        try {
            // Імітуємо спробу отримати доступ до банкомату
            if (sem.tryAcquire(5, TimeUnit.SECONDS)) {  // Очікуємо до 5 секунд на доступ
                System.out.println(clientName + " отримав доступ до банкомату.");
                Thread.sleep(2000);  // Імітація зняття готівки
                System.out.println(clientName + " завершив транзакцію.");
                sem.release();  // Вивільняємо банкомат
            } else {
                System.out.println(clientName + " не зміг отримати доступ до банкомату. Банк закритий.");
            }
        } catch (InterruptedException e) {
            System.out.println(clientName + " був перерваний.");
        }
    }
}

public class BankSimulation {
    public static void main(String[] args) throws InterruptedException {
        Semaphore sem = new Semaphore(3);  // У банку є 3 банкомати

        Thread bankOperation = new Thread(() -> {
            try {
                System.out.println("Банк відкрито.");
                Thread.sleep(1 * 1000);
                System.out.println("Банк закрито.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        bankOperation.start();

        // Імітуємо клієнтів, що приходять до банку
        for (int i = 1; i <= 10; i++) {
            Thread client = new Thread(new Bankomat(sem, "Клієнт " + i));
            client.start();
            Thread.sleep(1000);  // Новий клієнт приходить кожну секунду
        }

        bankOperation.join();  // Чекаємо закінчення робочого часу банку
    }
}
