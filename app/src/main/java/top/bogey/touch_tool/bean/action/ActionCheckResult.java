package top.bogey.touch_tool.bean.action;

import androidx.annotation.StringRes;

import java.util.Stack;

public class ActionCheckResult {
    private final Stack<Result> results = new Stack<>();

    public void addResult(ResultType type, @StringRes int msg) {
        results.push(new Result(type, msg));
    }

    public void merge(ActionCheckResult result) {
        results.addAll(result.results);
    }

    public boolean hasError() {
        return results.stream().anyMatch(result -> result.type == ResultType.ERROR);
    }

    public Result getError() {
        return results.stream().filter(result -> result.type == ResultType.ERROR).findFirst().orElse(null);
    }

    public boolean hasWarning() {
        return results.stream().anyMatch(result -> result.type == ResultType.WARNING);
    }

    public Result getWarning() {
        return results.stream().filter(result -> result.type == ResultType.WARNING).findFirst().orElse(null);
    }

    public Result getResult() {
        return results.stream().findFirst().orElse(null);
    }

    public Result getImportantResult() {
        Result error = getError();
        if (error == null) {
            return getWarning();
        }
        return error;
    }

    public record Result(ResultType type, @StringRes int msg) {
    }

    public enum ResultType {
        WARNING,
        ERROR
    }
}
