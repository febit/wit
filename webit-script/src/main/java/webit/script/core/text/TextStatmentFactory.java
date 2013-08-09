package webit.script.core.text;

import webit.script.Template;
import webit.script.core.ast.Statment;

/**
 *
 * @author Zqq
 */
public interface TextStatmentFactory {

    void startTemplate(Template template);

    void finishTemplate(Template template);

    Statment getTextStatment(Template template, String text, int line, int column);
}
