package com.kamilkurp.dialogue;

public class Dialogue {
    private String id;
    private String text;
    private Action action;
    private String actionArgument;

    public Dialogue(String id, String text, Action action, String actionArgument) {
        this.id = id;
        this.text = text;
        this.action = action;
        this.actionArgument = actionArgument;
    }

    enum Action {
        GOTO, TRADE, CHOICE, GOODBYE
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Action getAction() {
        return action;
    }

    public String getActionArgument() {
        return actionArgument;
    }
}
