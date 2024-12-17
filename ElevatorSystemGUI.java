import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.*;


public class ElevatorSystemGUI {
    // Логгер событий
    private static final Logger logger = Logger.getLogger(ElevatorSystemGUI.class.getName());
    // Очередь для хранения запросов
    private static final Queue<Request> requestQueue = new ConcurrentLinkedQueue<>();
    // Список лифтов, работающих в системе
    private static final List<Elevator> elevators = new ArrayList<>();
    // Хэш-таблица, связывающая лифт с его графическим представлением
    private static final Map<Elevator, JLabel> elevatorGraphics = new HashMap<>();
    // Панель для отображения лифтов
    private static final ElevatorPanel elevatorPanel = new ElevatorPanel();
    // Текстовое поле для лога работы системы
    private static final JTextArea logArea = new JTextArea();
    // Массив меток, отображающих этажи
    private static final JLabel[] floorLabels = new JLabel[10];


    // Геттеры
    public static Queue<Request> getRequestQueue() { return requestQueue; }
    public static Map<Elevator, JLabel> getElevatorGraphics() { return elevatorGraphics; }
    public static ElevatorPanel getElevatorPanel() { return elevatorPanel; }


    // Метод для создания и отображения графического интерфейса
    public static void startGUI() {
        JFrame frame = new JFrame("Система лифтов");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);

        // Панель с лифтами
        frame.add(elevatorPanel, BorderLayout.CENTER);

        // Панель для логов
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BorderLayout());
        logArea.setEditable(false);
        logArea.setRows(10);
        logArea.setColumns(30);
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        frame.add(logPanel, BorderLayout.SOUTH);

        // Панель с подписями этажей
        JPanel floorPanel = new JPanel();
        floorPanel.setLayout(new GridLayout(10, 1));
        for (int i = 9; i >= 0; i--) {
            floorLabels[i] = new JLabel("Этаж " + (i + 1), JLabel.CENTER);
            floorLabels[i].setPreferredSize(new Dimension(100, 40));
            floorPanel.add(floorLabels[i]);
        }
        frame.add(floorPanel, BorderLayout.WEST);

        // Отображение окна
        frame.setVisible(true);

        logger.info("Графический интерфейс запущен.");
    }


    // Метод для запуска системы лифтов
    public static void startElevatorSystem() {
        ExecutorService elevatorPool = Executors.newFixedThreadPool(3);

        // Генерация заявок
        new Thread(new RequestGenerator()).start();

        // Создание графических объектов для лифтов
        for (int i = 1; i <= 3; i++) {
            Elevator elevator = new Elevator(i);
            elevators.add(elevator);
            JLabel elevatorLabel = elevatorPanel.createElevatorLabel(i);
            elevatorGraphics.put(elevator, elevatorLabel);
            elevatorPool.submit(elevator);
        }

        // Закрытие ресурсов
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            elevatorPool.shutdown();
            try {
                if (!elevatorPool.awaitTermination(10, TimeUnit.SECONDS)) {
                    elevatorPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                elevatorPool.shutdownNow();
            }
        }));

    }


    // Генератор заявок
    static class RequestGenerator implements Runnable {
        @Override
        public void run() {
            Random random = new Random();
            while (true) {
                try {
                    // Случайный выбор этажа, с которого вызван лифт
                    int fromFloor = random.nextInt(10) + 1;

                    // Случайный выбор этажа назначения
                    int toFloor = random.nextInt(10) + 1;
                    while (toFloor == fromFloor) {
                        toFloor = random.nextInt(10) + 1;
                    }

                    // Заявка с начальным и конечным этажом
                    Request request = new Request(fromFloor, toFloor);
                    // Добавление заявки в очередь
                    requestQueue.add(request);

                    log("Новая заявка: с этажа " + fromFloor + " на этаж " + toFloor);

                    // Задержка перед созданием следующей заявки
                    Thread.sleep(random.nextInt(3000) + 1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // Логирование событий
    public static void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
        logger.info(message);
    }
}
