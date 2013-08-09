package webit.script;

/**
 *
 * @author Zqq
 */
public class EngineManager {

    private static Engine engine;

    static {
        engine = Engine.getEngine("/webitl-test.props", null);
    }

    public static Engine getEngine() {
        return engine;
    }
}
