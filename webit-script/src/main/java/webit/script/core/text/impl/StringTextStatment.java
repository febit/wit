package webit.script.core.text.impl;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Optimizable;

/**
 *
 * @author Zqq
 */
public final class StringTextStatment extends AbstractStatment implements Optimizable {

    private final String text;

    public StringTextStatment(String text, int line, int column) {
        super(line, column);
        this.text = text;
    }

    @Override
    public void execute(Context context) {
        context.out(text);
    }

    public StringTextStatment optimize() {
        if (text != null && text.length() > 0) {
            return this;
        }
        return null;
    }
}
