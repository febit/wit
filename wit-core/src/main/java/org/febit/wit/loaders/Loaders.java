package org.febit.wit.loaders;

import lombok.Singular;
import lombok.experimental.UtilityClass;
import org.febit.wit.loaders.impl.AdvancePathLoaderDecorator;
import org.febit.wit.loaders.impl.ClasspathLoader;
import org.febit.wit.loaders.impl.DebouncedLoaderDecorator;
import org.febit.wit.loaders.impl.DispatcherLoader;
import org.febit.wit.loaders.impl.FileSystemLoader;
import org.febit.wit.loaders.impl.NoopLoader;
import org.febit.wit.loaders.impl.SecurityLoaderDecorator;
import org.jspecify.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@UtilityClass
public class Loaders {

    public static NoopLoader noop() {
        return new NoopLoader();
    }

    public static DispatcherLoader.Builder dispatcher() {
        return DispatcherLoader.builder();
    }

    public static SecurityLoaderDecorator security(Loader delegate, List<String> allows) {
        return SecurityLoaderDecorator.of(delegate, allows);
    }

    public static DebouncedLoaderDecorator debounce(Loader delegate, long delayMillis) {
        return DebouncedLoaderDecorator.of(delegate, delayMillis);
    }

    public static DebouncedLoaderDecorator debounce(Loader delegate, Duration delay) {
        return DebouncedLoaderDecorator.of(delegate, (int) delay.toMillis());
    }

    public static AdvancePathLoaderDecorator.Builder advance(BasicPathLoader delegate) {
        return AdvancePathLoaderDecorator.builder()
                .delegate(delegate);
    }

    @lombok.Builder(
            builderClassName = "ClasspathBuilder",
            builderMethodName = "classpath"
    )
    private static Loader classpath0(
            @Nullable Charset charset,
            @Nullable Boolean codeFirst,
            @Nullable Boolean cacheEnabled,

            @Nullable String root,
            @Nullable String missingSuffix,
            @Singular List<String> deputySuffixes
    ) {
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        if (codeFirst == null) {
            codeFirst = Boolean.FALSE;
        }

        var delegate = ClasspathLoader.of(charset, codeFirst);
        var advance = advance(delegate)
                .root(root)
                .missingSuffix(missingSuffix)
                .deputySuffixes(deputySuffixes);
        if (cacheEnabled != null) {
            advance.cacheEnabled(cacheEnabled);
        }
        return advance.build();
    }

    @lombok.Builder(
            builderClassName = "FileSystemBuilder",
            builderMethodName = "fileSystem"
    )
    private static Loader fileSystem0(
            @Nullable Charset charset,
            @Nullable Boolean codeFirst,
            @Nullable Boolean cacheEnabled,
            @Nullable String root,
            @Nullable String missingSuffix,
            @Singular List<String> deputySuffixes
    ) {
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        if (codeFirst == null) {
            codeFirst = Boolean.TRUE;
        }
        var delegate = FileSystemLoader.of(charset, codeFirst);
        var advance = advance(delegate)
                .root(root)
                .missingSuffix(missingSuffix)
                .deputySuffixes(deputySuffixes);
        if (cacheEnabled != null) {
            advance.cacheEnabled(cacheEnabled);
        }
        return advance.build();
    }

}
