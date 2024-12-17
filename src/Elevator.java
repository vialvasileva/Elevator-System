import javax.swing.*;

public class Elevator implements Runnable {
    // Уникальный идентификатор лифта
    private final int id;
    // Текущий этаж лифта, по умолчанию 1
    private int currentFloor = 1;


    // Конструктор
    public Elevator(int id) {
        this.id = id;
    }


    // Основной цикл работы лифта
    @Override
    public void run() {
        while (true) {
            try {
                Request request = ElevatorSystemGUI.getRequestQueue().poll();

                // Извлечение заявки из общей очереди и ее обработка
                if (request != null) {
                    processRequest(request);
                }
                // Если заявок нет, подождать 500 мс перед следующей проверкой
                else {
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    // Обработка заявки лифтом
    private void processRequest(Request request) throws InterruptedException {
        ElevatorSystemGUI.log("Лифт " + id + " забрал пассажира с этажа " + request.fromFloor + " и едет на этаж " + request.toFloor);

        // Целевой этаж
        int targetFloor = request.toFloor;

        // Движение лифта в зависимости от расположения текущего этажа относительно целевого
        if (currentFloor < targetFloor) {
            moveUp(targetFloor);
        } else if (currentFloor > targetFloor) {
            moveDown(targetFloor);
        }

        ElevatorSystemGUI.log("Лифт " + id + " достиг этажа " + targetFloor);

        // Обновление текущего этажа
        currentFloor = targetFloor;
    }


    // Метод для подъема лифта на целевой этаж
    private void moveUp(int targetFloor) throws InterruptedException {
        while (currentFloor < targetFloor) {
            currentFloor++;

//            ElevatorSystemGUI.log("Лифт " + id + " поднимается на этаж " + currentFloor);

            Thread.sleep(1000);
            updateElevatorGraphic();
        }
    }


    // Метод для спуска лифта на целевой этаж
    private void moveDown(int targetFloor) throws InterruptedException {
        while (currentFloor > targetFloor) {
            currentFloor--;

//            ElevatorSystemGUI.log("Лифт " + id + " спускается на этаж " + currentFloor);

            Thread.sleep(1000);
            updateElevatorGraphic();
        }
    }


    // Метод для обновления графического интерфейса лифта
    private void updateElevatorGraphic() {
        // Безопасное обновление графики из другого потока
        SwingUtilities.invokeLater(() -> {
            // Метка, представляющая лифт в интерфейсе
            JLabel elevatorLabel = ElevatorSystemGUI.getElevatorGraphics().get(this);

            // Обновление расположения метки на панели в зависимости от текущего этажа
            elevatorLabel.setLocation(50 + id * 100, 450 - (currentFloor * 40));

            // Перерисовка панели лифтов
            ElevatorSystemGUI.getElevatorPanel().repaint();
        });
    }
}
