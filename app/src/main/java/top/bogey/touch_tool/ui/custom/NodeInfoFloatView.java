package top.bogey.touch_tool.ui.custom;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.other.NodeInfo;
import top.bogey.touch_tool.bean.pin.pin_objects.pin_string.PinNodePathString;
import top.bogey.touch_tool.databinding.FloatNodeInfoBinding;
import top.bogey.touch_tool.databinding.FloatNodeInfoItemBinding;
import top.bogey.touch_tool.utils.EAnchor;
import top.bogey.touch_tool.utils.callback.ResultCallback;
import top.bogey.touch_tool.utils.float_window_manager.FloatInterface;
import top.bogey.touch_tool.utils.float_window_manager.FloatWindow;

@SuppressLint("ViewConstructor")
public class NodeInfoFloatView extends FrameLayout implements FloatInterface {
    private final ResultCallback<NodeInfo> callback;
    private final FloatNodeInfoBinding binding;
    private final NodeInfoFloatViewAdapter adapter;

    private NodeInfo nodeInfo;

    public static void showInfo(NodeInfo nodeInfo, ResultCallback<NodeInfo> callback) {
        KeepAliveFloatView keepView = (KeepAliveFloatView) FloatWindow.getView(KeepAliveFloatView.class.getName());
        if (keepView == null) return;
        new Handler(Looper.getMainLooper()).post(() -> {
            NodeInfoFloatView nodeInfoView = (NodeInfoFloatView) FloatWindow.getView(NodeInfoFloatView.class.getName());
            if (nodeInfoView == null) {
                nodeInfoView = new NodeInfoFloatView(keepView.getContext(), callback);
                nodeInfoView.show();
            }
            nodeInfoView.innerShowToast(nodeInfo);
        });
    }

    private NodeInfoFloatView(@NonNull Context context, ResultCallback<NodeInfo> callback) {
        super(context);
        this.callback = callback;
        binding = FloatNodeInfoBinding.inflate(LayoutInflater.from(context), this, true);
        adapter = new NodeInfoFloatViewAdapter();
        binding.contentBox.setAdapter(adapter);

        binding.parentButton.setOnClickListener(v -> {
            if (nodeInfo == null || nodeInfo.parent == null) return;
            innerShowToast(nodeInfo.parent);
        });
        binding.childButton.setOnClickListener(v -> {
            if (nodeInfo == null || nodeInfo.children.isEmpty()) return;
            innerShowToast(nodeInfo.children.get(0));
        });
        binding.preButton.setOnClickListener(v -> {
            if (nodeInfo == null || nodeInfo.parent == null || nodeInfo.index <= 1) return;
            innerShowToast(nodeInfo.parent.children.get(nodeInfo.index - 2));
        });
        binding.nextButton.setOnClickListener(v -> {
            if (nodeInfo == null || nodeInfo.parent == null || nodeInfo.index == nodeInfo.parent.children.size()) return;
            innerShowToast(nodeInfo.parent.children.get(nodeInfo.index));
        });

        binding.closeButton.setOnClickListener(v -> dismiss());
    }

    private void innerShowToast(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
        if (nodeInfo == null) return;
        binding.parentButton.setVisibility(nodeInfo.parent == null ? INVISIBLE : VISIBLE);
        binding.childButton.setVisibility(nodeInfo.children.isEmpty() ? INVISIBLE : VISIBLE);
        binding.preButton.setVisibility(nodeInfo.parent == null || nodeInfo.index <= 1 ? INVISIBLE : VISIBLE);
        binding.nextButton.setVisibility(nodeInfo.parent == null || nodeInfo.index == nodeInfo.parent.children.size() ? INVISIBLE : VISIBLE);

        List<NodeInfoContent> contents = new ArrayList<>();
        String[] array = getResources().getStringArray(R.array.node_info_type);
        for (int i = 0; i < array.length; i++) {
            String content = switch (i) {
                case 0 -> nodeInfo.clazz;
                case 1 -> nodeInfo.id != null ? nodeInfo.id : "";
                case 2 -> nodeInfo.toString();
                case 3 -> nodeInfo.text != null ? nodeInfo.text : "";
                case 4 -> nodeInfo.desc != null ? nodeInfo.desc : "";
                case 5 -> String.valueOf(nodeInfo.index);
                case 6 -> String.valueOf(nodeInfo.usable);
                case 7 -> String.valueOf(nodeInfo.visible);
                case 8 -> getContext().getString(R.string.area_left) + ": " + nodeInfo.area.left + ", "
                        + getContext().getString(R.string.area_top) + ": " + nodeInfo.area.top + ", "
                        + getContext().getString(R.string.area_right) + ": " + nodeInfo.area.right + ", "
                        + getContext().getString(R.string.area_bottom) + ": " + nodeInfo.area.bottom;
                case 9 -> nodeInfo.area.width() + " × " + nodeInfo.area.height();
                case 10 -> nodeInfo.parent == null ? "" : nodeInfo.parent.toString();
                case 11 -> String.valueOf(!nodeInfo.children.isEmpty());
                default -> "";
            };
            String copyValue = switch (i) {
                case 2 -> {
                    PinNodePathString pathString = new PinNodePathString();
                    pathString.setValue(nodeInfo);
                    yield pathString.getValue();
                }
                case 7 -> nodeInfo.area.toString();
                case 10 -> {
                    if (nodeInfo.parent == null) yield "";
                    PinNodePathString pathString = new PinNodePathString();
                    pathString.setValue(nodeInfo.parent);
                    yield pathString.getValue();
                }
                default -> content;
            };
            contents.add(new NodeInfoContent(array[i], content, copyValue));
        }
        adapter.setContents(contents);
        callback.onResult(nodeInfo);
    }

    @Override
    public void show() {
        FloatWindow.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(NodeInfoFloatView.class.getName())
                .setLocation(EAnchor.CENTER, 0, 0)
                .setSpecial(true)
                .show();
    }

    @Override
    public void dismiss() {
        FloatWindow.dismiss(NodeInfoFloatView.class.getName());
    }

    private static class NodeInfoFloatViewAdapter extends RecyclerView.Adapter<NodeInfoFloatViewViewHolder> {
        private List<NodeInfoContent> contents = new ArrayList<>();

        @NonNull
        @Override
        public NodeInfoFloatViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NodeInfoFloatViewViewHolder(FloatNodeInfoItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull NodeInfoFloatViewViewHolder holder, int position) {
            holder.refresh(contents.get(position));
        }

        @Override
        public int getItemCount() {
            return contents.size();
        }

        public void setContents(List<NodeInfoContent> contents) {
            int size = this.contents.size();
            this.contents = contents;
            if (size == 0) notifyItemRangeInserted(0, contents.size());
            else notifyItemRangeChanged(0, size);
        }
    }

    private static class NodeInfoFloatViewViewHolder extends RecyclerView.ViewHolder {
        private final FloatNodeInfoItemBinding binding;
        private NodeInfoContent content;

        public NodeInfoFloatViewViewHolder(@NonNull FloatNodeInfoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.copyButton.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(itemView.getContext().getString(R.string.copy_ttp_url), content.copyValue);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(itemView.getContext(), R.string.copy_tips, Toast.LENGTH_SHORT).show();
            });
        }

        public void refresh(NodeInfoContent content) {
            this.content = content;
            binding.title.setText(content.key);
            binding.content.setText(content.value);
        }
    }

    private record NodeInfoContent(String key, String value, String copyValue) {
    }
}
