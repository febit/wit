package webit.script.core.ast;


/**
 *
 * @author Zqq
 */
public abstract class AbstractStatment implements Statment{

    protected final int line;
    protected final int column;

    public AbstractStatment(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

}
