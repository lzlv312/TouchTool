package top.bogey.touch_tool.bean.pin.pin_objects;

public enum PinType {
    NONE,

    // 特殊类型
    EXECUTE,
    ADD,
    PARAM,

    // 值类型
    OBJECT,

    LIST,
    MAP,

    STRING,
    NUMBER,
    BOOLEAN,

    VALUE_AREA,

    POINT,
    AREA,

    TOUCH,
    NODE,

    IMAGE,
    COLOR,

    APP,
    APPS,
    ;

    public int getGroup() {
        return switch (this) {
            case NONE, EXECUTE, ADD -> -1;
            case PARAM -> 0;
            case LIST, APPS -> 2;
            case MAP -> 3;
            default -> 1;
        };
    }
}
