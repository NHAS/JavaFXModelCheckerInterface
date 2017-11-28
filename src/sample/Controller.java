package sample;

import com.sun.xml.internal.ws.util.StringUtils;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.net.URL;
import javax.swing.SwingUtilities;
import javax.swing.*;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.ListView;
import org.fxmisc.richtext.CodeArea;


import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.demo.JavaKeywordsAsync;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import javafx.stage.Popup;



public class Controller implements Initializable{


    private static final String[] KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );


    @FXML private SwingNode modelDisplay;

    @FXML private CodeArea userCodeInput;


    @FXML
    private void handleQuit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleCreateNew(ActionEvent event) {

    }

    @FXML
    private void handleOpenRecentAction(ActionEvent event) {


    }

    @FXML
    private void handleOpen(ActionEvent event) {

    }

    @FXML
    private void handleFileClose(ActionEvent event) {

    }

    @FXML
    private void handleSave(ActionEvent event) {

    }

    @FXML
    private void handleAddSelectedModel(ActionEvent event) {

    }

    @FXML
    private void handleAddallModels(ActionEvent event) {

    }

    @FXML
    private void handleClearCanvas(ActionEvent event) {

    }

    @FXML
    private void handlExportImage(ActionEvent event) {

    }

    @FXML
    private void handOptionsRequest(ActionEvent event) {

    }

    @FXML
    private void handleCompileRequest(ActionEvent event) {

    }

    @FXML
    private void handleSaveAs(ActionEvent event) {


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userCodeInput.getStylesheets().add(JavaKeywordsAsync.class.getResource("java-keywords.css").toExternalForm());
        Popup popup = new Popup();

        ListView popupSelection = new ListView();
        popupSelection.setStyle( "-fx-background-color: #f7e1a0;" +
                "-fx-text-fill: black;" +
                "-fx-padding: 5;");

        popup.getContent().add(popupSelection);
        TrieNode<String> completionDictionary = new TrieNode<>();
        for(int i = 0; i < KEYWORDS.length; i++)
                completionDictionary.add(KEYWORDS[i], KEYWORDS[i]);

        int[] lastCaretPosition = {0}; // cant tell the different when in an array.

        createAndSetSwingDrawingPanel(modelDisplay);
        userCodeInput.setParagraphGraphicFactory(LineNumberFactory.get(userCodeInput));
        userCodeInput.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())).subscribe(( change) -> {

            if(lastCaretPosition[0] < userCodeInput.getCaretPosition() ) { // Handles if there is a backspace

                char currentCharacter = userCodeInput.getText().charAt(userCodeInput.getCaretPosition());
                switch (currentCharacter) {
                    case '\n':
                    case '\t':
                    case ' ': {
                        popup.hide();
                        popupSelection.getItems().clear();
                    }
                    break;

                    default: {
                        popupSelection.getItems().clear();
                        ArrayList<String> list = completionDictionary.getId(getWordAtIndex(userCodeInput.getCaretPosition()));

                        if(list.size() != 0) {
                            popupSelection.getItems().addAll(list);
                            popup.show(userCodeInput, userCodeInput.getCaretBounds().get().getMinX(), userCodeInput.getCaretBounds().get().getMaxY());
                        } else {
                            popup.hide();
                        }

                        userCodeInput.setStyleSpans(0, computeHighlighting(userCodeInput.getText()));
                    }
                    break;
                }
            } else {
                System.out.println(lastCaretPosition[0] + " " + userCodeInput.getCaretPosition());
                popupSelection.getItems().clear();
                popup.hide();

            }



            lastCaretPosition[0] = userCodeInput.getCaretPosition();

        });





    }




    public void createAndSetSwingDrawingPanel(final SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            JButton jButton = new JButton("Click me!");
            jButton.setBounds(0,0,120,50);

            JPanel panel = new JPanel();
            panel.setLayout(null);
            panel.add(jButton);

            panel.setSize(120, 60);

            swingNode.setContent(panel);

        });
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private String getWordAtIndex(int pos) {
        String text = userCodeInput.getText().substring(0, pos);

        // keeping track of index
        int index;

        // get first whitespace "behind caret"
        for (index = text.length() - 1; index >= 0 && !Character.isWhitespace(text.charAt(index)); index--);

        // get prefix and startIndex of word
        String prefix = text.substring(index + 1, text.length());

        // get first whitespace forward from caret
        for (index = pos; index < userCodeInput.getLength() && !Character.isWhitespace(userCodeInput.getText().charAt(index)); index++);

        String suffix = userCodeInput.getText().substring(pos, index);

        // replace regex wildcards (literal ".") with "\.". Looks weird but
        // correct...
        prefix = prefix.replaceAll("\\.", "\\.");
        suffix = suffix.replaceAll("\\.", "\\.");

        // combine both parts of words
        prefix = prefix + suffix;

        // return current word being typed
        return prefix;
    }


}
