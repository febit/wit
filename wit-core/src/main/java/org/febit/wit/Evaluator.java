package org.febit.wit;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;
import org.febit.wit.io.Out;
import org.febit.wit.io.out.DiscardOut;
import org.febit.wit.runtime.BreakpointHandler;
import org.jspecify.annotations.Nullable;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.function.Supplier;

import static org.febit.wit.util.Defaults.nvl;

@Getter
@Setter(onMethod_ = {@CheckReturnValue})
@Accessors(fluent = true, chain = true)
@RequiredArgsConstructor(
        staticName = "of",
        onConstructor_ = {@CheckReturnValue}
)
public class Evaluator {

    @lombok.NonNull
    @SuppressWarnings("NullableProblems")
    private final Script script;

    @Nullable
    private Vars inputs;

    @Nullable
    private Charset charset;

    @Nullable
    private Supplier<Out> out;

    @Nullable
    private BreakpointHandler breakpointHandler;

    @Tolerate
    @CheckReturnValue
    public Evaluator out(Writer writer) {
        return this.out(() -> script.engine().asOut(writer, charset));
    }

    @Tolerate
    @CheckReturnValue
    public Evaluator out(OutputStream output) {
        return this.out(() -> script.engine().asOut(output, charset));
    }

    public Context eval() {
        return script().eval(
                nvl(inputs, Vars::empty),
                nvl(out, (Supplier<Out>) DiscardOut::get).get(),
                breakpointHandler
        );
    }

}
