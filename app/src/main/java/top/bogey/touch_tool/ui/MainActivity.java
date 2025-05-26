package top.bogey.touch_tool.ui;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.bean.save.SettingSaver;
import top.bogey.touch_tool.databinding.ActivityMainBinding;
import top.bogey.touch_tool.service.TaskInfoSummary;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;

    public static Fragment getCurrentFragment() {
        MainActivity activity = MainApplication.getInstance().getActivity();
        if (activity == null) return null;
        Fragment navFragment = activity.getSupportFragmentManager().getPrimaryNavigationFragment();
        if (navFragment == null || !navFragment.isAdded()) return null;
        return navFragment.getChildFragmentManager().getPrimaryNavigationFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().setActivity(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        SettingSaver.getInstance().init(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        NavController controller = Navigation.findNavController(this, R.id.conView);
        NavigationUI.setupWithNavController(binding.menuView, controller);

        controller.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            int id = navDestination.getId();
            if (id == R.id.task || id == R.id.tool || id == R.id.setting) {
                showBottomNavigation();
            } else {
                hideBottomNavigation();
            }
        });

        TaskInfoSummary.getInstance().resetApps();
    }

    public void showBottomNavigation() {
        binding.menuView.setVisibility(View.VISIBLE);
    }

    public void hideBottomNavigation() {
        binding.menuView.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController controller = Navigation.findNavController(this, R.id.conView);
        return controller.navigateUp() || super.onSupportNavigateUp();
    }
}