package top.bogey.touch_tool.utils.tree;

import java.util.List;

public interface ITreeNodeData {
    List<? extends ITreeNodeData> getChildren();
}
