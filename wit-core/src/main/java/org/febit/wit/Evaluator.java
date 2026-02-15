package org.febit.wit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;
import org.febit.wit.io.DiscardOut;
import org.febit.wit.io.OutputStreamOut;
import org.febit.wit.io.WriterOut;
import org.febit.wit.runtime.BreakpointHandler;
import org.jspecify.annotations.Nullable;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.function.Supplier;

import static org.febit.wit.util.Defaults.nvl;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
@RequiredArgsConstructor(staticName = "of")
public class Evaluator {

    @lombok.NonNull
    @SuppressWarnings("NullableProblems")
    private final Script script;

    @Nullable
    private Vars vars;

    @Nullable
    private BreakpointHandler breakpointHandler;

    @Nullable
    private Charset charset;

    @Nullable
    private Supplier<Out> out;

    private Supplier<Out> discard() {
        return DiscardOut::new;
    }

    private Supplier<Out> wrap(Writer writer) {
        var engine = script.engine();
        return () -> new WriterOut(writer,
                charset != null ? charset : engine.charset(),
                engine.codecFactory()
        );
    }

    private Supplier<Out> wrap(OutputStream output) {
        var engine = script.engine();
        return () -> new OutputStreamOut(output,
                charset != null ? charset : engine.charset(),
                engine.codecFactory()
        );
    }

    @Tolerate
    public Evaluator out(Writer writer) {
        return this.out(wrap(writer));
    }

    @Tolerate
    public Evaluator out(OutputStream output) {
        return this.out(wrap(output));
    }

    public Context eval() {
        return script().eval(
                nvl(vars, Vars::empty),
                nvl(out, this::discard).get(),
                breakpointHandler
        );
    }

}
