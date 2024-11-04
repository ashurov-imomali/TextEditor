import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TextEditor extends JFrame {

    private JTextArea textArea;
    private boolean isOpen = false;
    private String fileName;
    private JLabel statusLabel;
    public TextEditor() {
        setTitle("Text Editor");
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane);
        setSize(1600, 1200);
        statusLabel = new JLabel("Абзацы: 0 | Предложения: 0 | Слова: 0 | Символы: 0 | Символы без пробелов: 0 | " +
                "Спец. символы: 0 | Лат. буквы: 0 | Рус. буквы: 0 | Цифры: 0 | Знаки препинания: 0");
        add(statusLabel, BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(GetMenuBars());
        setVisible(true);
        textArea.setFont(new Font("Serif", Font.PLAIN, 20));
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateStatus();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateStatus();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateStatus();
            }
        });
    }

    private void updateStatus() {
        String text = textArea.getText();
        int paragraphs = text.split("\\n").length; // Подсчет абзацев
        int sentences = text.split("[.!?]+").length; // Подсчет предложений
        int words = text.split("\\s+").length; // Подсчет слов
        int characters = text.length(); // Подсчет символов
        int charactersWithoutSpaces = text.replace(" ", "").length(); // Символы без пробелов
        int specialCharacters = text.replaceAll("[\\w\\s]", "").length(); // Специальные символы
        int latinLetters = countLatinLetters(text); // Латинские буквы
        int russianLetters = countRussianLetters(text); // Русские буквы
        int digits = text.replaceAll("\\D", "").length(); // Цифры
        int punctuationMarks = countPunctuationMarks(text); // Знаки препинания

        // Обновление строки состояния
        statusLabel.setText(String.format("Абзацы: %d | Предложения: %d | Слова: %d | Символы: %d | " +
                        "Символы без пробелов: %d | Спец. символы: %d | Лат. буквы: %d | Рус. буквы: %d | " +
                        "Цифры: %d | Знаки препинания: %d",
                paragraphs, sentences, words, characters, charactersWithoutSpaces,
                specialCharacters, latinLetters, russianLetters, digits, punctuationMarks));
    }
    private int countLatinLetters(String text) {
        return (int) text.chars().filter(ch -> Character.UnicodeScript.of(ch) == Character.UnicodeScript.LATIN).count();
    }

    private int countRussianLetters(String text) {
        return (int) text.chars().filter(ch -> Character.UnicodeScript.of(ch) == Character.UnicodeScript.CYRILLIC).count();
    }

    private int countPunctuationMarks(String text) {
        return (int) text.chars().filter(ch -> String.valueOf((char) ch).matches("[.,!?;:()\"'\\[\\]{}]")).count();
    }
    public JMenuBar GetMenuBars(){
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenu editMenu = new JMenu("Изменить");
        JMenuItem openItem = new JMenuItem("Открыть");
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Текстовые файлы (*.txt)", "txt");
                fileChooser.setFileFilter(filter);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnVal = fileChooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    readFile(file);
                }
            }
        });
        JMenuItem saveItem = new JMenuItem("Сохранить");
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        JMenuItem saveAs = new JMenuItem("Сохранить как");
        saveAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               saveAsFile(textArea.getText());
            }
        });
        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAs);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        JMenuItem font = new JMenuItem("Шрифт");
        font.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Font f = showFontDialog(new Font("Serif", Font.PLAIN, 16));
            }
        });
        JMenuItem up = new JMenuItem("Увеличить");
        up.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Font font1 = textArea.getFont();

                textArea.setFont(new Font(font1.getFontName(), Font.PLAIN, font1.getSize() + 1));

            }
        });
        JMenuItem down = new JMenuItem("Уменьшить");
        down.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Font font1 = textArea.getFont();

                textArea.setFont(new Font(font1.getFontName(), Font.PLAIN, font1.getSize() - 1));

            }
        });
        editMenu.add(font);
        editMenu.add(up);
        editMenu.add(down);
        menuBar.add(editMenu);
        return menuBar;
    }
    private Font showFontDialog(Font initialFont) {
        // Создаем диалог выбора шрифта
        JDialog fontDialog = new JDialog(this, "Выбор шрифта", true);
        fontDialog.setLayout(new GridLayout(5, 2)); // Обновлено на 6 строк

        // Элементы для выбора шрифта
        JComboBox<String> fontList = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontList.setSelectedItem(initialFont.getFamily());

        Integer[] sizes = {8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 36, 48, 72};
        JComboBox<Integer> sizeList = new JComboBox<>(sizes);
        sizeList.setSelectedItem(initialFont.getSize());

        JComboBox<String> styleList = new JComboBox<>(new String[]{"Plain", "Bold", "Italic", "Bold Italic"});
        styleList.setSelectedItem(initialFont.isBold() ? (initialFont.isItalic() ? "Bold Italic" : "Bold") : (initialFont.isItalic() ? "Italic" : "Plain"));

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Отмена");

        // Кнопка для выбора цвета текста
        JButton textColorButton = new JButton("Выбрать цвет текста");
        textColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(fontDialog, "Выберите цвет текста", textArea.getForeground());
            if (newColor != null) {
                textArea.setForeground(newColor);
            }
        });

