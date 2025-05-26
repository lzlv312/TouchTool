package top.bogey.touch_tool.ui.blueprint;

import android.graphics.Bitmap;
import android.graphics.Point;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.action.Action;
import top.bogey.touch_tool.bean.action.start.StartAction;
import top.bogey.touch_tool.bean.save.Saver;
import top.bogey.touch_tool.bean.task.Task;
import top.bogey.touch_tool.databinding.ViewBlueprintBinding;
import top.bogey.touch_tool.ui.MainActivity;
import top.bogey.touch_tool.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool.ui.blueprint.history.HistoryManager;
import top.bogey.touch_tool.ui.tool.log.LogView;
import top.bogey.touch_tool.ui.blueprint.selecter.select_action.SelectActionDialog;
import top.bogey.touch_tool.utils.AppUtil;
import top.bogey.touch_tool.utils.DisplayUtil;

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
            MenuItem item = menu.findItem(R.id.taskDetailLog);
            item.setChecked(task.hasFlag(Task.FLAG_DEBUG));
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
                LogView logView = new LogView(requireContext(), task, false);
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.task_running_log)
                        .setView(logView)
                        .setPositiveButton(R.string.close, null)
                        .setNeutralButton(R.string.task_running_log_clear, (dialog, which) -> {
                            dialog.dismiss();
                            Saver.getInstance().clearLog(task.getId());
                        })
                        .show();
                DisplayUtil.setViewWidth(logView, ViewGroup.LayoutParams.MATCH_PARENT);
                DisplayUtil.setViewHeight(logView, ViewGroup.LayoutParams.WRAP_CONTENT);
                int px = (int) DisplayUtil.dp2px(requireContext(), 16);
                DisplayUtil.setViewMargin(logView, px, px, px, px);
                return true;
            } else if (itemId == R.id.taskDetailLog) {
                Task task = taskStack.peek();
                task.toggleFlag(Task.FLAG_DEBUG);
                task.save();
                menuItem.setChecked(task.hasFlag(Task.FLAG_DEBUG));
                return true;
            } else if (itemId == R.id.taskCapture) {
                Bitmap bitmap = binding.cardLayout.takeTaskCapture();
                ShapeableImageView imageView = new ShapeableImageView(requireContext());
                imageView.setImageBitmap(bitmap);

                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.task_capture)
                        .setView(imageView)
                        .setPositiveButton(R.string.save, (dialog, which) -> {
                            dialog.dismiss();
                            AppUtil.saveImage(requireContext(), bitmap);
                        })
                        .setNegativeButton(R.string.share_to_action, (dialog, which) -> {
                            dialog.dismiss();
                            AppUtil.shareImage(requireContext(), bitmap);
                        })
                        .setNeutralButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                        .show();

                Point size = DisplayUtil.getScreenSize(requireContext());
                DisplayUtil.setViewWidth(imageView, ViewGroup.LayoutParams.MATCH_PARENT);
                DisplayUtil.setViewHeight(imageView, size.y / 2);
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
                for (ActionCard card : new HashSet<>(binding.cardLayout.selectedCards)) {
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

        binding.sortButton.setOnClickListener(v -> {
            Task currTask = taskStack.peek();
            List<Action> startActions = currTask.getActions(StartAction.class);
            CardLayoutHelper.ActionArea actionArea = new CardLayoutHelper.ActionArea(binding.cardLayout, new ArrayList<>(), startActions);
            actionArea.arrange(binding.cardLayout, new Point(), null);
            binding.cardLayout.updateCardsPos();
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
