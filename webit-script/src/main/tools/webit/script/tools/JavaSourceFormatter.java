// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import jodd.io.FileUtil;
import jodd.io.findfile.FindFile;
import jodd.io.findfile.WildcardFindFile;
import jodd.util.StringUtil;

import java.io.File;
import java.io.IOException;

/**
 * Formats java sources.
 */
public class JavaSourceFormatter {

    private static final boolean WRITE_MODE = false;

    public static void main(String[] args) throws IOException {
        JavaSourceFormatter formatter = new JavaSourceFormatter();
        formatter.formatJava("F:\\Workspace\\webit-script");
    }

    public JavaSourceFormatter() {
        System.out.println("Java Source Formatter " + (WRITE_MODE ? "is ACTIVE." : "works in TEST mode."));
    }

    /**
     * Formats java files under provided source folder.
     */
    public void formatJava(String sourceRoot) throws IOException {
        System.out.println("*** format: " + sourceRoot);

        FindFile ff = new WildcardFindFile()
                .include("/webit-script/src/**/*.java")
                //.exclude("**/asm4/**/*.java","**/Lexer.java","**/Parser.java","**/Tokens.java")
                .setRecursive(true)
                .setIncludeDirs(false)
                .setMatchType(FindFile.Match.RELATIVE_PATH)
                .searchPath(sourceRoot);

        File f;
        int count = 0;
        while ((f = ff.nextFile()) != null) {
            boolean changed = format(f);

            if (changed) {
                count++;
            }
        }

        if (count != 0) {
            System.out.println(count + " changes");
        }
    }
    private String ruleName;

    /**
     * Formats java file.
     */
    @SuppressWarnings("ConstantConditions")
    private boolean format(File file) throws IOException {

        String filePath = file.getAbsolutePath();


        String originalContent = FileUtil.readString(file);
        String content = originalContent;
        boolean changed = false;

        // apply rules
        originalContent = content;
        content = checkCopyright(content, filePath);
        changed |= isChanged(content, originalContent, filePath);

        // the end, write changes
        if (changed && WRITE_MODE) {
            FileUtil.writeString(file, content);
        }
        return changed;
    }

    private boolean isChanged(String content, String originalContent, String filePath) {
        if (!originalContent.equals(content)) {
            System.out.println(ruleName + ": " + filePath);
            return true;
        }
        return false;
    }
// ---------------------------------------------------------------- rules
    /**
     * Copyright.
     */
    private static final String COPYRIGHT = "// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.\n";

    /**
     * Checks if there is a copyright.
     */
    private String checkCopyright(String content, String filePath) {
        ruleName = "copyright";

        // ignore
        if (filePath.contains("\\asm3\\")
                || filePath.contains("/asm3/")
                ||filePath.contains("/props/")
                ||filePath.contains("\\props\\")
                || filePath.endsWith("Parser.java")
                || filePath.endsWith("Lexer.java")
                || filePath.endsWith("Tokens.java")
                
                || content.contains("Jodd Team (jodd.org). All Rights Reserved.")) {
            return content;
        }

        content = StringUtil.trimLeft(content);

        if (!content.startsWith("package")) {
            int index = content.indexOf("\npackage");
            if (index != -1) {
                content = content.substring(index + 1);
            }
        }
        return COPYRIGHT  + content;
    }
}