package sample;

import com.sun.xml.internal.ws.util.StringUtils;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.fxmisc.richtext.CodeArea;


import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.demo.JavaKeywordsAsync;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import javafx.stage.Popup;



public class Controller implements Initializable{
    private TrieNode<String> completionDictionary;

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
        completionDictionary = new TrieNode<>(KEYWORDS, KEYWORDS.length);

        createAndSetSwingDrawingPanel(modelDisplay);
        userCodeInput.getStylesheets().add(JavaKeywordsAsync.class.getResource("java-keywords.css").toExternalForm());


        Popup popup = new Popup();

        ListView popupSelection = new ListView();
        popupSelection.setStyle(
                "-fx-background-color: #f7e1a0;" +
                "-fx-text-fill: black;" +
                "-fx-padding: 5;"
        );


        popupSelection.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                String selectedItem = (String)popupSelection.getSelectionModel().getSelectedItem();
                if(selectedItem != null) {

                    String code = userCodeInput.getText();
                    int wordPosition = userCodeInput.getCaretPosition()-1; // we know where the user word is, but we dont know the start or end

                    int start = 0;
                    for(start = wordPosition; start > 0 && !Character.isWhitespace(code.charAt(start-1)) && Character.isLetterOrDigit(userCodeInput.getText().charAt(start-1)); start--);


                    int end = 0;
                    for(end = wordPosition; end < code.length() && !Character.isWhitespace(code.charAt(end)) && Character.isLetterOrDigit(userCodeInput.getText().charAt(end)) ; end++);


                    userCodeInput.replaceText(start, end, selectedItem);

                    popupSelection.getItems().clear();
                    popup.hide();
                    userCodeInput.setStyleSpans(0, computeHighlighting(userCodeInput.getText())); // Need to reupdate the styles when an insert has happened.
                }
            }
        });

        popupSelection.setOnKeyReleased(new EventHandler<KeyEvent>(){

            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    String selectedItem = (String)popupSelection.getSelectionModel().getSelectedItem();
                    if(selectedItem != null) {


                        String code = userCodeInput.getText();
                        int wordPosition = userCodeInput.getCaretPosition()-1; // we know where the user word is, but we dont know the start or end

                        //Find the first whitespace or special character, so statements like for(int will give an autocomplete option
                        int start = 0;
                        for(start = wordPosition; start > 0 && !Character.isWhitespace(code.charAt(start-1)) && Character.isLetterOrDigit(userCodeInput.getText().charAt(start-1)); start--);

                        int end = 0;
                        for(end = wordPosition; end < code.length() && !Character.isWhitespace(code.charAt(end)) && Character.isLetterOrDigit(userCodeInput.getText().charAt(end)); end++);


                        userCodeInput.replaceText(start, end, selectedItem);

                        popupSelection.getItems().clear();
                        popup.hide();
                        userCodeInput.setStyleSpans(0, computeHighlighting(userCodeInput.getText())); // Need to reupdate the styles when an insert has happened.
                    }
                }

            }
        });



        popup.getContent().add(popupSelection);

        userCodeInput.setParagraphGraphicFactory(LineNumberFactory.get(userCodeInput)); // Add line numbers

        userCodeInput.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())).subscribe(( change) -> { // Hook for detecting user input
            if(change.getRemoved().getText().length() == 0 ) {  // If this isnt a backspace character

                String currentUserCode = userCodeInput.getText();

                if( userCodeInput.getCaretPosition() < currentUserCode.length() ) {

                    char currentCharacter = userCodeInput.getText().charAt(userCodeInput.getCaretPosition());
                    switch (currentCharacter) {
                        case '\n':
                        case '\t':
                        case ' ': { // If the user has broken off a word, dont continue autocompleting it.
                            popup.hide();
                            popupSelection.getItems().clear();
                        }
                        break;

                        default: {
                            popupSelection.getItems().clear();
                            String currentWord = getWordAtIndex(userCodeInput.getCaretPosition());
                            System.out.println(currentWord);
                            if(currentWord.length() > 0) {
                                ArrayList<String> list = completionDictionary.getId(currentWord);

                                if (list.size() != 0) {
                                    popupSelection.getItems().addAll(list);
                                    popupSelection.getSelectionModel().select(0);
                                    popup.show(userCodeInput, userCodeInput.getCaretBounds().get().getMinX(), userCodeInput.getCaretBounds().get().getMaxY());
                                } else {
                                    popup.hide();
                                }
                            } else {
                                    popup.hide();

                            }
                            // Apply the visual effects of syntax highlighting
                            userCodeInput.setStyleSpans(0, computeHighlighting(userCodeInput.getText()));
                        }
                        break;
                    }
                }

            } else { // Handles if there is a backspace
                popupSelection.getItems().clear();
                popup.hide();
            }


        });

    }



    public void createAndSetSwingDrawingPanel(final SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {

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
        for (index = text.length() - 1; index >= 0 && !Character.isWhitespace(text.charAt(index)) && Character.isLetterOrDigit(userCodeInput.getText().charAt(index)); index--);

        // get prefix and startIndex of word
        String prefix = text.substring(index + 1, text.length());

        // get first whitespace forward from caret
        for (index = pos; index < userCodeInput.getLength() && !Character.isWhitespace(userCodeInput.getText().charAt(index)) && Character.isLetterOrDigit(userCodeInput.getText().charAt(index)); index++);

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
