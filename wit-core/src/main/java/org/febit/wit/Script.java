package org.febit.wit;

import org.febit.wit.exception.ParseException;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.io.DiscardOut;
import org.febit.wit.io.OutputStreamOut;
import org.febit.wit.io.WriterOut;
import org.febit.wit.runtime.BreakpointHandler;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Source;
import org.jspecify.annotations.Nullable;

import java.io.OutputStream;
import java.io.Writer;

public interface Script {

    Engine engine();

    String path();

    Source source();

    void reset();

    void reload();

    /**
     * Eval script.
     *
     * @param vars     vars
     * @param out      out
     * @param breakpointHandler breakpoint handler, may be null
     * @return Context
     * @throws ScriptEvaluateException when script runtime exception
     */
    Context eval(Vars vars, Out out, @Nullable BreakpointHandler breakpointHandler);

    Context merge(InternalContext target, Vars vars);

    default Evaluator evaluator() {
        return Evaluator.of(this);
    }

    /**
     * Eval script.
     *
     * @return Context
     * @throws ScriptEvaluateException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    default Context eval() {
        return eval(Vars.empty(), new DiscardOut(), null);
    }

    /**
     * Eval script.
     *
     * @param vars   vars
     * @param output out
     * @return Context
     * @throws ScriptEvaluateException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    default Context eval(Vars vars, OutputStream output) {
        var out = new OutputStreamOut(output, engine().charset(), engine().codecFactory());
        return eval(vars, out, null);
    }

    /**
     * Eval script.
     *
     * @param vars   vars
     * @param writer writer
     * @return Context
     * @throws ScriptEvaluateException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    default Context eval(Vars vars, Writer writer) {
        var out = new WriterOut(writer, engine().charset(), engine().codecFactory());
        return eval(vars, out, null);
    }

    /**
     * Eval script.
     *
     * @param vars vars
     * @param out  out
     * @return Context
     * @throws ScriptEvaluateException when script runtime exception
     * @throws ParseException         when unable to parse
     */
    default Context eval(Vars vars, Out out) {
        return eval(vars, out, null);
    }

}
