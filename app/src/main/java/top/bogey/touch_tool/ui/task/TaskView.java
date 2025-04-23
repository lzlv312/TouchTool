package top.bogey.touch_tool.ui.task;

import static top.bogey.touch_tool.ui.blueprint.selecter.select_action.SelectActionItemRecyclerViewAdapter.getTipsLinearLayout;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.other.Usage;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.save.TaskSaveListener;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.ViewTaskBinding;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.service.TaskListener;
import top.bogey.touch_tool.service.TaskRunnable;
import top.bogey.touch_tool.ui.MainActivity;
import top.bogey.touch_tool.ui.custom.EditTaskDialog;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.DisplayUtil;
import top.bogey.touch_tool.utils.listener.TextChangedListener;

public class TaskView extends Fragment implements TaskListener, TaskSaveListener {
    private ViewTaskBinding binding;

    boolean selecting = false;
    Set<String> selected = new HashSet<>();

    private TaskPageViewAdapter adapter;

    private final MenuProvider menuProvider = new MenuProvider() {

        @Override
        public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            menuInflater.inflate(R.menu.menu_task, menu);
        }

        @Override
        public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
            MainActivity activity = (MainActivity) requireActivity();
            if (menuItem.getItemId() == R.id.importTask) {

                return true;
            } else if (menuItem.getItemId() == R.id.exportTask) {
                if (selecting) {

                } else {

                }
                return true;
            }
            return false;
        }
    };

    private final OnBackPressedCallback callback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            unselectAll();
            hideBottomBar();
        }
    };

    @Override
    public void onDestroyView() {
        Saver.getInstance().removeListener(this);
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null && service.isEnabled()) service.removeListener(this);
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        binding = ViewTaskBinding.inflate(inflater, container, false);

        binding.toolBar.addMenuProvider(menuProvider, getViewLifecycleOwner());

        Saver.getInstance().addListener(this);
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null && service.isEnabled()) service.addListener(this);

        binding.searchEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                binding.clean.setVisibility(s.length() == 0 ? View.GONE : View.VISIBLE);
                resetTags();
            }
        });

        binding.clean.setOnClickListener(v -> binding.searchEdit.setText(""));

        adapter = new TaskPageViewAdapter(this);
        binding.tasksBox.setAdapter(adapter);
        new TabLayoutMediator(binding.tabBox, binding.tasksBox, (tab, position) -> tab.setText(adapter.tags.get(position))).attach();
        resetTags();

        binding.selectAllButton.setOnClickListener(v -> selectAll());

        binding.deleteButton.setOnClickListener(v -> {
            List<Usage> usages = new ArrayList<>();
            for (String id : selected) {
                usages.addAll(Saver.getInstance().getTaskUses(id));
            }
            if (!usages.isEmpty()) {
                LinearLayout linearLayout = getTipsLinearLayout(requireContext(), usages, R.string.task_delete_tips);
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.remove_task)
                        .setView(linearLayout)
                        .setPositiveButton(R.string.enter, null)
                        .setNegativeButton(R.string.force_delete, (dialog, which) -> {
                            for (String id : selected) {
                                Saver.getInstance().removeTask(id);
                            }
                            hideBottomBar();
                        })
                        .show();

                int px = (int) DisplayUtil.dp2px(requireContext(), 32);
                DisplayUtil.setViewMargin(linearLayout, px, px / 2, px, px / 2);
            } else {
                AppUtil.showDialog(requireContext(), R.string.remove_tips, result -> {
                    if (result) {
                        for (String id : selected) {
                            Saver.getInstance().removeTask(id);
                        }
                        hideBottomBar();
                    }
                });
            }
        });

        binding.exportButton.setOnClickListener(v -> {
            unselectAll();
            hideBottomBar();
        });

        binding.moveButton.setOnClickListener(v -> {
            TaskTagListView tagListView = new TaskTagListView(this);
            tagListView.show(getParentFragmentManager(), null);
        });

        binding.copyButton.setOnClickListener(v -> {
            selected.forEach(id -> {
                Task task = Saver.getInstance().getTask(id);
                Task copy = task.newCopy();
                copy.setTitle(getString(R.string.copy_title, task.getTitle()));
                copy.save();
            });

            unselectAll();
            hideBottomBar();
        });

        binding.addButton.setOnClickListener(v -> {
            String currentTag = getCurrentTag();
            Task task = new Task();
            task.addTag(currentTag);
            EditTaskDialog dialog = new EditTaskDialog(requireContext(), task);
            dialog.setTitle(R.string.task_add);
            dialog.setCallback(result -> {
                if (result) task.save();
            });
            dialog.show();
        });

        return binding.getRoot();
    }

    public void resetTags() {
        Editable text = binding.searchEdit.getText();
        if (text != null && text.length() > 0) {
            adapter.search(text.toString());
        } else {
            List<String> tags = Saver.getInstance().getTaskTags();
            adapter.setTags(tags);
        }
    }

    public String getCurrentTag() {
        TabLayout.Tab tab = binding.tabBox.getTabAt(binding.tabBox.getSelectedTabPosition());
        if (tab == null || tab.getText() == null) return null;
        return tab.getText().toString();
    }

    public void showBottomBar() {
        MainApplication.getInstance().getActivity().hideBottomNavigation();

        binding.addButton.hide();
        binding.bottomBar.setVisibility(View.VISIBLE);

        selecting = true;
        callback.setEnabled(true);
    }

    public void hideBottomBar() {
        MainApplication.getInstance().getActivity().showBottomNavigation();

        binding.addButton.show();
        binding.bottomBar.setVisibility(View.GONE);

        selecting = false;
        callback.setEnabled(false);
    }

    public void selectAll() {
        TabLayout.Tab tab = binding.tabBox.getTabAt(binding.tabBox.getSelectedTabPosition());
        if (tab == null || tab.getText() == null) return;

        String tag = tab.getText().toString();
        List<Task> tasks = Saver.getInstance().getTasks(tag);

        boolean flag = true;
        if (selected.size() == tasks.size()) {
            boolean matched = true;
            for (Task task : tasks) {
                if (!selected.contains(task.getId())) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                flag = false;
            }
        }

        if (flag) {
            tasks.forEach(task -> selected.add(task.getId()));
            adapter.notifyItemChanged(binding.tabBox.getSelectedTabPosition());
        } else {
            unselectAll();
        }
    }

    public void unselectAll() {
        selected.clear();
        adapter.notifyItemChanged(binding.tabBox.getSelectedTabPosition());
    }

    @Override
    public void onCreate(Task task) {
        resetTags();
    }

    @Override
    public void onUpdate(Task task) {
        resetTags();
    }

    @Override
    public void onRemove(Task task) {
        resetTags();
    }

    @Override
    public void onStart(TaskRunnable runnable) {

    }

    @Override
    public void onExecute(TaskRunnable runnable, Action action, int progress) {

    }

    @Override
    public void onCalculate(TaskRunnable runnable, Action action) {

    }

    @Override
    public void onFinish(TaskRunnable runnable) {

    }
}
