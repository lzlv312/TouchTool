package top.bogey.touch_tool.bean.pin.pin_objects;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.utils.GsonUtil;

public class PinNode extends PinObject {
    private NodeInfo nodeInfo;

    public PinNode() {
        super(PinType.NODE);
    }

    public PinNode(NodeInfo nodeInfo) {
        this();
        this.nodeInfo = nodeInfo;
    }

    public PinNode(JsonObject jsonObject) {
        super(jsonObject);
        nodeInfo = GsonUtil.getAsObject(jsonObject, "nodeInfo", NodeInfo.class, null);
    }

    @Override
    public void reset() {
        super.reset();
        nodeInfo = null;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinNode pinNode = (PinNode) o;

        return getNodeInfo() != null ? getNodeInfo().equals(pinNode.getNodeInfo()) : pinNode.getNodeInfo() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getNodeInfo() != null ? getNodeInfo().hashCode() : 0);
        return result;
    }
}
