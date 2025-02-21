package io.phial.phialc;

import java.nio.file.Path;

public class ParserException extends Exception {
    private Path path;
    private int lineNumber;
    private int columnNumber;

    public ParserException(String msg) {
        super(msg);
    }

    @Override
    public String getMessage() {
        return this.path + ":" + (this.lineNumber + 1) + ":" + (this.columnNumber + 1) + " " + super.getMessage();
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getColumnNumber() {
        return this.columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }
}