//         Кнопка для выбора цвета фона
        JButton backgroundColorButton = new JButton("Выбрать цвет фона");
        backgroundColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(fontDialog, "Выберите цвет фона", textArea.getBackground());
            if (newColor != null) {
                textArea.setBackground(newColor);
            }
        });

        // Обработчик кнопки OK
        okButton.addActionListener(e -> {
            int style = switch (styleList.getSelectedItem().toString()) {
                case "Bold" -> Font.BOLD;
                case "Italic" -> Font.ITALIC;
                case "Bold Italic" -> Font.BOLD | Font.ITALIC;
                default -> Font.PLAIN;
            };
            Font selectedFont = new Font(fontList.getSelectedItem().toString(), style, (Integer) sizeList.getSelectedItem());
            textArea.setFont(selectedFont);
            fontDialog.dispose(); // Закрываем диалог
        });

        // Обработчик кнопки Cancel
        cancelButton.addActionListener(e -> fontDialog.dispose());

        // Добавление элементов в диалог
        fontDialog.add(new JLabel("Шрифт:"));
        fontDialog.add(fontList);
        fontDialog.add(new JLabel("Размер:"));
        fontDialog.add(sizeList);
        fontDialog.add(new JLabel("Стиль:"));
        fontDialog.add(styleList);
        fontDialog.add(textColorButton);
        fontDialog.add(backgroundColorButton);
        fontDialog.add(okButton);
        fontDialog.add(cancelButton);

        fontDialog.pack();
        fontDialog.setLocationRelativeTo(this);
        fontDialog.setVisible(true);

        return initialFont; // Можно вернуть null или initialFont, если диалог закрыт без выбора
    }

    public static void saveAsFile(String content) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить как");

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(content);
                JOptionPane.showMessageDialog(null, "Файл сохранен: " + fileToSave.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ошибка при сохранении файла.");
            }
        }
    }


    private void saveFile() {
        if (isOpen) {
            try (FileWriter writer = new FileWriter(fileName, false)) {
                writer.write(textArea.getText());
                System.out.println("Содержимое файла перезаписано.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Ошибка при перезаписи файла.");
            }
            return;
        }
        LocalDateTime t = LocalDateTime.now();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        String format = t.format(f);
        String filePath = "C:/Users/26PC/Desktop/Dreamer/file_" + format + ".txt"; // Замените на нужный вам путь
        File file = new File(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(textArea.getText());
            JOptionPane.showMessageDialog(this, "Файл успешно сохранён в " + filePath);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Ошибка при сохранении файла: " + e.getMessage());
        }
        isOpen = true;
        fileName = filePath;
    }



    private void readFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }

            textArea.setText(content.toString());
        } catch (IOException e) {
            e.printStackTrace();

        }
        isOpen = true;
        fileName = file.getPath();
    }



    private void save(JMenuItem i) {
        i.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

            }
        });
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TextEditor::new);
    }
}
