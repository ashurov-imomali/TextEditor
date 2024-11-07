import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class TextEditor extends JFrame {

    private JTextPane textArea;
    Integer[] sizess = {8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 36, 48, 72};
    private boolean isOpen = false;
    JComboBox<Integer> sizeLists = new JComboBox<>(sizess);
    private String fileName;
    JComboBox<String> fontLists = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
    private JLabel statusLabel;
    JComboBox<String> styleLists = new JComboBox<>(new String[]{"Plain", "Bold", "Italic", "Bold Italic"});
    private Font tFont;
    public TextEditor() {
        Map<Integer, Integer> mpS = new java.util.HashMap<>(Map.of(8, 0, 10, 1, 12, 2, 14, 3, 16, 4, 18, 5, 20, 6, 22, 7, 24, 8, 26, 9));
        Map<String, Integer> mpF = new java.util.HashMap<>(Map.of());
        mpS.put(28,11);
        mpS.put(30,12);
        mpS.put(36,13);
        mpS.put(48,14);
        mpS.put(72,15);
        for (int i = 0; i < fontLists.getMaximumRowCount(); i++) {
            System.out.println(fontLists.getItemAt(i));
            mpF.put(fontLists.getItemAt(i), i);
        }
        setTitle("Text Editor");
        tFont = new Font("Arial", Font.PLAIN, 20);
        textArea = new JTextPane();
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.setBackground(Color.LIGHT_GRAY); // Установка фона для верхней панели
        textArea.addCaretListener(e -> {
            int start = textArea.getSelectionStart();
            int end = textArea.getSelectionEnd();
            if (start != end){
                StyledDocument doc = textArea.getStyledDocument();
                Element elem = doc.getCharacterElement(start);
                AttributeSet attributes = elem.getAttributes();
                int fontSize = StyleConstants.getFontSize(attributes);
                String fontFamily = StyleConstants.getFontFamily(attributes);
                System.out.println("Font Size: " + fontSize);
                System.out.println("Font Size <int>: " + mpS.get(fontSize));
                sizeLists.setSelectedIndex(mpS.get(fontSize));
                System.out.println("Selected Font: " + fontFamily);
                System.out.println("Selected Font <int>: " + mpF.get(fontFamily));
                fontLists.setSelectedIndex(mpF.get(fontFamily));
            }
        });
        fontLists.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedFont = (String) fontLists.getSelectedItem();
                int start = textArea.getSelectionStart();
                int end = textArea.getSelectionEnd();

                if (start != end) {
                    StyledDocument doc = textArea.getStyledDocument();
                    Style style = textArea.addStyle("FontStyle", null);
                    StyleConstants.setFontFamily(style, selectedFont);

                    doc.setCharacterAttributes(start, end - start, style, false);
                    tFont = new Font(selectedFont, tFont.getStyle(), tFont.getSize());
                }
            }
        });
        topPanel.add(fontLists);

