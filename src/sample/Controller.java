package sample;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.util.*;
import java.net.URL;
import javax.swing.SwingUtilities;
import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import org.fxmisc.richtext.CodeArea;


import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import javafx.stage.Popup;





public class Controller implements Initializable {
    private Popup autocompleteBox = new Popup();
    private ExecutorService executor;
    private TrieNode<String> completionDictionary;

    private static final String[] processTypes = new String[] {
            "automata", "petrinet", "operation", "equation",

    };

    private static final String[] functions = new String[] {
            "abs", "simp", "safe", "nfa2dfa"
    };

    private static final String[] terminals = new String[] {
            "STOP", "ERROR"
    };

    private static final String[] keywords = new String[] {
            "const", "range", "set", "if", "then", "else", "when", "forall"
    };

    private static final String PROCESSTYPES_PATTERN = "\\b(" + String.join("|", processTypes) + ")\\b";
    private static final String FUNCTIONS_PATTERN = "\\b(" + String.join("|", functions) + ")\\b";
    private static final String TERMINALS_PATTERN = "\\b(" + String.join("|", terminals) + ")\\b";
    private static final String KEYWORDS_PATTERN = "\\b(" + String.join("|", keywords) + ")\\b";

    private static final String SYMBOLS = "\\.\\.|\\.|,|:|\\[|\\]|\\(|\\)|->|~>|\\\\|@|\\$|\\?";
    private static final String OPERATORS = "\\|\\||\\||&&|&|\\^|==|=|!=|<<|<=|<|>>|>=|>|\\+|-|\\*|\\/|%|!|\\?";
    private static final String OPERATIONS = "~|#";
    private static final String ACTION_LABEL_PATTERN = "[a-z][A-Za-z0-9_]*";
    private static final String IDENT_PATTERN = "[A-Z][A-Za-z0-9_\\\\*]*";
    private static final String INT_PATTERN = "[0-9][0-9]*";

    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String COMMENT_PATTERN = "\\/\\/[^\n]*";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<COMMENT>" + COMMENT_PATTERN + ")"+
            "|(?<PROCESSTYPE>" + PROCESSTYPES_PATTERN + ")" +
            "|(?<FUNCTION>" + FUNCTIONS_PATTERN + ")" +
            "|(?<TERMINAL>" + TERMINALS_PATTERN + ")" +
            "|(?<KEYWORD>" + KEYWORDS_PATTERN + ")" +
            "|(?<SYMBOL>" + SYMBOLS + ")" +
            "|(?<OPERATOR>" + OPERATORS + ")" +
            "|(?<OPERATION>" + OPERATIONS + ")" +
            "|(?<ACTIONLABEL>" + ACTION_LABEL_PATTERN + ")" +
            "|(?<IDENTIFER>" + IDENT_PATTERN + ")" +
            "|(?<INT>" + INT_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
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
        createAndSetSwingDrawingPanel(modelDisplay);
        completionDictionary = new TrieNode<>(  new ArrayList<String>(Arrays.asList(processTypes)) );
        completionDictionary.add(new ArrayList<String>(Arrays.asList(functions)));
        completionDictionary.add(new ArrayList<String>(Arrays.asList(keywords)));


        userCodeInput.getStylesheets().add(Main.class.getResource("automata-keywords.css").toExternalForm());

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
                    autocompleteBox.hide();

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
                        autocompleteBox.hide();

                        userCodeInput.setStyleSpans(0, computeHighlighting(userCodeInput.getText())); // Need to reupdate the styles when an insert has happened.
                    }
                }

            }
        });

        autocompleteBox.getContent().add(popupSelection);

        executor = Executors.newSingleThreadExecutor();

        userCodeInput.setParagraphGraphicFactory(LineNumberFactory.get(userCodeInput)); // Add line numbers

        userCodeInput.richChanges() // Set up syntax highlighting in another thread as regex finding can take a while.
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .successionEnds(Duration.ofMillis(20))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(userCodeInput.richChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);

        userCodeInput.richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved())).subscribe(( change) -> { // Hook for detecting user input, used for autocompletion as that happens quickly.
            if(change.getInserted().getStyleOfChar(0).isEmpty()) { // If this isnt a style event rather than the user typing.
                if (change.getRemoved().getText().length() == 0) {  // If this isnt a backspace character

                    String currentUserCode = userCodeInput.getText();

                    if (userCodeInput.getCaretPosition() < currentUserCode.length()) {

                        char currentCharacter = userCodeInput.getText().charAt(userCodeInput.getCaretPosition());
                        switch (currentCharacter) {
                            case '\n':
                            case '\t':
                            case ' ': { // If the user has broken off a word, dont continue autocompleting it.
                                autocompleteBox.hide();

                                popupSelection.getItems().clear();
                            }
                            break;

                            default: {
                                popupSelection.getItems().clear();
                                String currentWord = getWordAtIndex(userCodeInput.getCaretPosition());
                                if (currentWord.length() > 0) {
                                    ArrayList<String> list = completionDictionary.getId(currentWord);

                                    if (list.size() != 0) {
                                        popupSelection.getItems().addAll(list);
                                        popupSelection.getSelectionModel().select(0);

                                        autocompleteBox.show(userCodeInput, userCodeInput.getCaretBounds().get().getMinX(), userCodeInput.getCaretBounds().get().getMaxY());

                                    } else { // If we dont have any autocomplete suggestions dont show the box
                                        autocompleteBox.hide();

                                    }
                                } else {
                                    autocompleteBox.hide();

                                }

                            }
                            break;
                        }
                    }

                } else { // Handles if there is a backspace
                    popupSelection.getItems().clear();
                    autocompleteBox.hide();

                }

            }


        });

    }

    public void createAndSetSwingDrawingPanel(final SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {

        });
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = userCodeInput.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        userCodeInput.setStyleSpans(0, highlighting); // Fires a style event
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();


        while(matcher.find()) {
            String styleClass;

            if (matcher.group("PROCESSTYPE") != null) {
                styleClass = "process";
            } else if (matcher.group("FUNCTION") != null) {
                styleClass = "function";
            } else if (matcher.group("TERMINAL") != null) {
                if (matcher.group("TERMINAL").equals("STOP"))
                    styleClass = "terminalStop";
                else
                    styleClass = "terminalError";
            } else if(matcher.group("KEYWORD") != null) {
                    styleClass = "keyword";
            }else if(matcher.group("SYMBOL") != null) {
                styleClass = "symbol";
            } else if(matcher.group("OPERATOR") != null) {
                styleClass = "operator";
            } else if(matcher.group("OPERATION") != null) {
                styleClass = "operation";
            } else if(matcher.group("ACTIONLABEL") != null) {
                styleClass = "actionLabel";
            } else if(matcher.group("IDENTIFER") != null) {
                styleClass = "identifier";
            } else if(matcher.group("INT") != null) {
                styleClass = "number";
            } else if(matcher.group("PAREN") != null) {
                styleClass = "paren";

            } else if(matcher.group("BRACE") != null) {
                styleClass = "brace";

            }else if(matcher.group("BRACKET") != null) {
                styleClass = "bracket";

            } else if(matcher.group("COMMENT") != null) {
                styleClass = "comment";
            } else {
                styleClass = null;
            }


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
