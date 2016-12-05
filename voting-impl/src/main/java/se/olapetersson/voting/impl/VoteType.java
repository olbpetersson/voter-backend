package se.olapetersson.voting.impl;

public enum VoteType {
    UP(1),
    DOWN(-1);
    private int value;

    VoteType(int value) {
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }
}
