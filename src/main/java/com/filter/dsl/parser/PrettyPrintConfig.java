package com.filter.dsl.parser;

/**
 * Configuration options for DSL expression pretty printing.
 *
 * This class provides configurable formatting options including:
 * - Indent size (number of spaces per indentation level)
 * - Line width (maximum characters per line before wrapping)
 * - Compact vs expanded mode (single-line vs multi-line formatting)
 * - Indent style (spaces vs tabs)
 */
public class PrettyPrintConfig {

    private final int indentSize;
    private final int lineWidth;
    private final boolean compactMode;
    private final boolean useTabs;

    /**
     * Default configuration:
     * - 2 spaces per indent level
     * - 80 character line width
     * - Expanded mode (multi-line)
     * - Use spaces (not tabs)
     */
    public static final PrettyPrintConfig DEFAULT = new Builder().build();

    /**
     * Compact configuration for single-line output.
     */
    public static final PrettyPrintConfig COMPACT = new Builder()
        .compactMode(true)
        .build();

    /**
     * Wide format configuration for deeply nested expressions.
     */
    public static final PrettyPrintConfig WIDE = new Builder()
        .lineWidth(120)
        .indentSize(4)
        .build();

    private PrettyPrintConfig(Builder builder) {
        this.indentSize = builder.indentSize;
        this.lineWidth = builder.lineWidth;
        this.compactMode = builder.compactMode;
        this.useTabs = builder.useTabs;
    }

    public int getIndentSize() {
        return indentSize;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public boolean isCompactMode() {
        return compactMode;
    }

    public boolean useTabs() {
        return useTabs;
    }

    /**
     * Get the indent string for a given level.
     */
    public String getIndent(int level) {
        if (level <= 0) {
            return "";
        }

        if (useTabs) {
            return "\t".repeat(level);
        } else {
            return " ".repeat(indentSize * level);
        }
    }

    /**
     * Builder for creating custom PrettyPrintConfig instances.
     */
    public static class Builder {
        private int indentSize = 2;
        private int lineWidth = 80;
        private boolean compactMode = false;
        private boolean useTabs = false;

        public Builder indentSize(int indentSize) {
            if (indentSize < 0) {
                throw new IllegalArgumentException("Indent size must be non-negative");
            }
            this.indentSize = indentSize;
            return this;
        }

        public Builder lineWidth(int lineWidth) {
            if (lineWidth < 20) {
                throw new IllegalArgumentException("Line width must be at least 20");
            }
            this.lineWidth = lineWidth;
            return this;
        }

        public Builder compactMode(boolean compactMode) {
            this.compactMode = compactMode;
            return this;
        }

        public Builder useTabs(boolean useTabs) {
            this.useTabs = useTabs;
            return this;
        }

        public PrettyPrintConfig build() {
            return new PrettyPrintConfig(this);
        }
    }
}
