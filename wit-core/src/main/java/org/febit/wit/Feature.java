package org.febit.wit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Feature {

    LOOSE_VAR(false,
            "In loose var mode, accessing undefined variables returns null instead of throwing exceptions."
    ),
    LOOSE_SEMICOLON(true,
            "In loose semicolon mode, semicolons at the end of statements are optional."
    ),
    TRIM_CODE_BLOCK_BLANK_LINE(true,
            "Trims blank lines around code blocks for cleaner output."
    ),
    SHARE_ROOT_PARAMS(true,
            "When enabled, all scripts share the same root parameters."
    ),
    IGNORE_ACCESSOR_NULL_POINTER(true,
            "When enabled, accessor null pointer exceptions are ignored and return null instead."
    )
    ;

    /**
     * If feature is enabled by default.
     */
    @Getter
    private final boolean enabledByDefault;
    /**
     * Feature description.
     */
    @Getter
    private final String description;

    private final int mask;

    public static int collectFeatureDefaults() {
        int flags = 0;
        for (var f : values()) {
            if (f.isEnabledByDefault()) {
                flags = f.enable(flags);
            }
        }
        return flags;
    }

    Feature(boolean enabledByDefault, String description) {
        this.enabledByDefault = enabledByDefault;
        this.description = description;
        this.mask = 1 << this.ordinal();
    }

    public int enable(int flags) {
        return flags | mask;
    }

    public int disable(int flags) {
        return flags & ~mask;
    }

    public boolean isEnabled(int flags) {
        return (flags & mask) != 0;
    }
}