//        // Выпадающий список для выбора размера
//        Integer[] sizes = {8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 36, 48, 72};
//        JComboBox<Integer> sizeList = new JComboBox<>(sizes);
        sizeLists.setSelectedIndex(6);
        sizeLists.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedSize = (Integer) sizeLists.getSelectedItem();
                int start = textArea.getSelectionStart();
                int end = textArea.getSelectionEnd();

                if (start != end) { // Если есть выделенный текст
                    StyledDocument doc = textArea.getStyledDocument();
                    Style style = textArea.addStyle("SizeStyle", null);
                    StyleConstants.setFontSize(style, selectedSize);

                    // Применяем стиль к выделенному тексту
                    doc.setCharacterAttributes(start, end - start, style, false);
                    tFont = new Font(tFont.getFamily(), tFont.getStyle(), selectedSize);
                }
            }
        });

        topPanel.add(sizeLists);

        // Выпадающий список для выбора стиля
        styleLists.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedStyle = (String) styleLists.getSelectedItem();
                int start = textArea.getSelectionStart();
                int end = textArea.getSelectionEnd();

                if (start != end) { // Если есть выделенный текст
                    StyledDocument doc = textArea.getStyledDocument();
                    Style style = textArea.addStyle("TextStyle", null);
                    int s = Font.PLAIN;
                    // Устанавливаем стиль текста в зависимости от выбора
                    switch (selectedStyle) {
                        case "Bold":
                            s = Font.BOLD;
                            StyleConstants.setBold(style, true);
                            break;
                        case "Italic":
                            s = Font.ITALIC;
                            StyleConstants.setItalic(style, true);
                            break;
                        case "Bold Italic":
                            s = Font.BOLD | Font.ITALIC;
                            StyleConstants.setBold(style, true);
                            StyleConstants.setItalic(style, true);
                            break;
                        case "Plain":
                            s = Font.PLAIN;
                        default:
                            StyleConstants.setBold(style, false);
                            StyleConstants.setItalic(style, false);
                            break;
                    }

                    // Применяем стиль к выделенному тексту
                    doc.setCharacterAttributes(start, end - start, style, false);
                    tFont = new Font(tFont.getFamily(), s, tFont.getSize());
                }
            }
        });

        topPanel.add(styleLists);

        // Кнопка для выбора цвета текста
        JButton textColorButton = new JButton("Цвет текста");
        textColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(textArea, "Выберите цвет текста", textArea.getForeground());
            if (newColor != null) {
                int start = textArea.getSelectionStart();
                int end = textArea.getSelectionEnd();

                if (start != end) { // Если есть выделенный текст
                    StyledDocument doc = textArea.getStyledDocument();
                    Style style = textArea.addStyle("ColorStyle", null);
                    StyleConstants.setForeground(style, newColor);

                    // Применяем стиль к выделенному тексту
                    doc.setCharacterAttributes(start, end - start, style, false);
                }
            }
        });


        topPanel.add(textColorButton);

        JButton back = new JButton("Цвет фона");
        back.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(textArea, "Выберите цвет фона", textArea.getBackground());
            if (newColor != null) {
                int start = textArea.getSelectionStart();
                int end = textArea.getSelectionEnd();

                if (start != end) { // Если есть выделенный текст
                    StyledDocument doc = textArea.getStyledDocument();
                    Style style = textArea.addStyle("BackgroundStyle", null);
                    StyleConstants.setBackground(style, newColor);

                    // Применяем стиль к выделенному тексту
                    doc.setCharacterAttributes(start, end - start, style, false);
                }
            }
        });

        topPanel.add(back);
        add(topPanel, BorderLayout.NORTH); // Добавление верхней панели в окно



        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane);
        setSize(800, 600);
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
                showFontDialog();
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
    private void showFontDialog() {
        JDialog dialog = new JDialog((Frame) null, "Настройка шрифта", true);
        dialog.setLayout(new GridLayout(0, 1));

        // Выпадающий список для выбора шрифта
        JComboBox<String> fontList = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontList.setSelectedIndex(fontLists.getSelectedIndex());
        dialog.add(fontList);

        // Выпадающий список для выбора размера
        Integer[] sizes = {8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 36, 48, 72};
        JComboBox<Integer> sizeList = new JComboBox<>(sizes);
        sizeList.setSelectedIndex(sizeLists.getSelectedIndex()); // Установим по умолчанию 20
        dialog.add(sizeList);

        // Выпадающий список для выбора стиля
        JComboBox<String> styleList = new JComboBox<>(new String[]{"Plain", "Bold", "Italic", "Bold Italic"});
        styleList.setSelectedIndex(styleLists.getSelectedIndex());
        dialog.add(styleList);

        // Кнопка для выбора цвета текста
        JButton textColorButton = new JButton("Цвет текста");
        dialog.add(textColorButton);

        // Кнопка для выбора цвета фона
        JButton backButton = new JButton("Цвет фона");
        dialog.add(backButton);

        // Кнопки "OK" и "Отмена"
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Отмена");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel);

        // Переменные для хранения выбранных параметров
        String selectedFont = (String) fontList.getSelectedItem();
        int selectedSize = (Integer) sizeList.getSelectedItem();
        String selectedStyle = (String) styleList.getSelectedItem();
        final Color[] textColor = {textArea.getForeground()};
        final Color[] backgroundColor = {textArea.getBackground()};

        // Обработка выбора цвета текста
        textColorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(dialog, "Выберите цвет текста", textColor[0]);
            if (newColor != null) {
                textColor[0] = newColor; // Сохраняем выбранный цвет текста
            }
        });

        // Обработка выбора цвета фона
        backButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(dialog, "Выберите цвет фона", backgroundColor[0]);
            if (newColor != null) {
                backgroundColor[0] = newColor; // Сохраняем выбранный цвет фона
            }
        });

        // Обработка нажатия кнопки "OK"
        okButton.addActionListener(e -> {
            // Применяем выбранные параметры
            int start = textArea.getSelectionStart();
            int end = textArea.getSelectionEnd();
            if (start != end) {
                StyledDocument doc = textArea.getStyledDocument();
                Style style = textArea.addStyle("TextStyle", null);

                // Установка шрифта
                StyleConstants.setFontFamily(style, selectedFont);
                fontLists.setSelectedIndex(fontLists.getSelectedIndex());
                // Установка размера
                StyleConstants.setFontSize(style, (Integer) sizeList.getSelectedItem());
                System.out.println(selectedSize);
                sizeLists.setSelectedIndex(sizeLists.getSelectedIndex());
                // Установка стиля
                switch (selectedStyle) {
                    case "Bold":
                        StyleConstants.setBold(style, true);
                        StyleConstants.setItalic(style, false);
                        styleLists.setSelectedIndex(styleLists.getSelectedIndex());
                        break;
                    case "Italic":
                        StyleConstants.setItalic(style, true);
                        StyleConstants.setBold(style, false);
                        styleLists.setSelectedIndex(styleLists.getSelectedIndex());
                        break;
                    case "Bold Italic":
                        StyleConstants.setBold(style, true);
                        StyleConstants.setItalic(style, true);

                        styleLists.setSelectedIndex(styleLists.getSelectedIndex());
                        break;
                    case "Plain":
                    default:
                        StyleConstants.setBold(style, false);
                        StyleConstants.setItalic(style, false);
                        styleLists.setSelectedIndex(0);
                        break;
                }
                // Установка цвета текста и фона
                StyleConstants.setForeground(style, textColor[0]);
                StyleConstants.setBackground(style, backgroundColor[0]);
                sizeLists.setSelectedIndex(sizeList.getSelectedIndex());
                fontLists.setSelectedIndex(fontList.getSelectedIndex());
                styleLists.setSelectedIndex(styleList.getSelectedIndex());
                // Применяем стиль к выделенному тексту
                doc.setCharacterAttributes(start, end - start, style, false);
            }

            dialog.dispose(); // Закрываем диалог
        });

        // Обработка нажатия кнопки "Отмена"
        cancelButton.addActionListener(e -> {
            dialog.dispose(); // Просто закрываем диалог
        });

        dialog.setSize(300, 300);
        dialog.setVisible(true);
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
