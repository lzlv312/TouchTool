package top.bogey.touch_tool.ui.blueprint;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.ViewBlueprintBinding;
import top.bogey.touch_tool.ui.MainActivity;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.blueprint.history.HistoryManager;
import top.bogey.touch_tool.ui.blueprint.selecter.select_action.SelectActionDialog;

public class BlueprintView extends Fragment {
    private final Stack<Task> taskStack = new Stack<>();
    private final Map<String, HistoryManager> managers = new HashMap<>();

    private ViewBlueprintBinding binding;
    private HistoryManager history;
    private boolean needDelete = false;

    public static void tryPushStack(Task task) {
        Fragment fragment = MainActivity.getCurrentFragment();
        if (fragment instanceof BlueprintView blueprintView) {
            blueprintView.pushStack(task);
        }
    }

    public static void tryShowFloatingToolBar(boolean show) {
        Fragment fragment = MainActivity.getCurrentFragment();
        if (fragment instanceof BlueprintView blueprintView) {
            blueprintView.binding.floatingToolBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private final OnBackPressedCallback callback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            popStack();
        }
    };

    private Menu menu;
    private final MenuProvider menuProvider = new MenuProvider() {
        @Override
        public void onCreateMenu(@NonNull Menu currMenu, @NonNull MenuInflater menuInflater) {
            menu = currMenu;
            menuInflater.inflate(R.menu.menu_blueprint, currMenu);
            if (history != null) {
                menu.findItem(R.id.back).setEnabled(history.canBack());
                menu.findItem(R.id.forward).setEnabled(history.canForward());
            }
        }

        @Override
        public void onPrepareMenu(@NonNull Menu menu) {
            Task task = taskStack.peek();
            while (task.getParent() != null) {
                task = task.getParent();
            }
            MenuItem item = menu.findItem(R.id.taskDetailLog);
            item.setChecked(task.isDetailLog());
        }

        @Override
        public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.back) {
                if (history != null) history.back(binding.cardLayout);
                menu.findItem(R.id.back).setEnabled(history.canBack());
                menu.findItem(R.id.forward).setEnabled(history.canForward());
                return true;
            } else if (itemId == R.id.forward) {
                if (history != null) history.forward(binding.cardLayout);
                menu.findItem(R.id.back).setEnabled(history.canBack());
                menu.findItem(R.id.forward).setEnabled(history.canForward());
                return true;
            } else if (itemId == R.id.save) {
                binding.cardLayout.getTask().save();
                return true;
            } else if (itemId == R.id.taskRunningLog) {
                Task task = taskStack.peek();
                while (task.getParent() != null) {
                    task = task.getParent();
                }
                // todo 显示日志
                return true;
            } else if (itemId == R.id.taskDetailLog) {
                Task task = taskStack.peek();
                while (task.getParent() != null) {
                    task = task.getParent();
                }
                task.setDetailLog(!task.isDetailLog());
                task.save();
                menuItem.setChecked(task.isDetailLog());
                return true;
            } else if (itemId == R.id.taskCapture) {
                Bitmap bitmap = binding.cardLayout.takeTaskCapture();
                // todo 保存图片
                return true;
            }
            return false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments == null) throw new IllegalArgumentException();
        BlueprintViewArgs args = BlueprintViewArgs.fromBundle(arguments);
        Task task = Saver.getInstance().getTask(args.getTaskId());
        if (task == null) throw new IllegalArgumentException();

        binding = ViewBlueprintBinding.inflate(inflater, container, false);

        binding.toolBar.addMenuProvider(menuProvider, getViewLifecycleOwner());
        binding.toolBar.setNavigationOnClickListener(v -> {
            if (taskStack.size() > 1) {
                popStack();
            } else {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        binding.addButton.setOnClickListener(v -> new SelectActionDialog(requireContext(), taskStack.peek(), action -> {
            ActionCard card = binding.cardLayout.addCard(action);
            if (card == null) return;
            binding.cardLayout.initCardPos(card);
        }).show());

        binding.copyButton.setOnClickListener(v -> {

        });

        binding.deleteButton.setOnClickListener(v -> {
            if (needDelete) {
                for (ActionCard card : binding.cardLayout.selectedCards) {
                    binding.cardLayout.removeCard(card);
                }
                binding.cardLayout.selectedCards.clear();
                binding.floatingToolBar.setVisibility(View.GONE);
            } else {
                binding.deleteButton.setChecked(true);
                needDelete = true;
                binding.deleteButton.postDelayed(() -> {
                    binding.deleteButton.setChecked(false);
                    needDelete = false;
                }, 1500);
            }
        });

        pushStack(task);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return binding.getRoot();
    }

    public void pushStack(Task task) {
        if (task == null) return;

        if (!taskStack.empty()) taskStack.peek().save();

        taskStack.remove(task);
        taskStack.push(task);

        setTask(task);
    }

    public void popStack() {
        if (taskStack.empty()) return;
        Task task = taskStack.pop();
        task.save();

        if (!taskStack.empty()) {
            task = taskStack.peek();
            setTask(task);
        }
    }

    public void setTask(Task task) {
        history = managers.computeIfAbsent(task.getId(), s -> new HistoryManager());
        binding.cardLayout.setTask(task, history);

        if (menu != null) {
            menu.findItem(R.id.back).setEnabled(history.canBack());
            menu.findItem(R.id.forward).setEnabled(history.canForward());
        }

        binding.toolBar.setTitle(task.getTitle());

        callback.setEnabled(taskStack.size() > 1);
    }
}
