package org.febit.wit;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import org.febit.wit.exception.ParseException;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.io.Out;
import org.febit.wit.io.Source;
import org.febit.wit.io.out.DiscardOut;
import org.febit.wit.runtime.BreakpointHandler;
import org.febit.wit.runtime.InternalContext;
import org.jspecify.annotations.Nullable;

import java.io.OutputStream;
import java.io.Writer;

public interface Script {

    Wit engine();

    String path();

    Source source();

    void reset();

    void reload();

    /**
     * Eval script.
     *
     * @param inputs            input vars
     * @param out               out
     * @param breakpointHandler breakpoint handler, may be null
     * @return Context
     * @throws ScriptEvaluateException when script runtime exception
     */
    Context eval(Vars inputs, Out out, @Nullable BreakpointHandler breakpointHandler);

    Context merge(InternalContext target, Vars inputs);

    @CheckReturnValue
    default Evaluator evaluator() {
        return Evaluator.of(this);
    }

    /**
     * Eval script.
     *
     * @return Context
     * @throws ScriptEvaluateException when script runtime exception
     * @throws ParseException          when unable to parse
     */
    default Context eval() {
        return eval(Vars.empty(), DiscardOut.get(), null);
    }

    /**
     * Eval script.
     *
     * @param inputs input vars
     * @param output out
     * @return Context
     * @throws ScriptEvaluateException when script runtime exception
     * @throws ParseException          when unable to parse
     */
    default Context eval(Vars inputs, OutputStream output) {
        var out = engine().asOut(output);
        return eval(inputs, out, null);
    }

    /**
     * Eval script.
     *
     * @param inputs input vars
     * @param writer writer
     * @return Context
     * @throws ScriptEvaluateException when script runtime exception
     * @throws ParseException          when unable to parse
     */
    default Context eval(Vars inputs, Writer writer) {
        var out = engine().asOut(writer);
        return eval(inputs, out, null);
    }

    /**
     * Eval script.
     *
     * @param inputs input vars
     * @param out  out
     * @return Context
     * @throws ScriptEvaluateException when script runtime exception
     * @throws ParseException          when unable to parse
     */
    default Context eval(Vars inputs, Out out) {
        return eval(inputs, out, null);
    }

}
