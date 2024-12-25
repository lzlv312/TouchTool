package top.bogey.touch_tool.bean.pin.pin_objects;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Objects;

import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.utils.GsonUtil;

public class PinNode extends PinObject {
    private transient NodeInfo nodeInfo;

    public PinNode() {
        super(PinType.NODE);
    }

    public PinNode(NodeInfo nodeInfo) {
        this();
        this.nodeInfo = nodeInfo;
    }

    public PinNode(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void reset() {
        super.reset();
        nodeInfo = null;
    }

    @Override
    public void sync(PinBase value) {
        if (value instanceof PinNode pinNode) {
            nodeInfo = pinNode.nodeInfo;
        }
    }

    @NonNull
    @Override
    public String toString() {
        if (nodeInfo == null) return super.toString();
        if (nodeInfo.text == null) return super.toString();
        return nodeInfo.text;
    }

    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PinNode pinNode)) return false;
        if (!super.equals(object)) return false;

        return Objects.equals(getNodeInfo(), pinNode.getNodeInfo());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(getNodeInfo());
        return result;
    }
}
