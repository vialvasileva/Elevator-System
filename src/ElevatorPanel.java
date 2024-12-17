import javax.swing.*;
import java.awt.*;


public class ElevatorPanel extends JPanel {
    // Список меток, представляющих лифты на графической панели


    // Конструктор
    public ElevatorPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(400, 500));
    }


    // Метод для создания графического представления лифта
    public JLabel createElevatorLabel(int elevatorId) {
        // Метка лифта
        JLabel elevatorLabel = new JLabel();

        // Установка цвета лифта
        elevatorLabel.setBackground(Color.BLUE);
        elevatorLabel.setOpaque(true);

        // Размер лифта
        elevatorLabel.setSize(40, 60);

        // Начальное положение лифта на панели
        elevatorLabel.setLocation(50 + elevatorId * 100, 450);

        // Добавление метки на панель
        add(elevatorLabel);

        return elevatorLabel;
    }


    // Отрисовка компонентов панели
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
