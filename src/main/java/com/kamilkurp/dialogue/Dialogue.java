package com.kamilkurp.dialogue;

public class Dialogue {
    private String text;
    private Action action;
    private int actionArgument;

    public Dialogue(String text, Action action, int actionArgument) {
        this.text = text;
        this.action = action;
        this.actionArgument = actionArgument;
    }

    enum Action {
        GOTO, TRADE, CHOICE
    }

    public String getText() {
        return text;
    }

    public Action getAction() {
        return action;
    }

    public int getActionArgument() {
        return actionArgument;
    }
}
