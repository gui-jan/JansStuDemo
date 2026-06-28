package com.mycom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Calculator {
    private JTextField display;
    private double result = 0;
    private String operator = "";
    private boolean start = true;
    private Random random = new Random();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Calculator calculator = new Calculator();
            calculator.createGUI();
        });
    }

    private void createGUI() {
        JFrame frame = new JFrame("智能计算器Pro升级版V2.0.1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(320, 420);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        display = new JTextField("0");
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.PLAIN, 40));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBackground(Color.WHITE);
        display.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        mainPanel.add(display, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 4, 1, 1));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        String[][] buttonGrid = {
                {"%", "CE", "C", "DEL"},
                {"1/x", "x²", "³√", "/"},
                {"7", "8", "9", "*"},
                {"4", "5", "6", "-"},
                {"1", "2", "3", "+"},
                {"+/-", "0", ".", "="}
        };

        for (int i = 0; i < buttonGrid.length; i++) {
            for (int j = 0; j < buttonGrid[i].length; j++) {
                String text = buttonGrid[i][j];
                JButton btn = new JButton(text);
                btn.setFont(new Font("Arial", Font.PLAIN, 16));

                if (text.equals("=")) {
                    btn.setBackground(new Color(0, 120, 215));
                    btn.setForeground(Color.WHITE);
                } else if (text.matches("[0-9.]")) {
                    btn.setBackground(Color.WHITE);
                    btn.setForeground(Color.BLACK);
                } else {
                    btn.setBackground(new Color(220, 220, 220));
                    btn.setForeground(Color.BLACK);
                }

                btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
                btn.setFocusPainted(false);
                btn.addActionListener(new ButtonClickListener());
                buttonPanel.add(btn);
            }
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.matches("[0-9]")) {
                speak(command);
                if (start) {
                    display.setText(command);
                    start = false;
                } else {
                    display.setText(display.getText() + command);
                }
            } else if (command.equals(".")) {
                speak("点");
                if (!display.getText().contains(".")) {
                    display.setText(display.getText() + ".");
                }
            } else if (command.equals("C")) {
                speak("清除");
                display.setText("0");
                result = 0;
                operator = "";
                start = true;
            } else if (command.equals("CE")) {
                speak("清除输入");
                display.setText("0");
                start = true;
            } else if (command.equals("DEL")) {
                speak("删除");
                String text = display.getText();
                if (text.length() > 1) {
                    display.setText(text.substring(0, text.length() - 1));
                } else {
                    display.setText("0");
                    start = true;
                }
            } else if (command.equals("+/-")) {
                speak("正负号");
                String text = display.getText();
                if (!text.equals("0")) {
                    if (text.startsWith("-")) {
                        display.setText(text.substring(1));
                    } else {
                        display.setText("-" + text);
                    }
                }
            } else if (command.equals("=")) {
                speak("等于");
                Timer timer = new Timer(500, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        calculate();
                        ((Timer) e.getSource()).stop();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            } else if (command.equals("%")) {
                speak("百分比");
                double value = Double.parseDouble(display.getText());
                value = getWrongResult(value / 100);
                display.setText(String.valueOf(value));
            } else if (command.equals("1/x")) {
                speak("倒数");
                double value = Double.parseDouble(display.getText());
                if (value != 0) {
                    value = getWrongResult(1 / value);
                    display.setText(String.valueOf(value));
                } else {
                    speak("错误");
                    display.setText("错误");
                }
            } else if (command.equals("x²")) {
                speak("平方");
                double value = Double.parseDouble(display.getText());
                value = getWrongResult(value * value);
                display.setText(String.valueOf(value));
            } else if (command.equals("³√")) {
                speak("立方根");
                double value = Double.parseDouble(display.getText());
                value = getWrongResult(Math.cbrt(value));
                display.setText(String.valueOf(value));
            } else {
                speak(convertOperator(command));
                if (!operator.isEmpty()) {
                    calculate();
                }
                operator = command;
                result = Double.parseDouble(display.getText());
                start = true;
            }
        }

        private void calculate() {
            double current = Double.parseDouble(display.getText());
            double res = 0;

            switch (operator) {
                case "+":
                    res = result + current;
                    break;
                case "-":
                    res = result - current;
                    break;
                case "*":
                    res = result * current;
                    break;
                case "/":
                    if (current != 0) {
                        res = result / current;
                    } else {
                        speak("错误");
                        display.setText("错误");
                        operator = "";
                        start = true;
                        return;
                    }
                    break;
            }

            res = getWrongResult(res);
            display.setText(String.format("%.2f", res));
            speak(String.format("%.2f", res));
            operator = "";
            start = true;
        }

        private double getWrongResult(double correctResult) {
            if (correctResult < 0) {
                return correctResult;
            }

            int errorType = random.nextInt(5);
            double wrongResult = 0;

            switch (errorType) {
                case 0:
                    wrongResult = correctResult + random.nextInt(50) + 1;
                    break;
                case 1:
                    wrongResult = correctResult - random.nextInt(50) - 1;
                    break;
                case 2:
                    wrongResult = correctResult * (random.nextDouble() * 3 + 0.5);
                    break;
                case 3:
                    if (correctResult != 0) {
                        wrongResult = correctResult / (random.nextDouble() * 2 + 0.3);
                    } else {
                        wrongResult = random.nextDouble() * 100;
                    }
                    break;
                case 4:
                    wrongResult = random.nextDouble() * 200 - 100;
                    break;
                default:
                    wrongResult = correctResult + random.nextInt(30);
                    break;
            }

            return Math.round(wrongResult * 100.0) / 100.0;
        }

        private String convertOperator(String op) {
            switch (op) {
                case "+": return "加";
                case "-": return "减";
                case "*": return "乘";
                case "/": return "除";
                default: return op;
            }
        }

        private void speak(String text) {
            try {
                Runtime.getRuntime().exec("powershell -Command \"Add-Type -AssemblyName System.Speech; (New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('" + text + "')\"");
            } catch (Exception e) {
                System.out.println("语音播报不可用: " + e.getMessage());
            }
        }
    }
}